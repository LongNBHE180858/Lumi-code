package com.lumi.domain;

import com.lumi.domain.enumeration.SePayWebhookStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/** Một giao dịch SePay, không phải một lần gửi HTTP. Không lưu raw body hoặc tên người gửi. */
@Entity
@Table(name = "sepay_webhook_events")
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SePayWebhookEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    Long id;

    @Column(name = "provider_event_id", nullable = false, unique = true, length = 255)
    String providerEventId;
    @Column(name = "bank_reference", length = 255)
    String bankReference;
    @Column(name = "receiving_account", nullable = false, length = 255)
    String receivingAccount;
    @Column(name = "transfer_type", nullable = false, length = 10)
    String transferType;
    @Column(name = "amount", nullable = false, precision = 21, scale = 2)
    BigDecimal amount;
    @Column(name = "transaction_at", nullable = false)
    Instant transactionAt;
    @Column(name = "received_at", nullable = false)
    Instant receivedAt;
    @Enumerated(EnumType.STRING)
    @Column(name = "processing_status", nullable = false, length = 30)
    SePayWebhookStatus processingStatus;
    @Column(name = "processing_reason", length = 255)
    String processingReason;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    CourseOrder order;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    Payment payment;
}
