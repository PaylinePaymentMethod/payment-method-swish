package com.payline.payment.swish.service.impl;

import com.payline.payment.swish.utils.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Map;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TransactionManagerServiceImplTest {

    private TransactionManagerServiceImpl transactionManagerService;

    @BeforeAll
    void setUp() {
        transactionManagerService = new TransactionManagerServiceImpl();
    }

    @Test
    void readAdditionalData() {
        Map<String, String> additionalData = transactionManagerService.readAdditionalData(TestUtils.PAYER_PHONE, null);
        Assertions.assertNotNull(additionalData);
        Assertions.assertEquals(1, additionalData.size());
    }

    @Test
    void readAdditionalDataNull() {
        Map<String, String> additionalData = transactionManagerService.readAdditionalData(null, null);
        Assertions.assertNotNull(additionalData);
    }
}