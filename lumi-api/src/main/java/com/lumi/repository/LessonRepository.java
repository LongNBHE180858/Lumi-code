package com.lumi.repository;

import com.lumi.domain.Lesson;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Lesson entity.
 */
@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long>, JpaSpecificationExecutor<Lesson> {

    List<Lesson> findByChapterCourseIdOrderByOrderIndexAscIdAsc(Long courseId);
    List<Lesson> findByChapterIdOrderByOrderIndexAscIdAsc(Long chapterId);

    @Query("""
        select l from Lesson l
        join fetch l.chapter ch
        where ch.course.id = :courseId
          and l.status = com.lumi.domain.enumeration.RecordStatus.ACTIVE
        order by ch.orderIndex asc, ch.id asc, l.orderIndex asc, l.id asc
        """)
    List<Lesson> findActiveLessonsByCourseId(@Param("courseId") Long courseId);

    default Optional<Lesson> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Lesson> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Lesson> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select lesson from Lesson lesson left join fetch lesson.chapter",
        countQuery = "select count(lesson) from Lesson lesson"
    )
    Page<Lesson> findAllWithToOneRelationships(Pageable pageable);

    @Query("select lesson from Lesson lesson left join fetch lesson.chapter")
    List<Lesson> findAllWithToOneRelationships();

    @Query("select lesson from Lesson lesson left join fetch lesson.chapter where lesson.id =:id")
    Optional<Lesson> findOneWithToOneRelationships(@Param("id") Long id);

    @Query("select lesson from Lesson lesson join lesson.chapter chapter where chapter.course.id = :courseId and (lesson.status is null or lesson.status = 'ACTIVE') order by chapter.orderIndex asc, lesson.orderIndex asc")
    List<Lesson> findByChapterCourseId(@Param("courseId") Long courseId);

    @Query("""
    select
        l.chapter.course.id as courseId,
        count(l.id) as lessonCount
    from Lesson l
    where l.chapter.course.id in :courseIds
      and l.status = com.lumi.domain.enumeration.RecordStatus.ACTIVE
    group by l.chapter.course.id
    """)
    List<CourseLessonCount> countActiveLessonsByCourseIds(@Param("courseIds") List<Long> courseIds);

    interface CourseLessonCount {
        Long getCourseId();
        Long getLessonCount();
    }
}