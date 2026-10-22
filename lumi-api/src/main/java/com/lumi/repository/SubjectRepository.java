package com.lumi.repository;

import com.lumi.domain.Subject;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Subject entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long>, JpaSpecificationExecutor<Subject> {
    java.util.List<Subject> findByActiveTrueOrderByNameAsc();
    java.util.Optional<Subject> findByNameIgnoreCase(String name);
}
