package com.lumi.repository;

import com.lumi.domain.User;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the {@link User} entity.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    String USERS_BY_LOGIN_CACHE = "usersByLogin";

    String USERS_BY_EMAIL_CACHE = "usersByEmail";

    Optional<User> findOneByActivationKey(String activationKey);

    List<User> findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(Instant dateTime);

    Optional<User> findOneByResetKey(String resetKey);

    @Cacheable(cacheNames = USERS_BY_EMAIL_CACHE, unless = "#result == null")
    @EntityGraph(attributePaths = "authorities")
    Optional<User> findOneWithAuthoritiesByEmailIgnoreCase(String email);

    Optional<User> findOneByEmailIgnoreCase(String email);

    @Cacheable(cacheNames = USERS_BY_LOGIN_CACHE, unless = "#result == null")
    @EntityGraph(attributePaths = "authorities")
    Optional<User> findOneWithAuthoritiesByLogin(String login);

    Optional<User> findOneByLogin(String login);

    @EntityGraph(attributePaths = "authorities")
    Optional<User> findOneWithAuthoritiesById(Long id);

    Page<User> findAllByIdNotNullAndActivatedIsTrue(Pageable pageable);

    @Query(
        """
        select u from User u
        where (lower(u.firstName) like :search escape '!'
               or lower(u.lastName) like :search escape '!'
               or lower(concat(concat(coalesce(u.firstName, ''), ' '), coalesce(u.lastName, ''))) like :search escape '!'
               or lower(concat(concat(coalesce(u.lastName, ''), ' '), coalesce(u.firstName, ''))) like :search escape '!'
               or lower(u.email) like :search escape '!')
          and (:role = ''
           or (:role = 'ROLE_ADMIN' and exists (select a from u.authorities a where a.name = 'ROLE_ADMIN'))
           or (:role = 'ROLE_INSTRUCTOR'
               and exists (select a from u.authorities a where a.name = 'ROLE_INSTRUCTOR')
               and not exists (select a from u.authorities a where a.name = 'ROLE_ADMIN'))
           or (:role = 'ROLE_PARENT'
               and exists (select a from u.authorities a where a.name = 'ROLE_PARENT')
               and not exists (select a from u.authorities a where a.name in ('ROLE_ADMIN', 'ROLE_INSTRUCTOR')))
           or (:role = 'ROLE_STUDENT'
               and not exists (select a from u.authorities a where a.name in ('ROLE_ADMIN', 'ROLE_INSTRUCTOR', 'ROLE_PARENT'))))
        """
    )
    Page<User> findAllByManagedRoleAndSearch(@Param("role") String role, @Param("search") String search, Pageable pageable);
}
