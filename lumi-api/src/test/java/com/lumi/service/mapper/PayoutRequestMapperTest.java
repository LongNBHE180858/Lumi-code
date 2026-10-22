package com.lumi.service.mapper;

import static com.lumi.domain.PayoutRequestAsserts.*;
import static com.lumi.domain.PayoutRequestTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PayoutRequestMapperTest {

    private PayoutRequestMapper payoutRequestMapper;

    @BeforeEach
    void setUp() {
        payoutRequestMapper = new PayoutRequestMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPayoutRequestSample1();
        var actual = payoutRequestMapper.toEntity(payoutRequestMapper.toDto(expected));
        assertPayoutRequestAllPropertiesEquals(expected, actual);
    }
}
