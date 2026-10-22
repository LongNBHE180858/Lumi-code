package com.lumi.repository;

import com.lumi.domain.Authority;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Authority entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AuthorityRepository extends JpaRepository<Authority, String> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select authority from Authority authority where authority.name = :name")
    Optional<Authority> lockByName(@Param("name") String name);
}
