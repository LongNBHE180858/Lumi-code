package com.lumi.service.mapper;

import static com.lumi.domain.TicketMessageAsserts.*;
import static com.lumi.domain.TicketMessageTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TicketMessageMapperTest {

    private TicketMessageMapper ticketMessageMapper;

    @BeforeEach
    void setUp() {
        ticketMessageMapper = new TicketMessageMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTicketMessageSample1();
        var actual = ticketMessageMapper.toEntity(ticketMessageMapper.toDto(expected));
        assertTicketMessageAllPropertiesEquals(expected, actual);
    }
}
