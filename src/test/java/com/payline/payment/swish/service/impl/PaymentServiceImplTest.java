package com.payline.payment.swish.service.impl;

import com.payline.payment.swish.exception.InvalidDataException;
import com.payline.payment.swish.utils.TestUtils;
import com.payline.payment.swish.utils.http.SwishHttpClient;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseActiveWaiting;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFailure;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

class PaymentServiceImplTest {


    @InjectMocks
    PaymentServiceImpl service;

    @Mock
    private SwishHttpClient client;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void paymentRequestOK() {
        // Mock the http call
        doReturn(TestUtils.TRANSACTION_ID).when(client).createTransaction(any());

        PaymentRequest request = TestUtils.createDefaultPaymentRequest();
        PaymentResponse response = service.paymentRequest(request);

        Assertions.assertEquals(PaymentResponseActiveWaiting.class, response.getClass());
    }

    @Test
    void paymentRequestKO() {
        // Mock the http call
        InvalidDataException exception = new InvalidDataException("message");
        doThrow(exception).when(client).createTransaction(any());

        PaymentRequest request = TestUtils.createDefaultPaymentRequest();
        PaymentResponse response = service.paymentRequest(request);

        Assertions.assertEquals(PaymentResponseFailure.class, response.getClass());
    }

}
