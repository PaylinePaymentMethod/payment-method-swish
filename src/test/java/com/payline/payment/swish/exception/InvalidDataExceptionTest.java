package com.payline.payment.swish.exception;

import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFailure;
import com.payline.pmapi.bean.refund.response.impl.RefundResponseFailure;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class InvalidDataExceptionTest {

    private InvalidDataException invalidDataException = new InvalidDataException("test");

    @Test
    void getFailureCause() {
        Assertions.assertEquals(FailureCause.INVALID_DATA, invalidDataException.getFailureCause());
    }


    @Test
    void toPaymentResponseFailure() {
        invalidDataException = new InvalidDataException("errorCodeOrLabelerrorCodeOrLabelerrorCodeOrLabelerrorCodeOrLabelerrorCodeOrLabel");

        PaymentResponseFailure paymentResponseFailure = invalidDataException.toPaymentResponseFailureBuilder().build();
        Assertions.assertEquals(FailureCause.INVALID_DATA, paymentResponseFailure.getFailureCause());
        Assertions.assertTrue(paymentResponseFailure.getErrorCode().contains("errorCodeOrLabel"));
        Assertions.assertEquals(50, paymentResponseFailure.getErrorCode().length());
    }

    @Test
    void toRefundResponseFailure() {
        invalidDataException = new InvalidDataException("errorCodeOrLabelerrorCodeOrLabelerrorCodeOrLabelerrorCodeOrLabelerrorCodeOrLabel");

        RefundResponseFailure refundResponseFailure = invalidDataException.toRefundResponseFailureBuilder().build();
        Assertions.assertEquals(FailureCause.INVALID_DATA, refundResponseFailure.getFailureCause());
        Assertions.assertTrue(refundResponseFailure.getErrorCode().contains("errorCodeOrLabel"));
        Assertions.assertEquals(50, refundResponseFailure.getErrorCode().length());
    }
}