package com.lumi.repository;

import com.lumi.domain.ClassMember;
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
}
