package com.lumi.repository;

import com.lumi.domain.CourseOrder;
import java.util.List;
import java.util.Optional;
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
