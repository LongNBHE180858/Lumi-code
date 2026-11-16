package com.lumi.security.jwt;

import com.lumi.domain.AuthSession;
import com.lumi.domain.User;
import com.lumi.repository.AuthSessionRepository;
import com.lumi.repository.UserRepository;
import java.time.Instant;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

/** Reject access tokens as soon as their account is deactivated. */
public class ActiveUserJwtDecoder implements JwtDecoder {

    private final JwtDecoder delegate;
    private final UserRepository userRepository;
    private final AuthSessionRepository authSessionRepository;

    public ActiveUserJwtDecoder(JwtDecoder delegate, UserRepository userRepository, AuthSessionRepository authSessionRepository) {
        this.delegate = delegate;
        this.userRepository = userRepository;
        this.authSessionRepository = authSessionRepository;
    }

    @Override
    public Jwt decode(String token) {
        Jwt jwt = delegate.decode(token);
        String login = jwt.getSubject();
        Number sessionClaim = jwt.getClaim("sid");
        if (login == null || sessionClaim == null) {
            throw new BadJwtException("Token không gắn với phiên đăng nhập");
        }
        User user = userRepository
            .findOneByLogin(login)
            .filter(User::isActivated)
            .orElseThrow(() -> new BadJwtException("User account is inactive or missing"));
        AuthSession session = authSessionRepository
            .findById(sessionClaim.longValue())
            .orElseThrow(() -> new BadJwtException("Session not found"));
        if (
            !session.getUser().getId().equals(user.getId()) ||
            Boolean.TRUE.equals(session.getIsRevoked()) ||
            session.getExpiresAt() == null ||
            !session.getExpiresAt().isAfter(Instant.now())
        ) {
            throw new BadJwtException("User account is inactive or missing");
        }
        return jwt;
    }
}
