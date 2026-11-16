package com.lumi.security.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.lumi.domain.AuthSession;
import com.lumi.domain.User;
import com.lumi.repository.AuthSessionRepository;
import com.lumi.repository.UserRepository;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

class ActiveUserJwtDecoderTest {

    private final JwtDecoder delegate = mock(JwtDecoder.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final AuthSessionRepository authSessionRepository = mock(AuthSessionRepository.class);
    private final ActiveUserJwtDecoder decoder = new ActiveUserJwtDecoder(delegate, userRepository, authSessionRepository);

    @Test
    void acceptsTokenForActiveUser() {
        Jwt jwt = jwtFor("student");
        User user = new User();
        user.setId(7L);
        user.setActivated(true);
        AuthSession session = new AuthSession();
        session.setUser(user);
        session.setIsRevoked(false);
        session.setExpiresAt(Instant.now().plusSeconds(600));
        when(delegate.decode("token")).thenReturn(jwt);
        when(userRepository.findOneByLogin("student")).thenReturn(Optional.of(user));
        when(authSessionRepository.findById(42L)).thenReturn(Optional.of(session));

        assertThat(decoder.decode("token")).isSameAs(jwt);
        verify(userRepository).findOneByLogin("student");
    }

    @Test
    void rejectsTokenForDeactivatedUser() {
        User user = new User();
        user.setActivated(false);
        when(delegate.decode("token")).thenReturn(jwtFor("student"));
        when(userRepository.findOneByLogin("student")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> decoder.decode("token")).isInstanceOf(BadJwtException.class);
    }

    @Test
    void rejectsTokenForMissingUser() {
        when(delegate.decode("token")).thenReturn(jwtFor("student"));
        when(userRepository.findOneByLogin("student")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> decoder.decode("token")).isInstanceOf(BadJwtException.class);
    }

    @Test
    void rejectsTokenAfterSessionIsRevoked() {
        User user = new User();
        user.setId(7L);
        user.setActivated(true);
        AuthSession session = new AuthSession();
        session.setUser(user);
        session.setIsRevoked(true);
        session.setExpiresAt(Instant.now().plusSeconds(600));
        when(delegate.decode("token")).thenReturn(jwtFor("student"));
        when(userRepository.findOneByLogin("student")).thenReturn(Optional.of(user));
        when(authSessionRepository.findById(42L)).thenReturn(Optional.of(session));

        assertThatThrownBy(() -> decoder.decode("token")).isInstanceOf(BadJwtException.class);
    }

    private static Jwt jwtFor(String login) {
        return Jwt.withTokenValue("token").header("alg", "HS512").subject(login).claim("sid", 42L).build();
    }
}
