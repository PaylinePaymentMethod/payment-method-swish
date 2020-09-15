package com.payline.payment.swish.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DataCheckerTest {


    @Test
    void isEmpty() {
        Assertions.assertTrue(DataChecker.isEmpty(null));
        Assertions.assertTrue(DataChecker.isEmpty(""));
        Assertions.assertFalse(DataChecker.isEmpty(" "));

    }

}