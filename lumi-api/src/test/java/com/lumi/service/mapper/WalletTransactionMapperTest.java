package com.lumi.service.mapper;

import static com.lumi.domain.WalletTransactionAsserts.*;
import static com.lumi.domain.WalletTransactionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WalletTransactionMapperTest {

    private WalletTransactionMapper walletTransactionMapper;

    @BeforeEach
    void setUp() {
        walletTransactionMapper = new WalletTransactionMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getWalletTransactionSample1();
        var actual = walletTransactionMapper.toEntity(walletTransactionMapper.toDto(expected));
        assertWalletTransactionAllPropertiesEquals(expected, actual);
    }
}
