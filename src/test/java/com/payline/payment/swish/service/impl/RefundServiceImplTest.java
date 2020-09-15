package com.payline.payment.swish.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.payline.payment.swish.bean.common.response.SwishRefundResponse;
import com.payline.payment.swish.utils.TestUtils;
import com.payline.payment.swish.utils.http.SwishHttpClient;
import com.payline.pmapi.bean.refund.request.RefundRequest;
import com.payline.pmapi.bean.refund.response.RefundResponse;
import com.payline.pmapi.bean.refund.response.impl.RefundResponseFailure;
import com.payline.pmapi.bean.refund.response.impl.RefundResponseSuccess;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class RefundServiceImplTest {

    private final static Gson parser = new GsonBuilder().create();

    private static final String DEFAULT_TRANSACTION_ID = "123456789";
    private static final String DEFAULT_PARTNER_TRANSACTION_ID = "987654321";
    private static final String templateResponse = "{" +
            "     \"id\": \"123456789\"," +
            "     \"payerPaymentReference\": \"0123456789\"," +
            "     \"originalPaymentReference\": \"6D6CD7406ECE4542A80152D909EF9F6B\"," +
            "     \"callbackUrl\": \"https://example.com/api/swishcb/refunds\"," +
            "     \"payerAlias\": \"1231234567890\"," +
            "     \"payeeAlias\": \"07211234567\"," +
            "     \"amount\": \"100\",\"currency\": \"SEK\"," +
            "     \"message\": \"Refund for Kingston USB Flash Drive 8 GB\"," +
            "     \"status\": \"XXX\"," +
            "     \"dateCreated\": \"2015-02-19T22:01:53+01:00\"," +
            "     \"datePaid\": \"2015-02-19T22:03:53+01:00\"" +
            "}";
    private static final String defaultResponseOK = templateResponse.replace("XXX", "PAID");
    private static final String responseDebited = templateResponse.replace("XXX", "DEBITED");
    private static final String responseError = templateResponse.replace("XXX", "ERROR");


    @InjectMocks
    RefundServiceImpl service;
    @Mock
    private SwishHttpClient client;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void refundRequestTestOK() {
        RefundRequest request = TestUtils.createRefundRequest(DEFAULT_TRANSACTION_ID, DEFAULT_PARTNER_TRANSACTION_ID);

        // Mock first call
        doReturn(DEFAULT_TRANSACTION_ID).when(client).createRefund(any());

        // Mock the second call to get a PAID response status
        SwishRefundResponse refundResponse = parser.fromJson(defaultResponseOK, SwishRefundResponse.class);
        doReturn(refundResponse).when(client).getRefundStatus(any(), anyString());

        RefundResponse response = service.refundRequest(request);
        Assertions.assertEquals(RefundResponseSuccess.class, response.getClass());
        RefundResponseSuccess responseSuccess = (RefundResponseSuccess) response;
        Assertions.assertEquals(DEFAULT_TRANSACTION_ID, responseSuccess.getPartnerTransactionId());
    }

    @Test
    void refundRequestTestDEBITED() {
        RefundRequest request = TestUtils.createRefundRequest(DEFAULT_TRANSACTION_ID, DEFAULT_PARTNER_TRANSACTION_ID);

        // Mock first call
        doReturn(DEFAULT_TRANSACTION_ID).when(client).createRefund(any());

        // Mock the second call to get a DEBITED response status
        SwishRefundResponse refundResponse = parser.fromJson(responseDebited, SwishRefundResponse.class);
        doReturn(refundResponse).when(client).getRefundStatus(any(), anyString());

        RefundResponse response = service.refundRequest(request);
        Assertions.assertEquals(RefundResponseFailure.class, response.getClass());
        RefundResponseFailure responseFailure = (RefundResponseFailure) response;
        Assertions.assertEquals(DEFAULT_TRANSACTION_ID, responseFailure.getPartnerTransactionId());
        verify(client, times(10)).getRefundStatus(any(), anyString());
    }

    @Test
    void refundRequestTestERROR() throws Exception {
        RefundRequest request = TestUtils.createRefundRequest(DEFAULT_TRANSACTION_ID, DEFAULT_PARTNER_TRANSACTION_ID);

        // Mock first call
        doReturn(DEFAULT_TRANSACTION_ID).when(client).createRefund(any());

        // Mock the second call to get a ERROR response status
        SwishRefundResponse refundResponse = parser.fromJson(responseError, SwishRefundResponse.class);
        doReturn(refundResponse).when(client).getRefundStatus(any(), anyString());

        RefundResponse response = service.refundRequest(request);
        Assertions.assertEquals(RefundResponseFailure.class, response.getClass());
        RefundResponseFailure responseFailure = (RefundResponseFailure) response;
        Assertions.assertEquals(DEFAULT_TRANSACTION_ID, responseFailure.getPartnerTransactionId());
    }

    @Test
    void canMultiple() {
        Assertions.assertTrue(service.canMultiple());
    }

    @Test
    void canPartial() {
        Assertions.assertTrue(service.canPartial());
    }

}
