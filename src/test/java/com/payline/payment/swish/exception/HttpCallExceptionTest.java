package com.payline.payment.swish.exception;

import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFailure;
import com.payline.pmapi.bean.refund.response.impl.RefundResponseFailure;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class HttpCallExceptionTest {

    @Test
    void toPaymentResponseFailure() {
        HttpCallException httpCallException = new HttpCallException("errorCodeOrLabelerrorCodeOrLabelerrorCodeOrLabelerrorCodeOrLabelerrorCodeOrLabel");

        PaymentResponseFailure paymentResponseFailure = httpCallException.toPaymentResponseFailureBuilder().build();
        Assertions.assertEquals(FailureCause.COMMUNICATION_ERROR, paymentResponseFailure.getFailureCause());
        Assertions.assertTrue(paymentResponseFailure.getErrorCode().contains("errorCodeOrLabel"));
        Assertions.assertEquals(50, paymentResponseFailure.getErrorCode().length());
    }

    @Test
    void toRefundResponseFailure() {
        HttpCallException httpCallException = new HttpCallException( "errorCodeOrLabelerrorCodeOrLabelerrorCodeOrLabelerrorCodeOrLabelerrorCodeOrLabel");

        RefundResponseFailure refundResponseFailure = httpCallException.toRefundResponseFailureBuilder().build();
        Assertions.assertEquals(FailureCause.COMMUNICATION_ERROR, refundResponseFailure.getFailureCause());
        Assertions.assertTrue(refundResponseFailure.getErrorCode().contains("errorCodeOrLabel"));
        Assertions.assertEquals(50, refundResponseFailure.getErrorCode().length());
    }
}