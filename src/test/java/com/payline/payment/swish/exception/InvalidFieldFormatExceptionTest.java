package com.payline.payment.swish.exception;

import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFailure;
import com.payline.pmapi.bean.refund.response.impl.RefundResponseFailure;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class InvalidFieldFormatExceptionTest {

    private InvalidFieldFormatException invalidFieldFormatException;

    @Test
    void getFailureCause() {
        Assertions.assertEquals(FailureCause.INVALID_FIELD_FORMAT, invalidFieldFormatException.getFailureCause());
    }


    @Test
    void toPaymentResponseFailure() {
        invalidFieldFormatException = new InvalidFieldFormatException("errorCodeOrLabelerrorCodeOrLabelerrorCodeOrLabelerrorCodeOrLabelerrorCodeOrLabel");

        PaymentResponseFailure paymentResponseFailure = invalidFieldFormatException.toPaymentResponseFailureBuilder().build();
        Assertions.assertEquals(FailureCause.INVALID_FIELD_FORMAT, paymentResponseFailure.getFailureCause());
        Assertions.assertTrue(paymentResponseFailure.getErrorCode().contains("errorCodeOrLabel"));
        Assertions.assertEquals(50, paymentResponseFailure.getErrorCode().length());
    }

    @Test
    void toRefundResponseFailure() {
        invalidFieldFormatException = new InvalidFieldFormatException("errorCodeOrLabelerrorCodeOrLabelerrorCodeOrLabelerrorCodeOrLabelerrorCodeOrLabel");

        RefundResponseFailure refundResponseFailure = invalidFieldFormatException.toRefundResponseFailureBuilder().build();
        Assertions.assertEquals(FailureCause.INVALID_FIELD_FORMAT, refundResponseFailure.getFailureCause());
        Assertions.assertTrue(refundResponseFailure.getErrorCode().contains("errorCodeOrLabel"));
        Assertions.assertEquals(50, refundResponseFailure.getErrorCode().length());
    }
}