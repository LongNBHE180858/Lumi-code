package com.lumi.repository;

import com.lumi.domain.CourseOrder;
import com.lumi.domain.enumeration.OrderStatus;
import java.util.List;
import java.util.Optional;
import java.time.Instant;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the CourseOrder entity.
 */
@Repository
public interface CourseOrderRepository extends JpaRepository<CourseOrder, Long>, JpaSpecificationExecutor<CourseOrder> {
    Optional<CourseOrder> findByIdempotencyKey(String idempotencyKey);

    interface PaymentParticipants {
        Long getBuyerId();
        Long getStudentId();
    }

    // Chỉ đọc ID trước khi khóa tài khoản, tránh giữ entity có trạng thái cũ trong persistence context.
    @Query("select o.buyer.id as buyerId, o.student.id as studentId from CourseOrder o where o.id = :id and o.orderType = 'COURSE' and o.buyer is not null")
    Optional<PaymentParticipants> findPaymentParticipants(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select o from CourseOrder o where o.id = :id and o.orderType = 'COURSE'")
    Optional<CourseOrder> findForPaymentUpdate(@Param("id") Long id);

    @EntityGraph(attributePaths = { "buyer", "student", "course" })
    Optional<CourseOrder> findByIdAndBuyerIdAndOrderType(Long id, Long buyerId, String orderType);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update CourseOrder o set o.status = com.lumi.domain.enumeration.OrderStatus.EXPIRED, o.updatedAt = :now
        where o.id = :id and o.buyer.id = :buyerId and o.orderType = 'COURSE'
        and o.status = com.lumi.domain.enumeration.OrderStatus.PENDING and o.expiresAt <= :now
        """)
    int expirePendingOrder(@Param("id") Long id, @Param("buyerId") Long buyerId, @Param("now") Instant now);

    List<CourseOrder> findByStudentIdAndCourseIdAndOrderTypeAndStatus(
        Long studentId,
        Long courseId,
        String orderType,
        OrderStatus status
    );

    @Query("select courseOrder from CourseOrder courseOrder where courseOrder.student.login = ?#{authentication.name}")
    List<CourseOrder> findByStudentIsCurrentUser();

    default Optional<CourseOrder> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<CourseOrder> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<CourseOrder> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select courseOrder from CourseOrder courseOrder left join fetch courseOrder.student left join fetch courseOrder.course left join fetch courseOrder.plan left join fetch courseOrder.coupon",
        countQuery = "select count(courseOrder) from CourseOrder courseOrder"
    )
    Page<CourseOrder> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select courseOrder from CourseOrder courseOrder left join fetch courseOrder.student left join fetch courseOrder.course left join fetch courseOrder.plan left join fetch courseOrder.coupon"
    )
    List<CourseOrder> findAllWithToOneRelationships();

    @Query(
        "select courseOrder from CourseOrder courseOrder left join fetch courseOrder.student left join fetch courseOrder.course left join fetch courseOrder.plan left join fetch courseOrder.coupon where courseOrder.id =:id"
    )
    Optional<CourseOrder> findOneWithToOneRelationships(@Param("id") Long id);
}
