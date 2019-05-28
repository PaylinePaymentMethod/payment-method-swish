package com.payline.payment.swish.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.payline.payment.swish.bean.common.response.SwishRefundResponse;
import com.payline.payment.swish.exception.InvalidDataException;
import com.payline.payment.swish.service.impl.RefundServiceImpl;
import com.payline.payment.swish.utils.http.SwishHttpClient;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.refund.request.RefundRequest;
import com.payline.pmapi.bean.refund.response.RefundResponse;
import com.payline.pmapi.bean.refund.response.impl.RefundResponseFailure;
import com.payline.pmapi.bean.refund.response.impl.RefundResponseSuccess;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Tested;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import com.payline.payment.swish.utils.TestUtils;

public class RefundServiceImplTest {

    private static Gson parser = new GsonBuilder().create();

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


    @Tested
    public RefundServiceImpl service;
    @Mocked
    private SwishHttpClient client;

    @Test
    void refundRequestTestOK() throws Exception {
        RefundRequest request = TestUtils.createRefundRequest(DEFAULT_TRANSACTION_ID, DEFAULT_PARTNER_TRANSACTION_ID);

        // Mock first call
        new Expectations() {{
            client.createRefund((RefundRequest) any);
            result = DEFAULT_TRANSACTION_ID;
        }};

        // Mock the second call to get a PAID response status
        new Expectations() {{
            client.getRefundStatus((RefundRequest) any, anyString);
            result = parser.fromJson(defaultResponseOK, SwishRefundResponse.class);
        }};

        RefundResponse response = service.refundRequest(request);
        Assertions.assertEquals(RefundResponseSuccess.class, response.getClass());
        RefundResponseSuccess responseSuccess = (RefundResponseSuccess) response;
        Assertions.assertEquals(DEFAULT_TRANSACTION_ID, responseSuccess.getPartnerTransactionId());
    }

    @Test
    void refundRequestTestDEBITED() throws Exception {
        RefundRequest request = TestUtils.createRefundRequest(DEFAULT_TRANSACTION_ID, DEFAULT_PARTNER_TRANSACTION_ID);

        // Mock first call
        new Expectations() {{
            client.createRefund((RefundRequest) any);
            result = DEFAULT_TRANSACTION_ID;
        }};

        // Mock the second call to get a DEBITED response status
        new Expectations() {{
            client.getRefundStatus((RefundRequest) any, anyString);
            result = parser.fromJson(responseDebited, SwishRefundResponse.class);
            times = 10;
        }};

        RefundResponse response = service.refundRequest(request);
        Assertions.assertEquals(RefundResponseFailure.class, response.getClass());
        RefundResponseFailure responseFailure = (RefundResponseFailure) response;
        Assertions.assertEquals(DEFAULT_TRANSACTION_ID, responseFailure.getPartnerTransactionId());
    }

    @Test
    void refundRequestTestERROR() throws Exception {
        RefundRequest request = TestUtils.createRefundRequest(DEFAULT_TRANSACTION_ID, DEFAULT_PARTNER_TRANSACTION_ID);

        // Mock first call
        new Expectations() {{
            client.createRefund((RefundRequest) any);
            result = DEFAULT_TRANSACTION_ID;
        }};

        // Mock the second call to get a ERROR response status
        new Expectations() {{
            client.getRefundStatus((RefundRequest) any, anyString);
            result = parser.fromJson(responseError, SwishRefundResponse.class);
        }};

        RefundResponse response = service.refundRequest(request);
        Assertions.assertEquals(RefundResponseFailure.class, response.getClass());
        RefundResponseFailure responseFailure = (RefundResponseFailure) response;
        Assertions.assertEquals(DEFAULT_TRANSACTION_ID, responseFailure.getPartnerTransactionId());
    }


    @Test
    void refundRequestTestException() throws Exception {
        // First, test InvalidDataException
        RefundRequest request = TestUtils.createRefundRequest(DEFAULT_TRANSACTION_ID, DEFAULT_PARTNER_TRANSACTION_ID);

        // Mock first call
        new Expectations() {{
            client.createRefund((RefundRequest) any);
            result = new InvalidDataException("this is an invalidDataException message", "field");
        }};

        RefundResponse response = service.refundRequest(request);
        Assertions.assertEquals(RefundResponseFailure.class, response.getClass());
        RefundResponseFailure responseFailure = (RefundResponseFailure) response;
        Assertions.assertEquals("UNKNOWN", responseFailure.getPartnerTransactionId());
        Assertions.assertEquals(FailureCause.INVALID_DATA, responseFailure.getFailureCause());


        // Then, test InterruptedException
        request = TestUtils.createRefundRequest(DEFAULT_TRANSACTION_ID, DEFAULT_PARTNER_TRANSACTION_ID);

        // Mock first call
        new Expectations() {{
            client.createRefund((RefundRequest) any);
            result = DEFAULT_TRANSACTION_ID;

        }};

        // Mock the waiting to throw an Exception
        new Expectations() {{
            client.getRefundStatus((RefundRequest) any, anyString);
            result = new InterruptedException("this is an interruption message");
        }};

        response = service.refundRequest(request);
        Assertions.assertEquals(RefundResponseFailure.class, response.getClass());
        responseFailure = (RefundResponseFailure) response;
        Assertions.assertEquals(DEFAULT_TRANSACTION_ID, responseFailure.getPartnerTransactionId());
        Assertions.assertEquals(FailureCause.INTERNAL_ERROR, responseFailure.getFailureCause());
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
