package com.lumi.repository;

import com.lumi.domain.AuthSession;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the AuthSession entity.
 */
@Repository
public interface AuthSessionRepository extends JpaRepository<AuthSession, Long>, JpaSpecificationExecutor<AuthSession> {
    @Query("select authSession from AuthSession authSession where authSession.user.login = ?#{authentication.name}")
    List<AuthSession> findByUserIsCurrentUser();

    default Optional<AuthSession> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<AuthSession> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<AuthSession> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select authSession from AuthSession authSession left join fetch authSession.user",
        countQuery = "select count(authSession) from AuthSession authSession"
    )
    Page<AuthSession> findAllWithToOneRelationships(Pageable pageable);

    @Query("select authSession from AuthSession authSession left join fetch authSession.user")
    List<AuthSession> findAllWithToOneRelationships();

    @Query("select authSession from AuthSession authSession left join fetch authSession.user where authSession.id =:id")
    Optional<AuthSession> findOneWithToOneRelationships(@Param("id") Long id);

    Optional<AuthSession> findOneByRefreshToken(String refreshToken);

    List<AuthSession> findAllByUserAndIsRevokedFalse(com.lumi.domain.User user);

    List<AuthSession> findAllByUserAndIsRevokedFalseOrderByCreatedAtDesc(com.lumi.domain.User user);

    List<AuthSession> findAllByUserOrderByCreatedAtDesc(com.lumi.domain.User user);

    Optional<AuthSession> findByIdAndUser(Long id, com.lumi.domain.User user);

    List<AuthSession> findAllByUserAndIdNotAndIsRevokedFalse(com.lumi.domain.User user, Long excludedId);
}
