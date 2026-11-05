package com.lumi.repository;

import com.lumi.domain.InstructorWallet;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the InstructorWallet entity.
 */
@Repository
public interface InstructorWalletRepository extends JpaRepository<InstructorWallet, Long>, JpaSpecificationExecutor<InstructorWallet> {
    default Optional<InstructorWallet> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<InstructorWallet> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<InstructorWallet> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select instructorWallet from InstructorWallet instructorWallet left join fetch instructorWallet.instructor",
        countQuery = "select count(instructorWallet) from InstructorWallet instructorWallet"
    )
    Page<InstructorWallet> findAllWithToOneRelationships(Pageable pageable);

    @Query("select instructorWallet from InstructorWallet instructorWallet left join fetch instructorWallet.instructor")
    List<InstructorWallet> findAllWithToOneRelationships();

    @Query(
        "select instructorWallet from InstructorWallet instructorWallet left join fetch instructorWallet.instructor where instructorWallet.id =:id"
    )
    Optional<InstructorWallet> findOneWithToOneRelationships(@Param("id") Long id);
}
