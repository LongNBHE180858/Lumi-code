package com.lumi.repository;

import com.lumi.domain.ClassMaterial;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ClassMaterial entity.
 */
@Repository
public interface ClassMaterialRepository extends JpaRepository<ClassMaterial, Long>, JpaSpecificationExecutor<ClassMaterial> {
    default Optional<ClassMaterial> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<ClassMaterial> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<ClassMaterial> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select classMaterial from ClassMaterial classMaterial left join fetch classMaterial.studyClass",
        countQuery = "select count(classMaterial) from ClassMaterial classMaterial"
    )
    Page<ClassMaterial> findAllWithToOneRelationships(Pageable pageable);

    @Query("select classMaterial from ClassMaterial classMaterial left join fetch classMaterial.studyClass")
    List<ClassMaterial> findAllWithToOneRelationships();

    @Query("select classMaterial from ClassMaterial classMaterial left join fetch classMaterial.studyClass where classMaterial.id =:id")
    Optional<ClassMaterial> findOneWithToOneRelationships(@Param("id") Long id);
}
