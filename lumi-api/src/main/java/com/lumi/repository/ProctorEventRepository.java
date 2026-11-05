package com.lumi.repository;

import com.lumi.domain.ProctorEvent;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ProctorEvent entity.
 */
@Repository
public interface ProctorEventRepository extends JpaRepository<ProctorEvent, Long>, JpaSpecificationExecutor<ProctorEvent> {
    @Query("select proctorEvent from ProctorEvent proctorEvent where proctorEvent.reviewedBy.login = ?#{authentication.name}")
    List<ProctorEvent> findByReviewedByIsCurrentUser();

    default Optional<ProctorEvent> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<ProctorEvent> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<ProctorEvent> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select proctorEvent from ProctorEvent proctorEvent left join fetch proctorEvent.reviewedBy",
        countQuery = "select count(proctorEvent) from ProctorEvent proctorEvent"
    )
    Page<ProctorEvent> findAllWithToOneRelationships(Pageable pageable);

    @Query("select proctorEvent from ProctorEvent proctorEvent left join fetch proctorEvent.reviewedBy")
    List<ProctorEvent> findAllWithToOneRelationships();

    @Query("select proctorEvent from ProctorEvent proctorEvent left join fetch proctorEvent.reviewedBy where proctorEvent.id =:id")
    Optional<ProctorEvent> findOneWithToOneRelationships(@Param("id") Long id);
}
