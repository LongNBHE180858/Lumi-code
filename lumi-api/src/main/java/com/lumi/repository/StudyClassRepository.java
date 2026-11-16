package com.lumi.repository;

import com.lumi.domain.StudyClass;
import com.lumi.domain.enumeration.RecordStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the StudyClass entity.
 */
@Repository
public interface StudyClassRepository extends JpaRepository<StudyClass, Long>, JpaSpecificationExecutor<StudyClass> {
    boolean existsByJoinCode(String joinCode);

    @Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    Optional<StudyClass> findByJoinCodeIgnoreCase(String joinCode);

    @Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from StudyClass c where c.id = :id")
    Optional<StudyClass> findByIdForUpdate(@Param("id") Long id);

    @Query("select studyClass from StudyClass studyClass where studyClass.instructor.login = ?#{authentication.name}")
    List<StudyClass> findByInstructorIsCurrentUser();

    default Optional<StudyClass> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<StudyClass> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<StudyClass> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select studyClass from StudyClass studyClass left join fetch studyClass.instructor",
        countQuery = "select count(studyClass) from StudyClass studyClass"
    )
    Page<StudyClass> findAllWithToOneRelationships(Pageable pageable);

    @Query("select studyClass from StudyClass studyClass left join fetch studyClass.instructor")
    List<StudyClass> findAllWithToOneRelationships();

    @Query("select studyClass from StudyClass studyClass left join fetch studyClass.instructor where studyClass.id =:id")
    Optional<StudyClass> findOneWithToOneRelationships(@Param("id") Long id);

    List<StudyClass> findAllByInstructor_IdOrderByCreatedAtAsc(Long instructorId);

    @Query(
        value = """
        select c
        from StudyClass c
        left join fetch c.subject
        left join fetch c.grade
        where c.instructor.id = :instructorId
          and (c.isDeleted = false or c.isDeleted is null)
          and (
              :status is null
              or c.status = :status
          )
          and (
              :keyword is null
              or lower(c.name) like :keyword escape '!'
          )
        """,
        countQuery = """
        select count(c.id)
        from StudyClass c
        where c.instructor.id = :instructorId
          and (c.isDeleted = false or c.isDeleted is null)
          and (
              :status is null
              or c.status = :status
          )
          and (
              :keyword is null
              or lower(c.name) like :keyword escape '!'
          )
        """
    )
    Page<StudyClass> findOwnedClasses(
        @Param("instructorId") Long instructorId,
        @Param("keyword") String keyword,
        @Param("status") RecordStatus status,
        Pageable pageable
    );
}
