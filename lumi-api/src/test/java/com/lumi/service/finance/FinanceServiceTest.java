package com.lumi.service.finance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.lumi.domain.*;
import com.lumi.domain.enumeration.TransactionType;
import com.lumi.repository.*;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class FinanceServiceTest {

    @Mock
    UserRepository users;

    @Mock
    InstructorWalletRepository wallets;

    @Mock
    WalletTransactionRepository transactions;

    @Mock
    PayoutRequestRepository payouts;

    @Mock
    RefundRepository refunds;

    @Mock
    CourseOrderRepository orders;

    @Mock
    PaymentRepository payments;

    @Mock
    CourseEnrollmentRepository enrollments;

    @Mock
    AuditLogRepository audits;

    @InjectMocks
    FinanceService service;

    @AfterEach
    void clearSecurity() {
        SecurityContextHolder.clearContext();
    }

    private void login(String name) {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(name, "test"));
    }

    @Test
    void paidCourseCreditsPendingBalanceOnce() {
        User instructor = new User();
        instructor.setId(7L);
        Course course = new Course();
        course.setAuthor(instructor);
        CourseOrder order = new CourseOrder();
        order.setId(12L);
        order.setCourse(course);
        order.setInstructorAmount(new BigDecimal("80000.00"));
        InstructorWallet wallet = new InstructorWallet();
        wallet.setId(3L);
        wallet.setPendingAmount(BigDecimal.ZERO);
        wallet.setAvailableAmount(BigDecimal.ZERO);
        wallet.setRowVersion(0L);
        when(transactions.findByIdempotencyKey("sale:12")).thenReturn(Optional.empty(), Optional.of(new WalletTransaction()));
        when(wallets.lockByInstructorId(7L)).thenReturn(Optional.of(wallet));
        when(wallets.save(wallet)).thenReturn(wallet);
        when(transactions.save(any(WalletTransaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.creditSale(order, Instant.parse("2026-11-17T00:00:00Z"));
        service.creditSale(order, Instant.parse("2026-11-17T00:00:00Z"));

        assertThat(wallet.getPendingAmount()).isEqualByComparingTo("80000.00");
        assertThat(wallet.getAvailableAmount()).isEqualByComparingTo("0");
        assertThat(wallet.getRowVersion()).isEqualTo(1L);
        verify(transactions, times(1)).save(
            argThat(
                tx ->
                    tx.getType() == TransactionType.SALE &&
                    "sale:12".equals(tx.getIdempotencyKey()) &&
                    "PENDING".equals(tx.getBalanceBucket())
            )
        );
    }

    @Test
    void releaseMovesOnlyUnrefundedRevenueToAvailable() {
        CourseOrder order = new CourseOrder();
        order.setId(12L);
        InstructorWallet wallet = new InstructorWallet();
        wallet.setId(3L);
        wallet.setPendingAmount(new BigDecimal("60000.00"));
        wallet.setAvailableAmount(BigDecimal.ZERO);
        wallet.setRowVersion(0L);
        WalletTransaction sale = new WalletTransaction();
        sale.setId(9L);
        sale.setWallet(wallet);
        sale.setOrder(order);
        sale.setType(TransactionType.SALE);
        sale.setAmount(new BigDecimal("80000.00"));
        sale.setStatus("PENDING");
        WalletTransaction reversal = new WalletTransaction();
        reversal.setAmount(new BigDecimal("-20000.00"));
        when(transactions.findByTypeAndStatusAndOccurredAtBefore(eq(TransactionType.SALE), eq("PENDING"), any(Instant.class))).thenReturn(
            List.of(sale)
        );
        when(payments.findUncreditedSePaySales(PageRequest.of(0, 100))).thenReturn(List.of());
        when(wallets.lockById(3L)).thenReturn(Optional.of(wallet));
        when(transactions.findByIdempotencyKey("release:9:available")).thenReturn(Optional.empty());
        when(transactions.findAllByOrderIdAndType(12L, TransactionType.REFUND)).thenReturn(List.of(reversal));

        service.releaseEligibleSales();

        assertThat(wallet.getPendingAmount()).isEqualByComparingTo("0");
        assertThat(wallet.getAvailableAmount()).isEqualByComparingTo("60000.00");
        assertThat(sale.getStatus()).isEqualTo("RELEASED");
        verify(transactions, times(2)).save(any(WalletTransaction.class));
    }

    @Test
    void payoutSubmissionReservesAvailableMoney() {
        login("teacher");
        User instructor = new User();
        instructor.setId(7L);
        InstructorWallet wallet = new InstructorWallet();
        wallet.setId(3L);
        wallet.setPendingAmount(BigDecimal.ZERO);
        wallet.setAvailableAmount(new BigDecimal("150000"));
        wallet.setInstructor(instructor);
        wallet.setRowVersion(0L);
        when(users.findOneByLogin("teacher")).thenReturn(Optional.of(instructor));
        when(wallets.lockByInstructorId(7L)).thenReturn(Optional.of(wallet));
        when(payouts.saveAndFlush(any(PayoutRequest.class))).thenAnswer(invocation -> {
            PayoutRequest request = invocation.getArgument(0);
            request.setId(5L);
            return request;
        });

        var result = service.requestPayout(new FinanceDTOs.PayoutCreate(new BigDecimal("50000"), "VCB", "123456", "Teacher"));

        assertThat(result.status()).isEqualTo("PENDING");
        assertThat(wallet.getAvailableAmount()).isEqualByComparingTo("100000");
        verify(transactions).save(
            argThat(
                tx ->
                    tx.getType() == TransactionType.PAYOUT &&
                    tx.getAmount().compareTo(new BigDecimal("-50000")) == 0 &&
                    "RESERVED".equals(tx.getStatus())
            )
        );
    }

    @Test
    void refundUsesPaidAmountAndRejectsOtherBuyers() {
        login("parent");
        User buyer = new User();
        buyer.setId(8L);
        User student = new User();
        student.setId(9L);
        CourseOrder order = new CourseOrder();
        order.setId(12L);
        order.setBuyer(buyer);
        order.setStudent(student);
        order.setStatus(com.lumi.domain.enumeration.OrderStatus.PAID);
        order.setPaidAt(Instant.now().minus(Duration.ofDays(2)));
        Payment payment = new Payment();
        payment.setAmount(new BigDecimal("900000"));
        when(users.findOneByLogin("parent")).thenReturn(Optional.of(buyer));
        when(orders.findForPaymentUpdate(12L)).thenReturn(Optional.of(order));
        when(payments.findFirstByOrderIdAndStatusOrderByPaidAtDesc(12L, "SUCCESS")).thenReturn(Optional.of(payment));
        when(refunds.save(any(Refund.class))).thenAnswer(invocation -> {
            Refund refund = invocation.getArgument(0);
            refund.setId(4L);
            return refund;
        });

        var result = service.requestRefund(new FinanceDTOs.RefundCreate(12L, "Không phù hợp", "VCB", "123456", "Parent"));

        assertThat(result.amount()).isEqualByComparingTo("720000");
        assertThat(result.status()).isEqualTo("PENDING");
        assertThat(result.bankCode()).isEqualTo("VCB");
        assertThat(result.bankAccount()).isEqualTo("123456");
        assertThat(result.accountHolderName()).isEqualTo("Parent");
        verify(refunds).save(
            argThat(saved -> saved.getReason().contains("lumi-refund-details-v1") && saved.getReason().contains("123456"))
        );
        User another = new User();
        another.setId(10L);
        when(users.findOneByLogin("parent")).thenReturn(Optional.of(another));
        org.assertj.core.api.Assertions.assertThatThrownBy(() ->
            service.requestRefund(new FinanceDTOs.RefundCreate(12L, "Không phù hợp", "VCB", "123456", "Parent"))
        ).isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void legacyRefundReasonRemainsReadable() {
        Refund refund = new Refund();
        refund.setReason("Lý do cũ");
        refund.setOrder(new CourseOrder());
        when(refunds.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(refund));

        var result = service.allRefunds().getFirst();

        assertThat(result.reason()).isEqualTo("Lý do cũ");
        assertThat(result.bankCode()).isNull();
    }

    @Test
    void refundApprovalReversesInstructorShareAndRevokesAccessOnce() {
        login("admin");
        User admin = new User();
        admin.setId(1L);
        User student = new User();
        student.setId(9L);
        Course course = new Course();
        course.setId(10L);
        CourseOrder order = new CourseOrder();
        order.setId(12L);
        order.setStudent(student);
        order.setCourse(course);
        order.setStatus(com.lumi.domain.enumeration.OrderStatus.PAID);
        order.setInstructorAmount(new BigDecimal("720000"));
        order.setPlatformAmount(new BigDecimal("180000"));
        Refund refund = new Refund();
        refund.setId(4L);
        refund.setOrder(order);
        refund.setAmount(new BigDecimal("720000"));
        refund.setStatus("PENDING");
        InstructorWallet wallet = new InstructorWallet();
        wallet.setId(3L);
        wallet.setPendingAmount(new BigDecimal("720000"));
        wallet.setAvailableAmount(BigDecimal.ZERO);
        WalletTransaction sale = new WalletTransaction();
        sale.setWallet(wallet);
        sale.setStatus("PENDING");
        CourseEnrollment enrollment = new CourseEnrollment();
        enrollment.setStatus("ACTIVE");
        when(users.findOneByLogin("admin")).thenReturn(Optional.of(admin));
        when(refunds.lockById(4L)).thenReturn(Optional.of(refund));
        when(orders.findForPaymentUpdate(12L)).thenReturn(Optional.of(order));
        when(transactions.findByIdempotencyKey("sale:12")).thenReturn(Optional.of(sale));
        when(transactions.findByOrderIdAndType(12L, TransactionType.SALE)).thenReturn(Optional.of(sale));
        when(wallets.lockById(3L)).thenReturn(Optional.of(wallet));
        when(enrollments.findByStudentIdAndCourseId(9L, 10L)).thenReturn(Optional.of(enrollment));

        var result = service.decideRefund(4L, new FinanceDTOs.Decision("APPROVE", null));

        assertThat(result.status()).isEqualTo("APPROVED");
        assertThat(wallet.getPendingAmount()).isEqualByComparingTo("144000");
        assertThat(enrollment.getStatus()).isEqualTo("REVOKED");
        assertThat(order.getEntitlementStatus()).isEqualTo("REVOKED");
        org.assertj.core.api.Assertions.assertThatThrownBy(() ->
            service.decideRefund(4L, new FinanceDTOs.Decision("APPROVE", null))
        ).isInstanceOf(ResponseStatusException.class);
        verify(transactions, times(1)).save(
            argThat(tx -> tx.getType() == TransactionType.REFUND && tx.getAmount().compareTo(new BigDecimal("-576000")) == 0)
        );
    }
}
