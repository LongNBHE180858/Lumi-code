package com.lumi.repository;

import com.lumi.domain.PayoutRequest;
import jakarta.persistence.LockModeType;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PayoutRequest entity.
 */
@Repository
public interface PayoutRequestRepository extends JpaRepository<PayoutRequest, Long>, JpaSpecificationExecutor<PayoutRequest> {
    List<PayoutRequest> findByWalletInstructorIdOrderByCreatedAtDesc(Long instructorId);
    List<PayoutRequest> findAllByOrderByCreatedAtDesc();
    boolean existsByWalletIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(Long walletId, Instant from, Instant to);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from PayoutRequest p where p.id = :id")
    Optional<PayoutRequest> lockById(@Param("id") Long id);

    @Query("select payoutRequest from PayoutRequest payoutRequest where payoutRequest.processedBy.login = ?#{authentication.name}")
    List<PayoutRequest> findByProcessedByIsCurrentUser();

    default Optional<PayoutRequest> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<PayoutRequest> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<PayoutRequest> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select payoutRequest from PayoutRequest payoutRequest left join fetch payoutRequest.processedBy",
        countQuery = "select count(payoutRequest) from PayoutRequest payoutRequest"
    )
    Page<PayoutRequest> findAllWithToOneRelationships(Pageable pageable);

    @Query("select payoutRequest from PayoutRequest payoutRequest left join fetch payoutRequest.processedBy")
    List<PayoutRequest> findAllWithToOneRelationships();

    @Query("select payoutRequest from PayoutRequest payoutRequest left join fetch payoutRequest.processedBy where payoutRequest.id =:id")
    Optional<PayoutRequest> findOneWithToOneRelationships(@Param("id") Long id);
}
