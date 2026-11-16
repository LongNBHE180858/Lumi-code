package com.lumi.repository;

import com.lumi.domain.EmailOtp;
import com.lumi.domain.enumeration.OtpPurpose;
import java.time.Instant;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the {@link EmailOtp} entity.
 */
@Repository
public interface EmailOtpRepository extends JpaRepository<EmailOtp, Long> {
    @Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    Optional<EmailOtp> findTopByEmailIgnoreCaseAndPurposeAndIsUsedFalseOrderByCreatedAtDesc(String email, OtpPurpose purpose);

    Optional<EmailOtp> findTopByEmailIgnoreCaseAndPurposeOrderByCreatedAtDesc(String email, OtpPurpose purpose);

    @Modifying
    @Query(
        """
        update EmailOtp o
        set o.isUsed = true
        where lower(o.email) = lower(:email)
          and o.purpose = :purpose
          and o.isUsed = false
        """
    )
    int invalidateUnused(@Param("email") String email, @Param("purpose") OtpPurpose purpose);

    @Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @Query("select o from EmailOtp o where o.loginChallengeId = :challengeId and o.purpose = :purpose")
    Optional<EmailOtp> findChallengeForUpdate(@Param("challengeId") String challengeId, @Param("purpose") OtpPurpose purpose);
}
