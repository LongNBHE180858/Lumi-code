package com.lumi.repository;

import com.lumi.domain.ClassMember;
import com.lumi.service.dto.studyClass.ClassMemberCountDTO;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ClassMember entity.
 */
@Repository
public interface ClassMemberRepository extends JpaRepository<ClassMember, Long>, JpaSpecificationExecutor<ClassMember> {
    @Query("select classMember from ClassMember classMember where classMember.student.login = ?#{authentication.name}")
    List<ClassMember> findByStudentIsCurrentUser();

    default Optional<ClassMember> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<ClassMember> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<ClassMember> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select classMember from ClassMember classMember left join fetch classMember.student",
        countQuery = "select count(classMember) from ClassMember classMember"
    )
    Page<ClassMember> findAllWithToOneRelationships(Pageable pageable);

    @Query("select classMember from ClassMember classMember left join fetch classMember.student")
    List<ClassMember> findAllWithToOneRelationships();

    @Query("select classMember from ClassMember classMember left join fetch classMember.student where classMember.id =:id")
    Optional<ClassMember> findOneWithToOneRelationships(@Param("id") Long id);

    long countByStudyClassIdAndStatus(long id, String status);

    Optional<ClassMember> findByStudyClassIdAndStudentId(Long classId, Long studentId);

    List<ClassMember> findByStudentIdOrderByRequestedAtDesc(Long studentId);

    @Query(
        value = "select m from ClassMember m join fetch m.studyClass c where m.student.id = :studentId and m.status <> 'REMOVED' and (c.isDeleted = false or c.isDeleted is null)",
        countQuery = "select count(m) from ClassMember m join m.studyClass c where m.student.id = :studentId and m.status <> 'REMOVED' and (c.isDeleted = false or c.isDeleted is null)"
    )
    Page<ClassMember> findVisibleStudentClasses(@Param("studentId") Long studentId, Pageable pageable);

    List<ClassMember> findByStudyClassIdOrderByRequestedAtAsc(Long classId);

    @Query(
        value = "select m from ClassMember m join fetch m.student where m.studyClass.id = :classId and m.status <> 'REMOVED'",
        countQuery = "select count(m) from ClassMember m where m.studyClass.id = :classId and m.status <> 'REMOVED'"
    )
    Page<ClassMember> findVisibleClassMembers(@Param("classId") Long classId, Pageable pageable);

    @Query(
        """
        select new com.lumi.service.dto.studyClass.ClassMemberCountDTO(
            m.studyClass.id,
            m.status,
            count(m.id)
        )
        from ClassMember m
        where m.studyClass.id in :classIds
          and m.studyClass.instructor.id = :instructorId
          and m.status in :statuses
        group by m.studyClass.id, m.status
        """
    )
    List<ClassMemberCountDTO> countMembersByClassIds(
        @Param("instructorId") Long instructorId,
        @Param("classIds") Collection<Long> classIds,
        @Param("statuses") Collection<String> statuses
    );
}
