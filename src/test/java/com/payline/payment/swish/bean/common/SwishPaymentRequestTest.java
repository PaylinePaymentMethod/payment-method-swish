package com.payline.payment.swish.bean.common;

import com.payline.payment.swish.bean.common.request.RequestFactory;
import com.payline.payment.swish.bean.common.request.SwishPaymentRequest;
import com.payline.payment.swish.exception.InvalidDataException;
import com.payline.payment.swish.utils.TestUtils;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SwishPaymentRequestTest {


    @Test
    void creation() throws InvalidDataException {
        PaymentRequest request = TestUtils.createCompletePaymentBuilder().build();
        SwishPaymentRequest swishPaymentRequest = RequestFactory.fromPaylineRequest(request);

        Assertions.assertNotNull(swishPaymentRequest.getPayeePaymentReference());
        Assertions.assertNotNull(swishPaymentRequest.getCallbackUrl());
        Assertions.assertNotNull(swishPaymentRequest.getPayerAlias());
        Assertions.assertNotNull(swishPaymentRequest.getPayeeAlias());
        Assertions.assertNotNull(swishPaymentRequest.getAmount());
        Assertions.assertNotNull(swishPaymentRequest.getCurrency());
        Assertions.assertNotNull(swishPaymentRequest.getMessage());
    }
}
