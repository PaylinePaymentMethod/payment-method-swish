package com.payline.payment.swish.exception;

import com.payline.payment.swish.exception.PluginTechnicalException;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFailure;
import com.payline.pmapi.bean.paymentform.response.configuration.impl.PaymentFormConfigurationResponseFailure;
import com.payline.pmapi.bean.refund.response.impl.RefundResponseFailure;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class PluginTechnicalExceptionTest {


    private PluginTechnicalException pluginTechnicalException;


    @Test
    void constructeur01() {
        pluginTechnicalException = new PluginTechnicalException("message", "errorCodeOrLabel");
        Assertions.assertEquals("message", pluginTechnicalException.getMessage());
        Assertions.assertEquals("errorCodeOrLabel", pluginTechnicalException.getErrorCodeOrLabel());
    }

    @Test
    void constructeur01A() {
        pluginTechnicalException = new PluginTechnicalException((String) null, "errorCodeOrLabel");
        Assertions.assertNull(pluginTechnicalException.getMessage());
        Assertions.assertEquals("errorCodeOrLabel", pluginTechnicalException.getErrorCodeOrLabel());
    }

    @Test
    void constructeur01B() {
        pluginTechnicalException = new PluginTechnicalException("message", (String) null);
        Assertions.assertEquals("message", pluginTechnicalException.getMessage());
        Assertions.assertNull(pluginTechnicalException.getErrorCodeOrLabel());
    }

    @Test
    void constructeur02() {
        pluginTechnicalException = new PluginTechnicalException(new Exception("message"), "errorCodeOrLabel");
        Assertions.assertEquals("message", pluginTechnicalException.getMessage());
        Assertions.assertEquals("errorCodeOrLabel", pluginTechnicalException.getErrorCodeOrLabel());
    }

    @Test
    void constructeur02A() {
        pluginTechnicalException = new PluginTechnicalException((Exception) null, "errorCodeOrLabel");
        Assertions.assertEquals("", pluginTechnicalException.getMessage());
        Assertions.assertEquals("errorCodeOrLabel", pluginTechnicalException.getErrorCodeOrLabel());
    }

    @Test
    void constructeur02B() {
        pluginTechnicalException = new PluginTechnicalException(new Exception("message"), (String) null);
        Assertions.assertEquals("message", pluginTechnicalException.getMessage());
        Assertions.assertNull(pluginTechnicalException.getErrorCodeOrLabel());
    }

    @Test
    void toPaymentResponseFailure() {
        pluginTechnicalException = new PluginTechnicalException((Exception) null, "errorCodeOrLabelerrorCodeOrLabelerrorCodeOrLabelerrorCodeOrLabelerrorCodeOrLabel");

        PaymentResponseFailure paymentResponseFailure = pluginTechnicalException.toPaymentResponseFailure();
        Assertions.assertEquals(FailureCause.PARTNER_UNKNOWN_ERROR, paymentResponseFailure.getFailureCause());
        Assertions.assertTrue(paymentResponseFailure.getErrorCode().contains("errorCodeOrLabel"));
        Assertions.assertEquals(50, paymentResponseFailure.getErrorCode().length());
    }

    @Test
    void toRefundResponseFailure() {
        pluginTechnicalException = new PluginTechnicalException((Exception) null, "errorCodeOrLabelerrorCodeOrLabelerrorCodeOrLabelerrorCodeOrLabelerrorCodeOrLabel");

        RefundResponseFailure refundResponseFailure = pluginTechnicalException.toRefundResponseFailure();
        Assertions.assertEquals(FailureCause.PARTNER_UNKNOWN_ERROR, refundResponseFailure.getFailureCause());
        Assertions.assertTrue(refundResponseFailure.getErrorCode().contains("errorCodeOrLabel"));
        Assertions.assertEquals(50, refundResponseFailure.getErrorCode().length());
    }

    @Test
    void toPaymentFormConfigurationResponseFailure() {
        pluginTechnicalException = new PluginTechnicalException((Exception) null, "errorCodeOrLabelerrorCodeOrLabelerrorCodeOrLabelerrorCodeOrLabelerrorCodeOrLabel");

        PaymentFormConfigurationResponseFailure refundResponseFailure = pluginTechnicalException.toPaymentFormConfigurationResponseFailure();
        Assertions.assertEquals(FailureCause.PARTNER_UNKNOWN_ERROR, refundResponseFailure.getFailureCause());
        Assertions.assertTrue(refundResponseFailure.getErrorCode().contains("errorCodeOrLabel"));
        Assertions.assertEquals(50, refundResponseFailure.getErrorCode().length());
    }

    @Test
    void getTruncatedErrorCodeOrLabel() {
        pluginTechnicalException = new PluginTechnicalException((Exception) null, "errorCodeOrLabelerrorCodeOrLabelerrorCodeOrLabelerrorCodeOrLabelerrorCodeOrLabel");
        Assertions.assertEquals("", pluginTechnicalException.getMessage());
        Assertions.assertTrue(pluginTechnicalException.getErrorCodeOrLabel().contains("errorCodeOrLabel"));
        Assertions.assertEquals(80, pluginTechnicalException.getErrorCodeOrLabel().length());
        Assertions.assertEquals(50, pluginTechnicalException.getTruncatedErrorCodeOrLabel().length());
    }


    @Test
    void getFailureCause() {
        pluginTechnicalException = new PluginTechnicalException((Exception) null, (String) null);
        Assertions.assertEquals(FailureCause.PARTNER_UNKNOWN_ERROR, pluginTechnicalException.getFailureCause());
    }

    @Test
    void testToString() {

        pluginTechnicalException = new PluginTechnicalException((Exception) null, (String) null);
        String json = pluginTechnicalException.toString();
        Assertions.assertTrue(json.contains((FailureCause.PARTNER_UNKNOWN_ERROR.name())));
    }
}