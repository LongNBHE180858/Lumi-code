package com.lumi.service.finance;

import com.lumi.domain.*;
import com.lumi.domain.enumeration.OrderStatus;
import com.lumi.domain.enumeration.TransactionType;
import com.lumi.repository.*;
import com.lumi.security.SecurityUtils;
import com.lumi.service.finance.FinanceDTOs.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class FinanceService {

    private static final ZoneId ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final Duration HOLD = Duration.ofDays(7);
    private static final String REFUND_DETAILS_FORMAT = "lumi-refund-details-v1";
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final UserRepository users;
    private final InstructorWalletRepository wallets;
    private final WalletTransactionRepository transactions;
    private final PayoutRequestRepository payouts;
    private final RefundRepository refunds;
    private final CourseOrderRepository orders;
    private final PaymentRepository payments;
    private final CourseEnrollmentRepository enrollments;
    private final AuditLogRepository audits;

    private User currentUser() {
        String login = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> error(HttpStatus.UNAUTHORIZED, "Cần đăng nhập"));
        return users.findOneByLogin(login).orElseThrow(() -> error(HttpStatus.UNAUTHORIZED, "Không tìm thấy tài khoản"));
    }

    private ResponseStatusException error(HttpStatus status, String message) {
        return new ResponseStatusException(status, message);
    }

    private BigDecimal money(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private void change(InstructorWallet wallet, BigDecimal pending, BigDecimal available) {
        BigDecimal nextPending = money(wallet.getPendingAmount()).add(pending);
        BigDecimal nextAvailable = money(wallet.getAvailableAmount()).add(available);
        if (nextPending.signum() < 0 || nextAvailable.signum() < 0) throw error(HttpStatus.CONFLICT, "Số dư ví không đủ");
        wallet.setPendingAmount(nextPending);
        wallet.setAvailableAmount(nextAvailable);
        wallet.setRowVersion((wallet.getRowVersion() == null ? 0 : wallet.getRowVersion()) + 1);
        wallets.save(wallet);
    }

    private WalletTransaction entry(
        InstructorWallet wallet,
        TransactionType type,
        BigDecimal amount,
        String status,
        String bucket,
        String key,
        Instant at,
        CourseOrder order,
        PayoutRequest payout,
        Refund refund
    ) {
        WalletTransaction tx = new WalletTransaction();
        tx.setWallet(wallet);
        tx.setType(type);
        tx.setAmount(amount);
        tx.setStatus(status);
        tx.setBalanceBucket(bucket);
        tx.setIdempotencyKey(key);
        tx.setOccurredAt(at);
        tx.setOrder(order);
        tx.setPayoutRequest(payout);
        tx.setRefund(refund);
        return transactions.save(tx);
    }

    @Transactional
    public void creditSale(CourseOrder order, Instant paidAt) {
        if (transactions.findByIdempotencyKey("sale:" + order.getId()).isPresent()) return;
        User instructor = order.getCourse().getAuthor();
        if (instructor == null || order.getInstructorAmount() == null || order.getInstructorAmount().signum() < 0) throw error(
            HttpStatus.CONFLICT,
            "Thiếu thông tin doanh thu giảng viên"
        );
        InstructorWallet wallet = wallets.lockByInstructorId(instructor.getId()).orElseGet(() -> {
            InstructorWallet created = new InstructorWallet();
            created.setInstructor(instructor);
            created.setPendingAmount(BigDecimal.ZERO);
            created.setAvailableAmount(BigDecimal.ZERO);
            created.setRowVersion(0L);
            return wallets.saveAndFlush(created);
        });
        change(wallet, order.getInstructorAmount(), BigDecimal.ZERO);
        entry(
            wallet,
            TransactionType.SALE,
            order.getInstructorAmount(),
            "PENDING",
            "PENDING",
            "sale:" + order.getId(),
            paidAt,
            order,
            null,
            null
        );
    }

    @Scheduled(fixedDelay = 300000)
    @Transactional
    public void releaseEligibleSales() {
        Instant now = Instant.now();
        for (Payment payment : payments.findUncreditedSePaySales(PageRequest.of(0, 100))) {
            creditSale(payment.getOrder(), payment.getPaidAt());
        }
        for (WalletTransaction sale : transactions.findByTypeAndStatusAndOccurredAtBefore(
            TransactionType.SALE,
            "PENDING",
            now.minus(HOLD)
        )) {
            InstructorWallet wallet = wallets.lockById(sale.getWallet().getId()).orElseThrow();
            if (refunds.existsByOrderIdAndStatusIn(sale.getOrder().getId(), List.of("PENDING", "APPROVED"))) continue;
            if (transactions.findByIdempotencyKey("release:" + sale.getId() + ":available").isPresent()) continue;
            BigDecimal reversed = transactions
                .findAllByOrderIdAndType(sale.getOrder().getId(), TransactionType.REFUND)
                .stream()
                .map(WalletTransaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .abs();
            BigDecimal releasable = sale.getAmount().subtract(reversed);
            if (releasable.signum() > 0) {
                change(wallet, releasable.negate(), releasable);
                entry(
                    wallet,
                    TransactionType.ADJUSTMENT,
                    releasable.negate(),
                    "POSTED",
                    "PENDING",
                    "release:" + sale.getId() + ":pending",
                    now,
                    sale.getOrder(),
                    null,
                    null
                );
                entry(
                    wallet,
                    TransactionType.ADJUSTMENT,
                    releasable,
                    "POSTED",
                    "AVAILABLE",
                    "release:" + sale.getId() + ":available",
                    now,
                    sale.getOrder(),
                    null,
                    null
                );
            }
            sale.setStatus("RELEASED");
        }
    }

    @Transactional(readOnly = true)
    public WalletView wallet() {
        InstructorWallet wallet = wallets.findByInstructorId(currentUser().getId()).orElse(null);
        return wallet == null
            ? new WalletView(BigDecimal.ZERO, BigDecimal.ZERO)
            : new WalletView(money(wallet.getPendingAmount()), money(wallet.getAvailableAmount()));
    }

    @Transactional(readOnly = true)
    public List<TransactionView> transactions() {
        return transactions
            .findByWalletInstructorIdOrderByOccurredAtDesc(currentUser().getId())
            .stream()
            .map(t ->
                new TransactionView(
                    t.getId(),
                    t.getType().name(),
                    t.getAmount(),
                    t.getStatus(),
                    t.getBalanceBucket(),
                    t.getOccurredAt(),
                    t.getOrder() == null ? null : t.getOrder().getId()
                )
            )
            .toList();
    }

    private PayoutView view(PayoutRequest p) {
        return new PayoutView(
            p.getId(),
            p.getWallet().getInstructor().getLogin(),
            p.getAmount(),
            p.getBankCode(),
            p.getBankAccount(),
            p.getAccountHolderName(),
            p.getStatus(),
            p.getCreatedAt(),
            p.getProcessedAt(),
            p.getRejectionReason(),
            p.getTransferReference()
        );
    }

    @Transactional(readOnly = true)
    public List<PayoutView> myPayouts() {
        return payouts.findByWalletInstructorIdOrderByCreatedAtDesc(currentUser().getId()).stream().map(this::view).toList();
    }

    @Transactional(readOnly = true)
    public List<PayoutView> allPayouts() {
        return payouts.findAllByOrderByCreatedAtDesc().stream().map(this::view).toList();
    }

    @Transactional
    public PayoutView requestPayout(PayoutCreate request) {
        User actor = currentUser();
        InstructorWallet wallet = wallets
            .lockByInstructorId(actor.getId())
            .orElseThrow(() -> error(HttpStatus.BAD_REQUEST, "Chưa có số dư ví"));
        Instant now = Instant.now();
        ZonedDateTime start = now.atZone(ZONE).toLocalDate().atStartOfDay(ZONE);
        if (
            payouts.existsByWalletIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
                wallet.getId(),
                start.toInstant(),
                start.plusDays(1).toInstant()
            )
        ) throw error(HttpStatus.CONFLICT, "Đã gửi yêu cầu rút tiền hôm nay");
        if (request.amount().compareTo(money(wallet.getAvailableAmount())) > 0) throw error(
            HttpStatus.BAD_REQUEST,
            "Số dư khả dụng không đủ"
        );
        PayoutRequest payout = new PayoutRequest();
        payout.setWallet(wallet);
        payout.setAmount(request.amount());
        payout.setBankCode(request.bankCode().trim());
        payout.setBankAccount(request.bankAccount().trim());
        payout.setAccountHolderName(request.accountHolderName().trim());
        payout.setStatus("PENDING");
        payout.setCreatedAt(now);
        payout.setUpdatedAt(now);
        payouts.saveAndFlush(payout);
        change(wallet, BigDecimal.ZERO, request.amount().negate());
        entry(
            wallet,
            TransactionType.PAYOUT,
            request.amount().negate(),
            "RESERVED",
            "AVAILABLE",
            "payout:" + payout.getId() + ":reserve",
            now,
            null,
            payout,
            null
        );
        return view(payout);
    }

    private void audit(User actor, String action, String target, Long id, String detail) {
        AuditLog log = new AuditLog();
        log.setActor(actor);
        log.setAction(action);
        log.setTargetEntity(target);
        log.setTargetId(id.toString());
        log.setDetails(detail);
        log.setOccurredAt(Instant.now());
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes) {
            String ip = attributes.getRequest().getRemoteAddr();
            log.setIpAddress(ip == null ? null : ip.substring(0, Math.min(ip.length(), 50)));
            String agent = attributes.getRequest().getHeader("User-Agent");
            log.setDetails(
                "Device: " +
                    (agent == null ? "Unknown" : agent.substring(0, Math.min(agent.length(), 200))) +
                    "; Details: " +
                    (detail == null ? "" : detail)
            );
        }
        audits.save(log);
    }

    @Transactional
    public PayoutView decidePayout(Long id, Decision decision) {
        User admin = currentUser();
        PayoutRequest payout = payouts.lockById(id).orElseThrow(() -> error(HttpStatus.NOT_FOUND, "Không tìm thấy yêu cầu"));
        if (!"PENDING".equals(payout.getStatus())) throw error(HttpStatus.CONFLICT, "Yêu cầu đã được xử lý");
        Instant now = Instant.now();
        if ("APPROVE".equals(decision.action())) {
            payout.setStatus("APPROVED");
        } else if ("REJECT".equals(decision.action()) && decision.reason() != null && !decision.reason().isBlank()) {
            InstructorWallet wallet = wallets.lockById(payout.getWallet().getId()).orElseThrow();
            payout.setStatus("REJECTED");
            payout.setRejectionReason(decision.reason().trim());
            change(wallet, BigDecimal.ZERO, payout.getAmount());
            entry(
                wallet,
                TransactionType.ADJUSTMENT,
                payout.getAmount(),
                "POSTED",
                "AVAILABLE",
                "payout:" + id + ":restore",
                now,
                null,
                payout,
                null
            );
        } else throw error(HttpStatus.BAD_REQUEST, "Quyết định không hợp lệ");
        payout.setProcessedBy(admin);
        payout.setProcessedAt(now);
        payout.setUpdatedAt(now);
        audit(admin, "PAYOUT_" + payout.getStatus(), "PayoutRequest", id, decision.reason());
        return view(payout);
    }

    @Transactional
    public PayoutView completePayout(Long id, Transfer transfer) {
        User admin = currentUser();
        PayoutRequest payout = payouts.lockById(id).orElseThrow(() -> error(HttpStatus.NOT_FOUND, "Không tìm thấy yêu cầu"));
        if (!"APPROVED".equals(payout.getStatus())) throw error(HttpStatus.CONFLICT, "Yêu cầu chưa được duyệt hoặc đã hoàn tất");
        payout.setStatus("COMPLETED");
        payout.setTransferReference(transfer.reference().trim());
        payout.setUpdatedAt(Instant.now());
        transactions.findByIdempotencyKey("payout:" + id + ":reserve").ifPresent(t -> t.setStatus("POSTED"));
        audit(admin, "PAYOUT_COMPLETED", "PayoutRequest", id, transfer.reference());
        return view(payout);
    }

    private record RefundDetails(String format, String reason, String bankCode, String bankAccount, String accountHolderName) {}

    private RefundDetails refundDetails(String stored) {
        if (stored != null && stored.startsWith("{")) {
            try {
                RefundDetails details = MAPPER.readValue(stored, RefundDetails.class);
                if (REFUND_DETAILS_FORMAT.equals(details.format())) return details;
            } catch (RuntimeException ignored) {}
        }
        return new RefundDetails(null, stored, null, null, null);
    }

    private RefundView view(Refund r) {
        RefundDetails details = refundDetails(r.getReason());
        return new RefundView(
            r.getId(),
            r.getOrder().getId(),
            r.getOrder().getBuyer() == null ? null : r.getOrder().getBuyer().getLogin(),
            r.getAmount(),
            details.reason(),
            details.bankCode(),
            details.bankAccount(),
            details.accountHolderName(),
            r.getStatus(),
            r.getCreatedAt(),
            r.getProcessedAt(),
            r.getReviewNote(),
            r.getRefundReference()
        );
    }

    @Transactional(readOnly = true)
    public List<RefundView> myRefunds() {
        return refunds.findByOrderBuyerIdOrderByCreatedAtDesc(currentUser().getId()).stream().map(this::view).toList();
    }

    @Transactional(readOnly = true)
    public List<RefundView> allRefunds() {
        return refunds.findAllByOrderByCreatedAtDesc().stream().map(this::view).toList();
    }

    @Transactional
    public RefundView requestRefund(RefundCreate request) {
        User buyer = currentUser();
        CourseOrder order = orders
            .findForPaymentUpdate(request.orderId())
            .orElseThrow(() -> error(HttpStatus.NOT_FOUND, "Không tìm thấy đơn hàng"));
        if (order.getBuyer() == null || !buyer.getId().equals(order.getBuyer().getId())) throw error(
            HttpStatus.NOT_FOUND,
            "Không tìm thấy đơn hàng"
        );
        Instant now = Instant.now();
        if (order.getStatus() != OrderStatus.PAID || order.getPaidAt() == null || !now.isBefore(order.getPaidAt().plus(HOLD))) throw error(
            HttpStatus.BAD_REQUEST,
            "Đơn hàng không đủ điều kiện hoàn tiền"
        );
        if (refunds.existsByOrderIdAndStatusIn(order.getId(), List.of("PENDING", "APPROVED", "COMPLETED"))) throw error(
            HttpStatus.CONFLICT,
            "Đơn hàng đã có yêu cầu hoàn tiền"
        );
        Payment payment = payments
            .findFirstByOrderIdAndStatusOrderByPaidAtDesc(order.getId(), "SUCCESS")
            .orElseThrow(() -> error(HttpStatus.BAD_REQUEST, "Không tìm thấy thanh toán thành công"));
        Refund refund = new Refund();
        refund.setOrder(order);
        refund.setPayment(payment);
        refund.setAmount(payment.getAmount().multiply(new BigDecimal("0.80")).setScale(2, RoundingMode.HALF_UP));
        refund.setReason(
            MAPPER.writeValueAsString(
                new RefundDetails(
                    REFUND_DETAILS_FORMAT,
                    request.reason().trim(),
                    request.bankCode().trim(),
                    request.bankAccount().trim(),
                    request.accountHolderName().trim()
                )
            )
        );
        refund.setStatus("PENDING");
        refund.setCreatedAt(now);
        return view(refunds.save(refund));
    }

    @Transactional
    public RefundView decideRefund(Long id, Decision decision) {
        User admin = currentUser();
        Refund refund = refunds.lockById(id).orElseThrow(() -> error(HttpStatus.NOT_FOUND, "Không tìm thấy yêu cầu"));
        if (!"PENDING".equals(refund.getStatus())) throw error(HttpStatus.CONFLICT, "Yêu cầu đã được xử lý");
        Instant now = Instant.now();
        if ("APPROVE".equals(decision.action())) {
            CourseOrder order = orders.findForPaymentUpdate(refund.getOrder().getId()).orElseThrow();
            if (order.getStatus() != OrderStatus.PAID) throw error(HttpStatus.CONFLICT, "Đơn hàng không còn hợp lệ");
            if (transactions.findByIdempotencyKey("sale:" + order.getId()).isEmpty()) {
                creditSale(order, order.getPaidAt());
            }
            WalletTransaction sale = transactions
                .findByOrderIdAndType(order.getId(), TransactionType.SALE)
                .orElseThrow(() -> error(HttpStatus.CONFLICT, "Chưa ghi nhận doanh thu ví"));
            InstructorWallet wallet = wallets.lockById(sale.getWallet().getId()).orElseThrow();
            BigDecimal allocated = money(order.getInstructorAmount()).add(money(order.getPlatformAmount()));
            if (allocated.signum() <= 0) throw error(HttpStatus.CONFLICT, "Thiếu phân bổ doanh thu của đơn hàng");
            BigDecimal reversal = refund.getAmount().multiply(order.getInstructorAmount()).divide(allocated, 2, RoundingMode.HALF_UP);
            boolean released = "RELEASED".equals(sale.getStatus());
            change(wallet, released ? BigDecimal.ZERO : reversal.negate(), released ? reversal.negate() : BigDecimal.ZERO);
            entry(
                wallet,
                TransactionType.REFUND,
                reversal.negate(),
                "POSTED",
                released ? "AVAILABLE" : "PENDING",
                "refund:" + id + ":reversal",
                now,
                order,
                null,
                refund
            );
            CourseEnrollment enrollment = enrollments
                .findByStudentIdAndCourseId(order.getStudent().getId(), order.getCourse().getId())
                .orElseThrow(() -> error(HttpStatus.CONFLICT, "Không tìm thấy quyền học"));
            enrollment.setStatus("REVOKED");
            enrollment.setUpdatedAt(now);
            order.setEntitlementStatus("REVOKED");
            order.setUpdatedAt(now);
            refund.setAccessRevokedAt(now);
            refund.setStatus("APPROVED");
        } else if ("REJECT".equals(decision.action()) && decision.reason() != null && !decision.reason().isBlank()) {
            refund.setStatus("REJECTED");
            refund.setReviewNote(decision.reason().trim());
        } else throw error(HttpStatus.BAD_REQUEST, "Quyết định không hợp lệ");
        refund.setProcessedBy(admin);
        refund.setProcessedAt(now);
        audit(admin, "REFUND_" + refund.getStatus(), "Refund", id, decision.reason());
        return view(refund);
    }

    @Transactional
    public RefundView completeRefund(Long id, Transfer transfer) {
        User admin = currentUser();
        Refund refund = refunds.lockById(id).orElseThrow(() -> error(HttpStatus.NOT_FOUND, "Không tìm thấy yêu cầu"));
        if (!"APPROVED".equals(refund.getStatus())) throw error(HttpStatus.CONFLICT, "Yêu cầu chưa được duyệt hoặc đã hoàn tất");
        refund.setStatus("COMPLETED");
        refund.setRefundReference(transfer.reference().trim());
        CourseOrder order = orders.findForPaymentUpdate(refund.getOrder().getId()).orElseThrow();
        order.setStatus(OrderStatus.REFUNDED);
        order.setUpdatedAt(Instant.now());
        audit(admin, "REFUND_COMPLETED", "Refund", id, transfer.reference());
        return view(refund);
    }
}
