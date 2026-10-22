package com.lumi.repository;

import com.lumi.domain.EmailOtp;
import com.lumi.domain.enumeration.OtpPurpose;
import java.time.Instant;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the {@link EmailOtp} entity.
 */
@Repository
public interface EmailOtpRepository extends JpaRepository<EmailOtp, Long> {
    /**
     * Find latest active OTP by email and purpose.
     */
    Optional<EmailOtp> findTopByEmailIgnoreCaseAndPurposeAndIsUsedFalseOrderByCreatedAtDesc(String email, OtpPurpose purpose);

    /**
     * Find latest OTP by email and purpose (regardless of used/expired) to calculate cooldown.
     */
    Optional<EmailOtp> findTopByEmailIgnoreCaseAndPurposeOrderByCreatedAtDesc(String email, OtpPurpose purpose);
}
