package com.payline.payment.swish.exception;

import com.google.gson.Gson;
import com.payline.payment.swish.utils.PluginUtils;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFailure;
import com.payline.pmapi.bean.paymentform.response.configuration.impl.PaymentFormConfigurationResponseFailure;
import com.payline.pmapi.bean.refund.response.impl.RefundResponseFailure;
import com.payline.pmapi.logger.LogManager;
import org.apache.logging.log4j.Logger;

public class PluginTechnicalException extends Exception {

    private static final Logger LOGGER = LogManager.getLogger(PluginTechnicalException.class);

    private static final Integer MAX_LENGHT = 50;

    private final String message;

    protected final String errorCodeOrLabel;

    public PluginTechnicalException(String message, String errorCodeOrLabel) {
        super();
        this.message = message;
        this.errorCodeOrLabel = errorCodeOrLabel;
        LOGGER.error(message);
    }


    /**
     * @param e                the original catched Exception
     * @param errorCodeOrLabel informations about the exception : 'Class.Method.Exception'
     */
    public PluginTechnicalException(Exception e, String errorCodeOrLabel) {
        super(e);
        this.message = e == null ? "" : e.getMessage();
        this.errorCodeOrLabel = errorCodeOrLabel;
        LOGGER.error(errorCodeOrLabel, e);

    }


    public PaymentResponseFailure toPaymentResponseFailure() {
        return toPaymentResponseFailure(null);
    }

    public PaymentResponseFailure toPaymentResponseFailure(String transactionId) {

        return PaymentResponseFailure.PaymentResponseFailureBuilder.aPaymentResponseFailure()
                .withFailureCause(getFailureCause())
                .withErrorCode(getTruncatedErrorCodeOrLabel())
                .withPartnerTransactionId(transactionId)
                .build();
    }

    public PaymentFormConfigurationResponseFailure toPaymentFormConfigurationResponseFailure() {
        return toPaymentFormConfigurationResponseFailure(null);
    }

    public PaymentFormConfigurationResponseFailure toPaymentFormConfigurationResponseFailure(String transactionId) {

        return PaymentFormConfigurationResponseFailure.PaymentFormConfigurationResponseFailureBuilder
                .aPaymentFormConfigurationResponseFailure()
                .withFailureCause(getFailureCause())
                .withErrorCode(getTruncatedErrorCodeOrLabel())
                .withPartnerTransactionId(transactionId)
                .build();
    }

    public RefundResponseFailure toRefundResponseFailure() {
        return toRefundResponseFailure(null);
    }

    public RefundResponseFailure toRefundResponseFailure(String transactionId) {

        return RefundResponseFailure.RefundResponseFailureBuilder.aRefundResponseFailure()
                .withFailureCause(getFailureCause())
                .withErrorCode(getTruncatedErrorCodeOrLabel())
                .withPartnerTransactionId(transactionId)
                .build();
    }

    public String getErrorCodeOrLabel() {
        return errorCodeOrLabel;
    }


    public String getTruncatedErrorCodeOrLabel() {
        return PluginUtils.truncate(this.getErrorCodeOrLabel(), MAX_LENGHT);
    }

    @Override
    public String getMessage() {
        return message;
    }

    public FailureCause getFailureCause() {
        return FailureCause.PARTNER_UNKNOWN_ERROR;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this.toPaymentResponseFailure());
    }
}
