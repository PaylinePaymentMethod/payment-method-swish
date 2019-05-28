package com.payline.payment.swish.exception;

import com.payline.payment.swish.exception.InvalidFieldFormatException;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFailure;
import com.payline.pmapi.bean.refund.response.impl.RefundResponseFailure;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class InvalidFieldFormatExceptionTest {

    private InvalidFieldFormatException invalidFieldFormatException;


    @BeforeAll
    void setUp() {

        invalidFieldFormatException = new InvalidFieldFormatException("test", "field");
        Assertions.assertEquals("field", invalidFieldFormatException.getErrorCodeOrLabel());
        Assertions.assertEquals("test", invalidFieldFormatException.getMessage());
    }

    @Test
    void getFailureCause() {
        Assertions.assertEquals(FailureCause.INVALID_FIELD_FORMAT, invalidFieldFormatException.getFailureCause());
    }


    @Test
    void toPaymentResponseFailure() {
        invalidFieldFormatException = new InvalidFieldFormatException((String) null, "errorCodeOrLabelerrorCodeOrLabelerrorCodeOrLabelerrorCodeOrLabelerrorCodeOrLabel");

        PaymentResponseFailure paymentResponseFailure = invalidFieldFormatException.toPaymentResponseFailure();
        Assertions.assertEquals(FailureCause.INVALID_FIELD_FORMAT, paymentResponseFailure.getFailureCause());
        Assertions.assertTrue(paymentResponseFailure.getErrorCode().contains("errorCodeOrLabel"));
        Assertions.assertEquals(50, paymentResponseFailure.getErrorCode().length());
    }

    @Test
    void toRefundResponseFailure() {
        invalidFieldFormatException = new InvalidFieldFormatException((String) null, "errorCodeOrLabelerrorCodeOrLabelerrorCodeOrLabelerrorCodeOrLabelerrorCodeOrLabel");

        RefundResponseFailure refundResponseFailure = invalidFieldFormatException.toRefundResponseFailure();
        Assertions.assertEquals(FailureCause.INVALID_FIELD_FORMAT, refundResponseFailure.getFailureCause());
        Assertions.assertTrue(refundResponseFailure.getErrorCode().contains("errorCodeOrLabel"));
        Assertions.assertEquals(50, refundResponseFailure.getErrorCode().length());
    }
}