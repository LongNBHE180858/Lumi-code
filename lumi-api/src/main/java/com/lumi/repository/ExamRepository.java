package com.lumi.repository;

import com.lumi.domain.Exam;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Exam entity.
 */
@Repository
public interface ExamRepository extends JpaRepository<Exam, Long>, JpaSpecificationExecutor<Exam> {
    List<Exam> findByStudyClassIdAndIsDeletedFalseOrderByCreatedAtDesc(Long classId);

    @Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from Exam e where e.id = :id")
    Optional<Exam> findByIdForUpdate(@Param("id") Long id);

    @Query("select exam from Exam exam where exam.createdBy.login = ?#{authentication.name}")
    List<Exam> findByCreatedByIsCurrentUser();

    @Query("select exam from Exam exam where exam.assignedStudent.login = ?#{authentication.name}")
    List<Exam> findByAssignedStudentIsCurrentUser();

    List<Exam> findByCourseIdAndChapterIsNullAndLessonIsNullAndIsDeletedFalseOrderByCreatedAtDesc(Long courseId);

    List<Exam> findByChapterIdAndLessonIsNullAndIsDeletedFalseOrderByCreatedAtDesc(Long chapterId);

    List<Exam> findByChapterIdAndIsDeletedFalseOrderByCreatedAtDesc(Long chapterId);

    List<Exam> findByLessonIdAndAssessmentTypeAndIsDeletedFalseOrderByCreatedAtDesc(Long lessonId, String assessmentType);

    List<Exam> findByLessonIdAndIsDeletedFalseOrderByCreatedAtDesc(Long lessonId);

    List<Exam> findByLessonIdAndAssessmentTypeAndIsDeletedFalseOrderByVideoTriggerSecondsAsc(Long lessonId, String assessmentType);

    boolean existsByLessonIdAndAssessmentTypeAndVideoTriggerSecondsAndIsDeletedFalse(
        Long lessonId,
        String assessmentType,
        java.math.BigDecimal videoTriggerSeconds
    );

    Optional<Exam> findByIdAndIsDeletedFalse(Long id);

    default Optional<Exam> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Exam> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Exam> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select exam from Exam exam left join fetch exam.createdBy left join fetch exam.assignedStudent",
        countQuery = "select count(exam) from Exam exam"
    )
    Page<Exam> findAllWithToOneRelationships(Pageable pageable);

    @Query("select exam from Exam exam left join fetch exam.createdBy left join fetch exam.assignedStudent")
    List<Exam> findAllWithToOneRelationships();

    @Query("select exam from Exam exam left join fetch exam.createdBy left join fetch exam.assignedStudent where exam.id =:id")
    Optional<Exam> findOneWithToOneRelationships(@Param("id") Long id);
}
