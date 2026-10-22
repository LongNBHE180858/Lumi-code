package com.lumi.repository;

import com.lumi.domain.DiscussionReply;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the DiscussionReply entity.
 */
@Repository
public interface DiscussionReplyRepository extends JpaRepository<DiscussionReply, Long>, JpaSpecificationExecutor<DiscussionReply> {
    @Query("select discussionReply from DiscussionReply discussionReply where discussionReply.user.login = ?#{authentication.name}")
    List<DiscussionReply> findByUserIsCurrentUser();

    default Optional<DiscussionReply> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<DiscussionReply> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<DiscussionReply> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select discussionReply from DiscussionReply discussionReply left join fetch discussionReply.user left join fetch discussionReply.discussion",
        countQuery = "select count(discussionReply) from DiscussionReply discussionReply"
    )
    Page<DiscussionReply> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select discussionReply from DiscussionReply discussionReply left join fetch discussionReply.user left join fetch discussionReply.discussion"
    )
    List<DiscussionReply> findAllWithToOneRelationships();

    @Query(
        "select discussionReply from DiscussionReply discussionReply left join fetch discussionReply.user left join fetch discussionReply.discussion where discussionReply.id =:id"
    )
    Optional<DiscussionReply> findOneWithToOneRelationships(@Param("id") Long id);
}
