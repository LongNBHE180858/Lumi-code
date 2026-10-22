package com.lumi.service.mapper;

import static com.lumi.domain.AuthSessionAsserts.*;
import static com.lumi.domain.AuthSessionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AuthSessionMapperTest {

    private AuthSessionMapper authSessionMapper;

    @BeforeEach
    void setUp() {
        authSessionMapper = new AuthSessionMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getAuthSessionSample1();
        var actual = authSessionMapper.toEntity(authSessionMapper.toDto(expected));
        assertAuthSessionAllPropertiesEquals(expected, actual);
    }
}
