package com.lumi.service.finance;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;

public final class FinanceDTOs {

    private FinanceDTOs() {}

    public record WalletView(BigDecimal pendingAmount, BigDecimal availableAmount) {}

    public record TransactionView(
        Long id,
        String type,
        BigDecimal amount,
        String status,
        String balanceBucket,
        Instant occurredAt,
        Long orderId
    ) {}

    public record PayoutCreate(
        @NotNull @DecimalMin("50000") @DecimalMax("10000000") @Digits(integer = 10, fraction = 0) BigDecimal amount,
        @NotBlank @Size(max = 50) String bankCode,
        @NotBlank @Size(max = 100) String bankAccount,
        @NotBlank @Size(max = 255) String accountHolderName
    ) {}

    public record PayoutView(
        Long id,
        String instructorLogin,
        BigDecimal amount,
        String bankCode,
        String bankAccount,
        String accountHolderName,
        String status,
        Instant createdAt,
        Instant processedAt,
        String rejectionReason,
        String transferReference
    ) {}

    public record RefundCreate(
        @NotNull Long orderId,
        @NotBlank String reason,
        @NotBlank @Size(max = 50) String bankCode,
        @NotBlank @Size(max = 100) String bankAccount,
        @NotBlank @Size(max = 255) String accountHolderName
    ) {}

    public record RefundView(
        Long id,
        Long orderId,
        String buyerLogin,
        BigDecimal amount,
        String reason,
        String bankCode,
        String bankAccount,
        String accountHolderName,
        String status,
        Instant createdAt,
        Instant processedAt,
        String reviewNote,
        String refundReference
    ) {}

    public record Decision(@NotBlank String action, String reason) {}

    public record Transfer(@NotBlank @Size(max = 255) String reference) {}
}
