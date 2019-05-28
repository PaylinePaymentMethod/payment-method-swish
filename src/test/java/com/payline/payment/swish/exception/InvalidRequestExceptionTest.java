package com.payline.payment.swish.exception;

import com.payline.payment.swish.exception.InvalidRequestException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class InvalidRequestExceptionTest {


    @Test
    public void test() {
        InvalidRequestException invalidRequestException = new InvalidRequestException("test");
        Assertions.assertEquals("test", invalidRequestException.getMessage());
    }
}