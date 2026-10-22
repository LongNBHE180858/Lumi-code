package com.lumi.security;

import static com.lumi.security.SecurityUtils.AUTHORITIES_CLAIM;
import static com.lumi.security.SecurityUtils.JWT_ALGORITHM;
import static com.lumi.security.SecurityUtils.USER_ID_CLAIM;

import com.lumi.domain.AuthSession;
import com.lumi.domain.User;
import com.lumi.repository.AuthSessionRepository;
import com.lumi.security.DomainUserDetailsService.UserWithId;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
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

    public String createAccessToken(Authentication authentication, boolean rememberMe) {
        String authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(" "));

        Instant now = Instant.now();
        Instant validity = rememberMe
            ? now.plus(this.tokenValidityInSecondsForRememberMe, ChronoUnit.SECONDS)
            : now.plus(this.tokenValidityInSeconds, ChronoUnit.SECONDS);

        JwtClaimsSet.Builder builder = JwtClaimsSet.builder()
            .issuedAt(now)
            .expiresAt(validity)
            .subject(authentication.getName())
            .claim(AUTHORITIES_CLAIM, authorities);

        if (authentication.getPrincipal() instanceof UserWithId user) {
            builder.claim(USER_ID_CLAIM, user.getId());
        }

        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, builder.build())).getTokenValue();
    }

    public String createAccessToken(Authentication authentication) {
        return createAccessToken(authentication, false);
    }

    public String createAccessToken(User user, boolean rememberMe) {
        String authorities =
            user.getAuthorities() != null
                ? user.getAuthorities().stream().map(com.lumi.domain.Authority::getName).collect(Collectors.joining(" "))
                : "";

        Instant now = Instant.now();
        Instant validity = rememberMe
            ? now.plus(this.tokenValidityInSecondsForRememberMe, ChronoUnit.SECONDS)
            : now.plus(this.tokenValidityInSeconds, ChronoUnit.SECONDS);

        JwtClaimsSet.Builder builder = JwtClaimsSet.builder()
            .issuedAt(now)
            .expiresAt(validity)
            .subject(user.getLogin())
            .claim(AUTHORITIES_CLAIM, authorities)
            .claim(USER_ID_CLAIM, user.getId());

        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, builder.build())).getTokenValue();
    }

    public String createAccessToken(User user) {
        return createAccessToken(user, false);
    }

    @Transactional
    public String createRefreshToken(User user, String deviceInfo) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(this.tokenValidityInSecondsForRememberMe, ChronoUnit.SECONDS);

        String refreshToken = UUID.randomUUID().toString() + "-" + UUID.randomUUID().toString();

        AuthSession session = new AuthSession()
            .user(user)
            .refreshToken(refreshToken)
            .deviceInfo(deviceInfo != null ? deviceInfo : "Unknown Device")
            .createdAt(now)
            .expiresAt(expiresAt)
            .isRevoked(false);

        authSessionRepository.save(session);

        return refreshToken;
    }

    @Transactional
    public String createRefreshToken(User user) {
        return createRefreshToken(user, "Unknown Device");
    }

    public long getTokenValidityInSeconds(boolean rememberMe) {
        return rememberMe ? this.tokenValidityInSecondsForRememberMe : this.tokenValidityInSeconds;
    }

    public long getTokenValidityInSeconds() {
        return this.tokenValidityInSeconds;
    }
}
