package com.lumi.service.payment.impl;

import com.lumi.config.SePayProperties;
import com.lumi.domain.*;
import com.lumi.domain.enumeration.AccessDurationUnit;
import com.lumi.domain.enumeration.OrderStatus;
import com.lumi.domain.enumeration.SePayWebhookStatus;
import com.lumi.repository.*;
import com.lumi.security.AuthoritiesConstants;
import com.lumi.service.dto.SePayWebhookRequestDTO;
import com.lumi.service.finance.FinanceService;
import com.lumi.service.payment.SePayWebhookProcessor;
import java.math.BigDecimal;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SePayWebhookProcessorImpl implements SePayWebhookProcessor {

    static final Pattern ORDER_CODE = Pattern.compile("(?<![A-Z0-9])LUMI([1-9][0-9]*)(?![A-Z0-9])", Pattern.CASE_INSENSITIVE);
    static final ZoneId VIETNAM = ZoneId.of("Asia/Ho_Chi_Minh");
    SePayWebhookEventRepository eventRepository;
    CourseOrderRepository orderRepository;
    UserRepository userRepository;
    ParentStudentLinkRepository linkRepository;
    PaymentRepository paymentRepository;
    CourseEnrollmentRepository enrollmentRepository;
    FinanceService financeService;
    SePayProperties properties;

    @Override
    @Transactional
    public void process(SePayWebhookRequestDTO request, Instant transactionAt, Instant receivedAt) {
        String eventId = request.getId().toString();
        int inserted = eventRepository.reserveEvent(
            eventId,
            request.getReferenceCode(),
            request.getAccountNumber(),
            request.getTransferType(),
            request.getTransferAmount(),
            transactionAt,
            receivedAt
        );
        if (inserted == 0) return; // Transaction trước đã commit: trả ACK(xác nhận đã nhận và xử lý thông tin), không xử lý lại.

        SePayWebhookEvent event = eventRepository.findByProviderEventId(eventId).orElseThrow();
        if ("out".equals(request.getTransferType())) {
            event.setProcessingStatus(SePayWebhookStatus.IGNORED);
            event.setProcessingReason("OUTGOING_TRANSFER");
            return;
        }
        if (!isExpectedAccount(request) || !isExpectedBank(request.getGateway())) {
            requireReview(event, "RECEIVING_ACCOUNT_MISMATCH");
            return;
        }

        Long orderId = findOrderId(request);
        if (orderId == null) {
            requireReview(event, "ORDER_CODE_INVALID");
            return;
        }
        var participants = orderRepository.findPaymentParticipants(orderId).orElse(null);
        if (participants == null) {
            requireReview(event, "ORDER_NOT_FOUND");
            return;
        }

        // Cùng thứ tự với tạo đơn: tài khoản theo ID tăng dần → liên kết → đơn.
        lockAccounts(participants.getBuyerId(), participants.getStudentId());
        List<ParentStudentLink> links = linkRepository.findForPurchaseAccess(participants.getBuyerId(), participants.getStudentId());
        CourseOrder order = orderRepository.findForPaymentUpdate(orderId).orElseThrow();
        event.setOrder(order);

        String reviewReason = getReviewReason(order, request, transactionAt, receivedAt, links, participants);
        if (reviewReason != null) {
            requireReview(event, reviewReason);
            return;
        }

        Instant accessExpiresAt = calculateAccessExpiry(order, transactionAt);
        if (accessExpiresAt == null) {
            requireReview(event, "ORDER_DURATION_INVALID");
            return;
        }
        Payment payment = createPayment(order, request, transactionAt, receivedAt);
        createEnrollment(order, transactionAt, accessExpiresAt, receivedAt);
        markOrderPaid(order, transactionAt, accessExpiresAt, receivedAt);
        financeService.creditSale(order, transactionAt);

        event.setPayment(payment);
        event.setProcessingStatus(SePayWebhookStatus.PROCESSED);
        event.setProcessingReason(null);
    }

    private void lockAccounts(Long buyerId, Long studentId) {
        userRepository.findForPurchaseAccess(Math.min(buyerId, studentId)).orElseThrow();
        if (!buyerId.equals(studentId)) {
            userRepository.findForPurchaseAccess(Math.max(buyerId, studentId)).orElseThrow();
        }
    }

    private String getReviewReason(
        CourseOrder order,
        SePayWebhookRequestDTO request,
        Instant transactionAt,
        Instant receivedAt,
        List<ParentStudentLink> links,
        CourseOrderRepository.PaymentParticipants participants
    ) {
        if (
            order.getBuyer() == null ||
            order.getStudent() == null ||
            order.getCourse() == null ||
            !order.getBuyer().getId().equals(participants.getBuyerId()) ||
            !order.getStudent().getId().equals(participants.getStudentId())
        ) return "ORDER_DATA_INVALID";
        if (order.getStatus() == OrderStatus.PAID) return "ORDER_ALREADY_PAID";
        if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.EXPIRED) return "ORDER_NOT_PAYABLE";
        if (
            order.getFinalPrice() == null ||
            order.getFinalPrice().signum() <= 0 ||
            request.getTransferAmount().compareTo(order.getFinalPrice()) != 0
        ) return "AMOUNT_MISMATCH";
        if (
            order.getCreatedAt() == null ||
            order.getExpiresAt() == null ||
            transactionAt.isBefore(order.getCreatedAt().truncatedTo(ChronoUnit.SECONDS)) ||
            transactionAt.isAfter(receivedAt.truncatedTo(ChronoUnit.SECONDS))
        ) return "TRANSACTION_TIME_INVALID";
        if (!transactionAt.isBefore(order.getExpiresAt())) return "PAYMENT_AFTER_DEADLINE";

        boolean linked = links.stream().anyMatch(link -> "LINKED".equals(link.getStatus()) && link.getRevokedAt() == null);
        if (!linked || order.getBuyer().getId().equals(order.getStudent().getId())) return "PARENT_LINK_INVALID";
        if (
            !isActiveWithRole(order.getBuyer(), AuthoritiesConstants.PARENT) ||
            !isActiveWithRole(order.getStudent(), AuthoritiesConstants.STUDENT)
        ) return "ACCOUNT_NOT_ELIGIBLE";
        if (enrollmentRepository.existsByStudentIdAndCourseId(order.getStudent().getId(), order.getCourse().getId())) {
            return "ENROLLMENT_ALREADY_EXISTS";
        }
        if (
            !orderRepository
                .findByStudentIdAndCourseIdAndOrderTypeAndStatus(
                    order.getStudent().getId(),
                    order.getCourse().getId(),
                    "COURSE",
                    OrderStatus.PAID
                )
                .isEmpty()
        ) return "COURSE_ALREADY_PAID";
        return null;
    }

    private boolean isActiveWithRole(User user, String role) {
        return (
            user.isActivated() &&
            user
                .getAuthorities()
                .stream()
                .anyMatch(authority -> role.equals(authority.getName()))
        );
    }

    private boolean isExpectedAccount(SePayWebhookRequestDTO request) {
        String configured = properties.getAccountNumber().trim();
        return configured.equals(request.getAccountNumber()) || configured.equals(request.getSubAccount());
    }

    private boolean isExpectedBank(String gateway) {
        String actual = normalizeBank(gateway);
        return (
            actual.equals(normalizeBank(properties.getBankCode())) ||
            (StringUtils.hasText(properties.getBankName()) && actual.equals(normalizeBank(properties.getBankName())))
        );
    }

    private String normalizeBank(String value) {
        String bank = value.trim().toUpperCase(Locale.ROOT);
        if (bank.equals("MB") || bank.equals("MBBANK") || bank.equals("970422")) return "MB";
        if (bank.equals("VCB") || bank.equals("VIETCOMBANK") || bank.equals("970436")) return "VCB";
        return bank;
    }

    private Long findOrderId(SePayWebhookRequestDTO request) {
        var matcher = ORDER_CODE.matcher(request.getContent());
        Long orderId = null;
        try {
            while (matcher.find()) {
                long candidate = Long.parseLong(matcher.group(1));
                if (orderId != null && orderId != candidate) return null;
                orderId = candidate;
            }
        } catch (NumberFormatException exception) {
            return null;
        }
        if (orderId == null) return null;
        if (StringUtils.hasText(request.getCode()) && !("LUMI" + orderId).equalsIgnoreCase(request.getCode().trim())) return null;
        return orderId;
    }

    private Instant calculateAccessExpiry(CourseOrder order, Instant startsAt) {
        try {
            if (
                order.getAccessDurationValue() == null || order.getAccessDurationValue() <= 0 || order.getAccessDurationUnit() == null
            ) return null;
            var start = startsAt.atZone(VIETNAM);
            return switch (AccessDurationUnit.valueOf(order.getAccessDurationUnit())) {
                case DAY -> start.plusDays(order.getAccessDurationValue()).toInstant();
                case MONTH -> start.plusMonths(order.getAccessDurationValue()).toInstant();
                case YEAR -> start.plusYears(order.getAccessDurationValue()).toInstant();
            };
        } catch (IllegalArgumentException | DateTimeException | ArithmeticException exception) {
            return null;
        }
    }

    private Payment createPayment(CourseOrder order, SePayWebhookRequestDTO request, Instant transactionAt, Instant receivedAt) {
        Payment payment = new Payment();
        payment.setGateway("SEPAY");
        payment.setTransactionNo("SEPAY:" + request.getId());
        payment.setProviderEventId(request.getId().toString());
        payment.setPaymentReference(request.getReferenceCode());
        payment.setOrder(order);
        payment.setAmount(request.getTransferAmount());
        payment.setStatus("SUCCESS");
        payment.setPaidAt(transactionAt);
        payment.setReceivedAt(receivedAt);
        return paymentRepository.saveAndFlush(payment); // Chỉ INSERT; không sửa payment đã ghi.
    }

    private void createEnrollment(CourseOrder order, Instant startsAt, Instant expiresAt, Instant receivedAt) {
        CourseEnrollment enrollment = new CourseEnrollment();
        enrollment.setStudent(order.getStudent());
        enrollment.setCourse(order.getCourse());
        enrollment.setStatus(expiresAt.isAfter(receivedAt) ? "ACTIVE" : "EXPIRED");
        enrollment.setProgressPercent(BigDecimal.ZERO);
        enrollment.setEnrolledAt(startsAt);
        enrollment.setExpiresAt(expiresAt);
        enrollment.setUpdatedAt(receivedAt);
        enrollmentRepository.saveAndFlush(enrollment);
    }

    private void markOrderPaid(CourseOrder order, Instant paidAt, Instant expiresAt, Instant receivedAt) {
        order.setStatus(OrderStatus.PAID);
        order.setPaidAt(paidAt);
        order.setEntitlementStartsAt(paidAt);
        order.setEntitlementExpiresAt(expiresAt);
        order.setEntitlementStatus(expiresAt.isAfter(receivedAt) ? "ACTIVE" : "EXPIRED");
        order.setUpdatedAt(receivedAt);
    }

    private void requireReview(SePayWebhookEvent event, String reason) {
        event.setProcessingStatus(SePayWebhookStatus.REVIEW_REQUIRED);
        event.setProcessingReason(reason);
    }
}
