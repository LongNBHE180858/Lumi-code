package com.lumi.repository;

import com.lumi.domain.ParentStudentLink;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ParentStudentLink entity.
 */
@Repository
public interface ParentStudentLinkRepository
        extends JpaRepository<ParentStudentLink, Long>, JpaSpecificationExecutor<ParentStudentLink> {
    @Query("select parentStudentLink from ParentStudentLink parentStudentLink where parentStudentLink.parent.login = ?#{authentication.name}")
    List<ParentStudentLink> findByParentIsCurrentUser();

    @Query("select parentStudentLink from ParentStudentLink parentStudentLink where parentStudentLink.student.login = ?#{authentication.name}")
    List<ParentStudentLink> findByStudentIsCurrentUser();

    default Optional<ParentStudentLink> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<ParentStudentLink> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<ParentStudentLink> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(value = "select parentStudentLink from ParentStudentLink parentStudentLink left join fetch parentStudentLink.parent left join fetch parentStudentLink.student", countQuery = "select count(parentStudentLink) from ParentStudentLink parentStudentLink")
    Page<ParentStudentLink> findAllWithToOneRelationships(Pageable pageable);

    @Query("select parentStudentLink from ParentStudentLink parentStudentLink left join fetch parentStudentLink.parent left join fetch parentStudentLink.student")
    List<ParentStudentLink> findAllWithToOneRelationships();

    @Query("select parentStudentLink from ParentStudentLink parentStudentLink left join fetch parentStudentLink.parent left join fetch parentStudentLink.student where parentStudentLink.id =:id")
    Optional<ParentStudentLink> findOneWithToOneRelationships(@Param("id") Long id);

    // Dung 'left join fetch' de lay luon thong tin student (ho ten, login...) ve
    // cung luc trong 1 cau query duy nhat
    // Neu chi dung 'join' thuong ma khong co 'fetch' thi se bi van nan N+1 query
    // (ban them select cho tung hoc sinh)
    @Query("select p from ParentStudentLink p left join fetch p.student where p.parent.id = :parentId")
    List<ParentStudentLink> findByParentIdWithStudent(@Param("parentId") Long parentId);
}
