package com.lumi.repository;

import com.lumi.domain.ContentReport;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ContentReport entity.
 */
@Repository
public interface ContentReportRepository extends JpaRepository<ContentReport, Long>, JpaSpecificationExecutor<ContentReport> {
    @Query("select contentReport from ContentReport contentReport where contentReport.reporter.login = ?#{authentication.name}")
    List<ContentReport> findByReporterIsCurrentUser();

    default Optional<ContentReport> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<ContentReport> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<ContentReport> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select contentReport from ContentReport contentReport left join fetch contentReport.reporter",
        countQuery = "select count(contentReport) from ContentReport contentReport"
    )
    Page<ContentReport> findAllWithToOneRelationships(Pageable pageable);

    @Query("select contentReport from ContentReport contentReport left join fetch contentReport.reporter")
    List<ContentReport> findAllWithToOneRelationships();

    @Query("select contentReport from ContentReport contentReport left join fetch contentReport.reporter where contentReport.id =:id")
    Optional<ContentReport> findOneWithToOneRelationships(@Param("id") Long id);
}
