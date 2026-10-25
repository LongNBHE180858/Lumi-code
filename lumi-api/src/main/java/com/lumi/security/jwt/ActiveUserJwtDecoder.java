package com.lumi.security.jwt;

import com.lumi.domain.User;
import com.lumi.repository.UserRepository;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

/** Reject access tokens as soon as their account is deactivated. */
public class ActiveUserJwtDecoder implements JwtDecoder {

    private final JwtDecoder delegate;
    private final UserRepository userRepository;

    public ActiveUserJwtDecoder(JwtDecoder delegate, UserRepository userRepository) {
        this.delegate = delegate;
        this.userRepository = userRepository;
    }

    @Override
    public Jwt decode(String token) {
        Jwt jwt = delegate.decode(token);
        String login = jwt.getSubject();
        if (login == null || userRepository.findOneByLogin(login).filter(User::isActivated).isEmpty()) {
            throw new BadJwtException("User account is inactive or missing");
        }
        return jwt;
    }
}
