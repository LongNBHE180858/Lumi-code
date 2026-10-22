package com.lumi.service.mapper;

import static com.lumi.domain.UserProfileAsserts.*;
import static com.lumi.domain.UserProfileTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserProfileMapperTest {

    private UserProfileMapper userProfileMapper;

    @BeforeEach
    void setUp() {
        userProfileMapper = new UserProfileMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getUserProfileSample1();
        var actual = userProfileMapper.toEntity(userProfileMapper.toDto(expected));
        assertUserProfileAllPropertiesEquals(expected, actual);
    }
}
