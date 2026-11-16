package com.lumi.security;

import static com.lumi.security.SecurityUtils.AUTHORITIES_CLAIM;
import static com.lumi.security.SecurityUtils.JWT_ALGORITHM;
import static com.lumi.security.SecurityUtils.USER_ID_CLAIM;

import com.lumi.domain.AuthSession;
import com.lumi.domain.User;
import com.lumi.repository.AuthSessionRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TokenProvider {

    private final JwtEncoder jwtEncoder;
    private final AuthSessionRepository authSessionRepository;

    @Value("${jhipster.security.authentication.jwt.token-validity-in-seconds:86400}")
    private long tokenValidityInSeconds;

    @Value("${jhipster.security.authentication.jwt.token-validity-in-seconds-for-remember-me:2592000}")
    private long tokenValidityInSecondsForRememberMe;

    public TokenProvider(JwtEncoder jwtEncoder, AuthSessionRepository authSessionRepository) {
        this.jwtEncoder = jwtEncoder;
        this.authSessionRepository = authSessionRepository;
    }

    public String createAccessToken(User user, Long sessionId) {
        String authorities = user.getAuthorities().stream().map(com.lumi.domain.Authority::getName).collect(Collectors.joining(" "));
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuedAt(now)
            .expiresAt(now.plus(tokenValidityInSeconds, ChronoUnit.SECONDS))
            .subject(user.getLogin())
            .claim(AUTHORITIES_CLAIM, authorities)
            .claim(USER_ID_CLAIM, user.getId())
            .claim("sid", sessionId)
            .build();
        JwsHeader header = JwsHeader.with(JWT_ALGORITHM).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    @Transactional
    public AuthSession createSession(User user, String deviceInfo) {
        Instant now = Instant.now();
        String refreshToken = UUID.randomUUID() + "-" + UUID.randomUUID();
        AuthSession session = new AuthSession()
            .user(user)
            .refreshToken(refreshToken)
            .deviceInfo(deviceInfo == null || deviceInfo.isBlank() ? "Unknown Device" : deviceInfo)
            .createdAt(now)
            .expiresAt(now.plus(tokenValidityInSecondsForRememberMe, ChronoUnit.SECONDS))
            .isRevoked(false);
        return authSessionRepository.saveAndFlush(session);
    }

    public long getTokenValidityInSeconds(boolean rememberMe) {
        return rememberMe ? this.tokenValidityInSecondsForRememberMe : this.tokenValidityInSeconds;
    }

    public long getTokenValidityInSeconds() {
        return this.tokenValidityInSeconds;
    }
}
