package com.lumi.repository;

import com.lumi.domain.CourseReview;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the CourseReview entity.
 */
@Repository
public interface CourseReviewRepository extends JpaRepository<CourseReview, Long>, JpaSpecificationExecutor<CourseReview> {

    //kiểu page = nội dung + totalelement + total page
    @Query(
        value = "select r from CourseReview r left join fetch r.student where r.course.id = :courseId and r.rating between 1 and 5",
        countQuery = "select count(r) from CourseReview r where r.course.id = :courseId and r.rating between 1 and 5"
    )
    Page<CourseReview> findPageByCourseId(@Param("courseId") Long courseId, Pageable pageable);

    //coalesce = CASE
    //    WHEN sum(r.rating) IS NOT NULL THEN sum(r.rating)
    //    ELSE 0
    //END

    @Query("""
        select count(r.id) as totalReviews,
               coalesce(sum(r.rating), 0) as totalRating,
               coalesce(sum(case when r.rating = 1 then 1L else 0L end), 0) as oneStarCount,
               coalesce(sum(case when r.rating = 2 then 1L else 0L end), 0) as twoStarCount,
               coalesce(sum(case when r.rating = 3 then 1L else 0L end), 0) as threeStarCount,
               coalesce(sum(case when r.rating = 4 then 1L else 0L end), 0) as fourStarCount,
               coalesce(sum(case when r.rating = 5 then 1L else 0L end), 0) as fiveStarCount
        from CourseReview r
        where r.course.id = :courseId
          and r.rating between 1 and 5
        """)
    RatingSummary getRatingSummaryByCourseId(@Param("courseId") Long courseId);

    interface RatingSummary {
        Long getTotalReviews();
        Long getTotalRating();
        Long getOneStarCount();
        Long getTwoStarCount();
        Long getThreeStarCount();
        Long getFourStarCount();
        Long getFiveStarCount();
    }

    @Query("select courseReview from CourseReview courseReview where courseReview.student.login = ?#{authentication.name}")
    List<CourseReview> findByStudentIsCurrentUser();

    default Optional<CourseReview> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<CourseReview> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<CourseReview> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select courseReview from CourseReview courseReview left join fetch courseReview.student left join fetch courseReview.course",
        countQuery = "select count(courseReview) from CourseReview courseReview"
    )
    Page<CourseReview> findAllWithToOneRelationships(Pageable pageable);

    @Query("select courseReview from CourseReview courseReview left join fetch courseReview.student left join fetch courseReview.course")
    List<CourseReview> findAllWithToOneRelationships();

    @Query(
        "select courseReview from CourseReview courseReview left join fetch courseReview.student left join fetch courseReview.course where courseReview.id =:id"
    )
    Optional<CourseReview> findOneWithToOneRelationships(@Param("id") Long id);
}
