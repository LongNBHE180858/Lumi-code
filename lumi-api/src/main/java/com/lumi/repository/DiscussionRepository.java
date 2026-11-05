package com.lumi.repository;

import com.lumi.domain.Discussion;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Discussion entity.
 */
@Repository
public interface DiscussionRepository extends JpaRepository<Discussion, Long>, JpaSpecificationExecutor<Discussion> {
    @Query("select discussion from Discussion discussion where discussion.student.login = ?#{authentication.name}")
    List<Discussion> findByStudentIsCurrentUser();

    default Optional<Discussion> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Discussion> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Discussion> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select discussion from Discussion discussion left join fetch discussion.student",
        countQuery = "select count(discussion) from Discussion discussion"
    )
    Page<Discussion> findAllWithToOneRelationships(Pageable pageable);

    @Query("select discussion from Discussion discussion left join fetch discussion.student")
    List<Discussion> findAllWithToOneRelationships();

    @Query("select discussion from Discussion discussion left join fetch discussion.student where discussion.id =:id")
    Optional<Discussion> findOneWithToOneRelationships(@Param("id") Long id);
}
