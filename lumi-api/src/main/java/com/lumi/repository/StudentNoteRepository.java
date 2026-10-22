package com.lumi.repository;

import com.lumi.domain.StudentNote;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the StudentNote entity.
 */
@Repository
public interface StudentNoteRepository extends JpaRepository<StudentNote, Long>, JpaSpecificationExecutor<StudentNote> {
    @Query("select studentNote from StudentNote studentNote where studentNote.student.login = ?#{authentication.name}")
    List<StudentNote> findByStudentIsCurrentUser();

    default Optional<StudentNote> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<StudentNote> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<StudentNote> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select studentNote from StudentNote studentNote left join fetch studentNote.student left join fetch studentNote.lesson",
        countQuery = "select count(studentNote) from StudentNote studentNote"
    )
    Page<StudentNote> findAllWithToOneRelationships(Pageable pageable);

    @Query("select studentNote from StudentNote studentNote left join fetch studentNote.student left join fetch studentNote.lesson")
    List<StudentNote> findAllWithToOneRelationships();

    @Query(
        "select studentNote from StudentNote studentNote left join fetch studentNote.student left join fetch studentNote.lesson where studentNote.id =:id"
    )
    Optional<StudentNote> findOneWithToOneRelationships(@Param("id") Long id);
}
