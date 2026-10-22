package com.lumi.repository;

import com.lumi.domain.PayoutRequest;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PayoutRequest entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PayoutRequestRepository extends JpaRepository<PayoutRequest, Long>, JpaSpecificationExecutor<PayoutRequest> {}
