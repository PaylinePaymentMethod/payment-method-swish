package com.payline.payment.swish.service.impl;

import com.payline.payment.swish.exception.InvalidDataException;
import com.payline.payment.swish.service.impl.PaymentServiceImpl;
import com.payline.payment.swish.utils.TestUtils;
import com.payline.payment.swish.utils.http.SwishHttpClient;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseActiveWaiting;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFailure;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Tested;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PaymentServiceImplTest {


    @Tested
    PaymentServiceImpl service;

    @Mocked
    private SwishHttpClient client;





    @Test
    public void paymentRequestOK() throws Exception {
        // Mock the http call
        new Expectations() {{
            client.createTransaction((PaymentRequest) any);
            result = TestUtils.TRANSACTION_ID;
        }};

        PaymentRequest request = TestUtils.createDefaultPaymentRequest();
        PaymentResponse response = service.paymentRequest(request);

        Assertions.assertEquals(PaymentResponseActiveWaiting.class, response.getClass());
    }

    @Test
    public void paymentRequestKO() throws Exception {
        // Mock the http call
        new Expectations() {{
            client.createTransaction((PaymentRequest) any);
            result = new InvalidDataException("message", "field");
        }};

        PaymentRequest request = TestUtils.createDefaultPaymentRequest();
        PaymentResponse response = service.paymentRequest(request);

        Assertions.assertEquals(PaymentResponseFailure.class, response.getClass());
    }

}
