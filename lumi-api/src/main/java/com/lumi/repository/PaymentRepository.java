package com.lumi.repository;

import com.lumi.domain.Payment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Payment entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long>, JpaSpecificationExecutor<Payment> {
    Optional<Payment> findFirstByOrderIdAndStatusOrderByPaidAtDesc(Long orderId, String status);

    @Query(
        "select p from Payment p join fetch p.order o where p.gateway = 'SEPAY' and p.status = 'SUCCESS' and p.paidAt is not null and o.orderType = 'COURSE' and o.status = com.lumi.domain.enumeration.OrderStatus.PAID and o.course.author is not null and o.instructorAmount is not null and not exists (select t.id from WalletTransaction t where t.order = o and t.type = com.lumi.domain.enumeration.TransactionType.SALE)"
    )
    List<Payment> findUncreditedSePaySales(Pageable pageable);
}
