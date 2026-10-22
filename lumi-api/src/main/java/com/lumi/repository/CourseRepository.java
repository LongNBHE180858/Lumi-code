package com.lumi.repository;

import com.lumi.domain.Course;
import com.lumi.service.dto.TeachingScopeDTO;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Course entity.
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, Long>, JpaSpecificationExecutor<Course> {
    @Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Course c where c.id = :id")
    Optional<Course> findForBuilderUpdate(@Param("id") Long id);

    String BROWSE_CONDITIONS = """
    where c.status = com.lumi.domain.enumeration.RecordStatus.ACTIVE
      and (c.isDeleted = false or c.isDeleted is null)
      and c.grade.level between 1 and 9
      and (:keyword = '' or locate(lower(:keyword), lower(c.title)) > 0)
      and (:subjectId is null or c.subject.id = :subjectId)
      and (:tagId is null or exists (select 1 from Course tagged join tagged.tags tag where tagged.id = c.id and tag.id = :tagId))
      and (:filterGrades = false or c.grade.id in (:gradeIds))
      and (
          :feeType = 'ALL'
          or (:feeType = 'FREE' and c.price = 0)
          or (:feeType = 'PAID' and c.price > 0)
      )
    """;

    @Query(
        value = """
        select c from Course c
        left join fetch c.author
        left join fetch c.subject
        left join fetch c.grade
        """ + BROWSE_CONDITIONS,
        countQuery = "select count(c) from Course c " + BROWSE_CONDITIONS
    )
    Page<Course> browse(
        @Param("keyword") String keyword,
        @Param("subjectId") Long subjectId,
        @Param("tagId") Long tagId,
        @Param("filterGrades") boolean filterGrades,
        @Param("gradeIds") List<Long> gradeIds,
        @Param("feeType") String feeType,
        Pageable pageable
    );

    @Query("select course from Course course where course.author.login = ?#{authentication.name}")
    List<Course> findByAuthorIsCurrentUser();

    //hàm lấy course theo id cùng với thông tin từ các bảng liên quan
    default Optional<Course> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Course> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Course> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select course from Course course left join fetch course.author left join fetch course.category left join fetch course.subject left join fetch course.grade",
        countQuery = "select count(course) from Course course"
    )
    Page<Course> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select course from Course course left join fetch course.author left join fetch course.category left join fetch course.subject left join fetch course.grade"
    )
    List<Course> findAllWithToOneRelationships();

    @Query(
        "select course from Course course left join fetch course.author left join fetch course.category left join fetch course.subject left join fetch course.grade where course.id =:id"
    )
    Optional<Course> findOneWithToOneRelationships(@Param("id") Long id);

    @Query(
        """
            select distinct new com.lumi.service.dto.TeachingScopeDTO(
                c.subject.id,
                c.subject.name,
                c.grade.id,
                c.grade.level
            )
            from Course c
            where c.author.id = :instructorId
              and c.status = com.lumi.domain.enumeration.RecordStatus.ACTIVE
              and (c.isDeleted is null or c.isDeleted = false)
              and c.subject is not null
              and c.grade is not null
              and c.grade.level between 1 and 9
        """
    )
    List<TeachingScopeDTO> findTeachingScopesByInstructorId(@Param("instructorId") Long instructorId);

    // Dung 'left join fetch' de lay luon subject va grade trong 1 query duy nhat
    @Query(
        """
            select c from Course c
            left join fetch c.subject
            left join fetch c.grade
            where c.author.id = :instructorId
              and c.status = com.lumi.domain.enumeration.RecordStatus.ACTIVE
              and (c.isDeleted is null or c.isDeleted = false)
              and c.grade.level between 1 and 9
            order by c.createdAt desc
        """
    )
    List<Course> findPublicCoursesByInstructorId(@Param("instructorId") Long instructorId);
}
