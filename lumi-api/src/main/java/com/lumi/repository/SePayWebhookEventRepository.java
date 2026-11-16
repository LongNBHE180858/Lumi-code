package com.lumi.repository;

import com.lumi.domain.SePayWebhookEvent;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SePayWebhookEventRepository extends JpaRepository<SePayWebhookEvent, Long> {

    Optional<SePayWebhookEvent> findByProviderEventId(String providerEventId);

    // Unique index giữ request trùng chờ transaction đầu tiên commit/rollback.
    // Trạng thái tạm chỉ tồn tại trong transaction, chưa được bên ngoài nhìn thấy.
    @Modifying
    @Query(value = """
        insert into sepay_webhook_events
        (id,provider_event_id,bank_reference,receiving_account,transfer_type,amount,transaction_at,received_at,processing_status)
        values(nextval('sequence_generator'),:eventId,:reference,:account,:type,:amount,:transactionAt,:receivedAt,'REVIEW_REQUIRED')
        on conflict (provider_event_id) do nothing
        """, nativeQuery = true)
    int reserveEvent(@Param("eventId") String eventId, @Param("reference") String reference,
        @Param("account") String account, @Param("type") String type, @Param("amount") BigDecimal amount,
        @Param("transactionAt") Instant transactionAt, @Param("receivedAt") Instant receivedAt);
}
