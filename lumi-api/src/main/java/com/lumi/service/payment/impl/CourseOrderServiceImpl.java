package com.lumi.service.payment.impl;

import com.lumi.domain.Course;
import com.lumi.domain.CourseOrder;
import com.lumi.domain.User;
import com.lumi.domain.enumeration.AccessDurationUnit;
import com.lumi.domain.enumeration.OrderStatus;
import com.lumi.repository.CourseOrderRepository;
import com.lumi.repository.CourseRepository;
import com.lumi.repository.CourseEnrollmentRepository;
import com.lumi.service.payment.ParentPurchaseAccessService;
import com.lumi.service.payment.CourseOrderService;
import com.lumi.service.CourseService;
import com.lumi.service.payment.SePayQrService;
import com.lumi.service.dto.CourseCheckoutDTO;
import com.lumi.service.dto.CreateCourseOrderRequestDTO;
import com.lumi.service.dto.CreateCourseOrderResponseDTO;
import com.lumi.service.dto.CourseOrderDetailDTO;
import com.lumi.service.dto.CourseOrderPaymentInfoDTO;
import com.lumi.service.dto.PaymentInfoDTO;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CourseOrderServiceImpl implements CourseOrderService {

    private static final String ORDER_TYPE = "COURSE";

    CourseOrderRepository orderRepository;
    CourseRepository courseRepository;
    ParentPurchaseAccessService purchaseAccessService;
    CourseService courseService;
    ObjectMapper objectMapper;
    SePayQrService sePayQrService;
    CourseEnrollmentRepository enrollmentRepository;

    @Override
    @Transactional
    @PreAuthorize("hasRole('PARENT')")
    public CourseOrderDetailDTO getOrderDetail(Long orderId) {
        User parent = purchaseAccessService.getCurrentActiveParent();
        CourseOrder order = getOwnedOrder(orderId, parent.getId());

        Instant now = Instant.now();
        order = expireOrderIfNecessary(order, parent.getId(), now);

        // Chủ đơn vẫn được xem thông tin đơn cũ khi liên kết với con đã thu hồi.
        return toOrderDetail(order, now);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('PARENT')")
    public CourseOrderPaymentInfoDTO getPaymentInfo(Long orderId) {
        User parent = purchaseAccessService.getCurrentActiveParent();
        CourseOrder order = getOwnedOrder(orderId, parent.getId());

        Instant now = Instant.now();
        order = expireOrderIfNecessary(order, parent.getId(), now);
        if (!isPaymentRequired(order, now)) {
            return toPaymentInfoResponse(order, null, now);
        }

        // Xem được đơn không đồng nghĩa với còn quyền tiếp tục thanh toán cho con.
        purchaseAccessService.validateForCheckout(order.getStudent().getId());
        if (enrollmentRepository.existsByStudentIdAndCourseId(order.getStudent().getId(), order.getCourse().getId())
            || !orderRepository.findByStudentIdAndCourseIdAndOrderTypeAndStatus(
                order.getStudent().getId(), order.getCourse().getId(), ORDER_TYPE, OrderStatus.PAID).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Học sinh đã có quyền học hoặc đơn đã thanh toán cho khóa học này.");
        }
        PaymentInfoDTO paymentInfo = sePayQrService.buildPaymentInfo(order.getId(), order.getFinalPrice());

        return toPaymentInfoResponse(order, paymentInfo, now);
    }

    private CourseOrder getOwnedOrder(Long orderId, Long buyerId) {
        if (orderId == null || orderId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mã đơn hàng không hợp lệ.");
        }
        return orderRepository.findByIdAndBuyerIdAndOrderType(orderId, buyerId, ORDER_TYPE)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy đơn hàng."));
    }

    private CourseOrder expireOrderIfNecessary(CourseOrder order, Long buyerId, Instant now) {
        if (order.getStatus() != OrderStatus.PENDING || order.getExpiresAt() == null || order.getExpiresAt().isAfter(now)) {
            return order;
        }

        // UPDATE có điều kiện: không ghi đè PAID nếu trạng thái vừa được cập nhật.
        orderRepository.expirePendingOrder(order.getId(), buyerId, now);
        return getOwnedOrder(order.getId(), buyerId);
    }

    private boolean isPaymentRequired(CourseOrder order, Instant now) {
        return order.getStatus() == OrderStatus.PENDING && order.getExpiresAt() != null && order.getExpiresAt().isAfter(now);
    }

    private CourseOrderDetailDTO toOrderDetail(CourseOrder order, Instant now) {
        CreateCourseOrderResponseDTO savedOrder = toResponse(order, now);
        return CourseOrderDetailDTO.builder()
            .orderId(savedOrder.getOrderId())
            .buyerId(savedOrder.getBuyerId())
            .studentId(savedOrder.getStudentId())
            .courseId(savedOrder.getCourseId())
            .status(savedOrder.getStatus())
            .originalPrice(savedOrder.getOriginalPrice())
            .taxAmount(savedOrder.getTaxAmount())
            .finalPrice(savedOrder.getFinalPrice())
            .accessDurationValue(savedOrder.getAccessDurationValue())
            .accessDurationUnit(savedOrder.getAccessDurationUnit())
            .paymentRequired(savedOrder.isPaymentRequired())
            .createdAt(savedOrder.getCreatedAt())
            .expiresAt(savedOrder.getExpiresAt())
            .paidAt(order.getPaidAt())
            .serverTime(now)
            .build();
    }

    private CourseOrderPaymentInfoDTO toPaymentInfoResponse(CourseOrder order, PaymentInfoDTO paymentInfo, Instant now) {
        return CourseOrderPaymentInfoDTO.builder()
            .orderId(order.getId())
            .status(order.getStatus())
            .paymentRequired(isPaymentRequired(order, now))
            .expiresAt(order.getExpiresAt())
            .serverTime(now)
            .paymentInfo(paymentInfo)
            .build();
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('PARENT')")
    public CreateCourseOrderResponseDTO createOrder(CreateCourseOrderRequestDTO req, UUID idempotencyKey) {
        if (req == null || req.getCourseId() == null || req.getCourseId() <= 0
            || req.getStudentId() == null || req.getStudentId() <= 0 || idempotencyKey == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Thông tin tạo đơn không hợp lệ.");
        }

        var access = purchaseAccessService.validateAndLockForOrder(req.getStudentId());
        User buyer = access.getParent();
        User student = access.getStudent();

        String key = "parent-course-order:" + buyer.getId() + ":" + idempotencyKey;
        Instant now = Instant.now();
        CourseOrder existing = orderRepository.findByIdempotencyKey(key).orElse(null);
        if (existing != null) {
            if (existing.getBuyer() == null || !existing.getBuyer().getId().equals(buyer.getId())
                || !existing.getStudent().getId().equals(student.getId())
                || existing.getCourse() == null
                || !existing.getCourse().getId().equals(req.getCourseId())
                || !ORDER_TYPE.equals(existing.getOrderType())
                || existing.getFinalPrice() == null || existing.getFinalPrice().signum() <= 0) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Mã chống trùng không phù hợp với yêu cầu này.");
            }
            if (existing.getStatus() == OrderStatus.PENDING && existing.getExpiresAt() != null
                && !existing.getExpiresAt().isAfter(now)) {
                existing.setStatus(OrderStatus.EXPIRED);
                existing.setUpdatedAt(now);
                orderRepository.save(existing);
            }
            return toResponse(existing, Instant.now());
        }

        // Reuse the existing course write lock to freeze price and duration until commit.
        Course course = courseRepository.findForBuilderUpdate(req.getCourseId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy khóa học."));
        CourseCheckoutDTO checkout = courseService.getCheckout(course.getId(), student.getId());
        if (!checkout.isCanPurchase()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, checkout.getPurchaseBlockReason());
        }
        if (checkout.getFinalPrice() == null || checkout.getFinalPrice().signum() <= 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "API này chỉ hỗ trợ đăng ký khóa học có phí.");
        }

        var paidOrders = orderRepository.findByStudentIdAndCourseIdAndOrderTypeAndStatus(
            student.getId(), course.getId(), ORDER_TYPE, OrderStatus.PAID
        );
        if (!paidOrders.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Khóa học đã có đơn thanh toán thành công.");
        }

        now = Instant.now();
        var pendingOrders = orderRepository.findByStudentIdAndCourseIdAndOrderTypeAndStatus(
            student.getId(), course.getId(), ORDER_TYPE, OrderStatus.PENDING
        );
        for (CourseOrder pending : pendingOrders) {
            if (pending.getExpiresAt() == null || pending.getExpiresAt().isAfter(now)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Học sinh đã có đơn thanh toán đang chờ cho khóa học này.");
            }
            pending.setStatus(OrderStatus.EXPIRED);
            pending.setUpdatedAt(now);
            orderRepository.save(pending);
        }

        CourseOrder order = buildOrder(buyer, student, course, checkout, key, now);
        return toResponse(orderRepository.saveAndFlush(order), Instant.now());
    }

    private CourseOrder buildOrder(User buyer, User student, Course course, CourseCheckoutDTO checkout, String key, Instant now) {
        BigDecimal price = checkout.getOriginalPrice();
        // Reviewed marketplace default (DBR-16); no referral/coupon in this endpoint.
        BigDecimal instructorAmount = price.multiply(new BigDecimal("0.80"));
        BigDecimal platformAmount = price.subtract(instructorAmount);

        CourseOrder order = new CourseOrder();
        order.setBuyer(buyer);
        order.setStudent(student);
        order.setCourse(course);
        order.setOrderType(ORDER_TYPE);
        order.setStatus(OrderStatus.PENDING);
        order.setIdempotencyKey(key);
        order.setOriginalPrice(price);
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setFinalPrice(checkout.getFinalPrice());
        order.setRevenueSplitRate("80/20");
        order.setInstructorSharePercent(new BigDecimal("80"));
        order.setInstructorAmount(instructorAmount);
        order.setPlatformAmount(platformAmount);
        order.setAccessDurationValue(checkout.getAccessDurationValue());
        order.setAccessDurationUnit(checkout.getAccessDurationUnit().name());
        order.setCreatedAt(now);
        order.setUpdatedAt(now);
        order.setExpiresAt(now.plus(15, ChronoUnit.MINUTES));
        order.setPricingSnapshot(objectMapper.writeValueAsString(Map.ofEntries(
            Map.entry("schemaVersion", 1),
            Map.entry("source", "LUMI_MARKETPLACE"),
            Map.entry("instructorId", course.getAuthor().getId()),
            Map.entry("originalPrice", price),
            Map.entry("discountAmount", BigDecimal.ZERO),
            Map.entry("courseSubtotal", price),
            Map.entry("taxAmount", checkout.getTaxAmount()),
            Map.entry("serviceFee", BigDecimal.ZERO),
            Map.entry("finalPrice", checkout.getFinalPrice()),
            Map.entry("instructorSharePercent", new BigDecimal("80")),
            Map.entry("platformSharePercent", new BigDecimal("20")),
            Map.entry("instructorAmount", instructorAmount),
            Map.entry("platformAmount", platformAmount)
        )));
        return order;
    }

    private CreateCourseOrderResponseDTO toResponse(CourseOrder order, Instant now) {
        if (order.getPricingSnapshot() == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Đơn hàng chưa có thông tin chốt giá hợp lệ.");
        }
        var snapshot = objectMapper.readTree(order.getPricingSnapshot());
        var tax = snapshot.get("taxAmount");
        if (tax == null || !tax.isNumber()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Đơn hàng chưa có thông tin thuế hợp lệ.");
        }
        return CreateCourseOrderResponseDTO.builder()
            .orderId(order.getId())
            .courseId(order.getCourse().getId())
            .buyerId(order.getBuyer().getId())
            .studentId(order.getStudent().getId())
            .status(order.getStatus())
            .originalPrice(order.getOriginalPrice())
            .taxAmount(tax.decimalValue())
            .finalPrice(order.getFinalPrice())
            .accessDurationValue(order.getAccessDurationValue())
            .accessDurationUnit(AccessDurationUnit.valueOf(order.getAccessDurationUnit()))
            .paymentRequired(isPaymentRequired(order, now))
            .createdAt(order.getCreatedAt())
            .expiresAt(order.getExpiresAt())
            .serverTime(now)
            .build();
    }
}
