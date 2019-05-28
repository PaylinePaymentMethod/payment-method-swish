package com.payline.payment.swish.exception;

import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFailure;
import com.payline.pmapi.bean.refund.response.impl.RefundResponseFailure;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HttpCallExceptionTest {

    private HttpCallException httpCallException;

    @BeforeEach
    void setUp() {
        httpCallException = new HttpCallException(new Exception("test"), "origin");
        Assertions.assertEquals("origin", httpCallException.getErrorCodeOrLabel());
        Assertions.assertEquals("test", httpCallException.getMessage());

    }

    @Test
    void getFailureCause() {
        Assertions.assertEquals(FailureCause.COMMUNICATION_ERROR, httpCallException.getFailureCause());
    }


    @Test
    void toPaymentResponseFailure() {
        httpCallException = new HttpCallException((String) null, "errorCodeOrLabelerrorCodeOrLabelerrorCodeOrLabelerrorCodeOrLabelerrorCodeOrLabel");

        PaymentResponseFailure paymentResponseFailure = httpCallException.toPaymentResponseFailure();
        Assertions.assertEquals(FailureCause.COMMUNICATION_ERROR, paymentResponseFailure.getFailureCause());
        Assertions.assertTrue(paymentResponseFailure.getErrorCode().contains("errorCodeOrLabel"));
        Assertions.assertEquals(50, paymentResponseFailure.getErrorCode().length());
    }

    @Test
    void toRefundResponseFailure() {
        httpCallException = new HttpCallException((String) null, "errorCodeOrLabelerrorCodeOrLabelerrorCodeOrLabelerrorCodeOrLabelerrorCodeOrLabel");

        RefundResponseFailure refundResponseFailure = httpCallException.toRefundResponseFailure();
        Assertions.assertEquals(FailureCause.COMMUNICATION_ERROR, refundResponseFailure.getFailureCause());
        Assertions.assertTrue(refundResponseFailure.getErrorCode().contains("errorCodeOrLabel"));
        Assertions.assertEquals(50, refundResponseFailure.getErrorCode().length());
    }
}