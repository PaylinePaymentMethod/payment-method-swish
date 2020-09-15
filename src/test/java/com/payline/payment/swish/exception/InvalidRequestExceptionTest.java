package com.payline.payment.swish.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class InvalidRequestExceptionTest {


    @Test
    void test() {
        InvalidRequestException invalidRequestException = new InvalidRequestException("test");
        Assertions.assertEquals("test", invalidRequestException.getMessage());
    }
}