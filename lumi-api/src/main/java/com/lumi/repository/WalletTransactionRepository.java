package com.lumi.repository;

import com.lumi.domain.WalletTransaction;
import com.lumi.domain.enumeration.TransactionType;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the WalletTransaction entity.
 */
@SuppressWarnings("unused")
@Repository
public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long>, JpaSpecificationExecutor<WalletTransaction> {
    Optional<WalletTransaction> findByIdempotencyKey(String idempotencyKey);
    Optional<WalletTransaction> findByOrderIdAndType(Long orderId, TransactionType type);
    List<WalletTransaction> findAllByOrderIdAndType(Long orderId, TransactionType type);
    List<WalletTransaction> findByWalletInstructorIdOrderByOccurredAtDesc(Long instructorId);
    List<WalletTransaction> findByTypeAndStatusAndOccurredAtBefore(TransactionType type, String status, Instant cutoff);
}
