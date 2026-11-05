package com.lumi.repository;

import com.lumi.domain.Wishlist;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Wishlist entity.
 */
@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long>, JpaSpecificationExecutor<Wishlist> {
    @Query("select wishlist from Wishlist wishlist where wishlist.student.login = ?#{authentication.name}")
    List<Wishlist> findByStudentIsCurrentUser();

    /**
     * True if a wishlist already links the given student to the given course. Used to prevent
     * a user from adding the same course more than once to their wishlist.
     */
    boolean existsByStudent_IdAndCourse_Id(Long studentId, Long courseId);

    /**
     * Finds the wishlist linking the given student to the given course, if any.
     */
    Optional<Wishlist> findOneByStudent_IdAndCourse_Id(Long studentId, Long courseId);

    default Optional<Wishlist> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Wishlist> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Wishlist> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select wishlist from Wishlist wishlist left join fetch wishlist.student",
        countQuery = "select count(wishlist) from Wishlist wishlist"
    )
    Page<Wishlist> findAllWithToOneRelationships(Pageable pageable);

    @Query("select wishlist from Wishlist wishlist left join fetch wishlist.student")
    List<Wishlist> findAllWithToOneRelationships();

    @Query("select wishlist from Wishlist wishlist left join fetch wishlist.student where wishlist.id =:id")
    Optional<Wishlist> findOneWithToOneRelationships(@Param("id") Long id);
}
