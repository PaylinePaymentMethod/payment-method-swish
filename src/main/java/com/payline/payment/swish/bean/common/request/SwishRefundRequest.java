package com.payline.payment.swish.bean.common.request;

import com.payline.payment.swish.exception.InvalidDataException;
import com.payline.payment.swish.service.impl.ConfigurationServiceImpl;
import com.payline.payment.swish.utils.DataChecker;
import com.payline.pmapi.bean.refund.request.RefundRequest;

public class SwishRefundRequest extends SwishBean {
    private String payerPaymentReference;
    private String originalPaymentReference;
//    private String paymentReference;
    private String callbackUrl;
    private String payerAlias;
    private String payeeAlias;
    private String amount;
    private String currency;
    private String message;

    private SwishRefundRequest(Builder builder) {
        this.payerPaymentReference = builder.payerPaymentReference;
        this.originalPaymentReference = builder.originalPaymentReference;
//        this.paymentReference = builder.paymentReference;
        this.callbackUrl = builder.callbackUrl;
        this.payerAlias = builder.payerAlias;
        this.payeeAlias = builder.payeeAlias;
        this.amount = builder.amount;
        this.currency = builder.currency;
        this.message = builder.message;
    }

    public String getPayerPaymentReference() {
        return payerPaymentReference;
    }

    public String getOriginalPaymentReference() {
        return originalPaymentReference;
    }

//    public String getPaymentReference() {
//        return paymentReference;
//    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public String getPayerAlias() {
        return payerAlias;
    }

    public String getPayeeAlias() {
        return payeeAlias;
    }

    public String getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }


    public String getMessage() {
        return message;
    }


    public static final class Builder {
        private String payerPaymentReference;
        private String originalPaymentReference;
//        private String paymentReference;
        private String callbackUrl;
        private String payerAlias;
        private String payeeAlias;
        private String amount;
        private String currency;
        private String message;

        /**
         * create a refund request from a Payline refundRequest
         *
         * @param paylineRequest the request to convert into a SwishRefundRequest
         * @return a SwishRefundRequest after validating it's fields
         * @throws InvalidDataException
         */
        public SwishRefundRequest fromPaylineRequest(RefundRequest paylineRequest) {
            this.payerPaymentReference = paylineRequest.getSoftDescriptor();
            this.originalPaymentReference = paylineRequest.getPartnerTransactionId();
//            this.paymentReference = paylineRequest.getTransactionId();
            this.callbackUrl = paylineRequest.getEnvironment().getNotificationURL();
            this.payeeAlias = paylineRequest.getTransactionAdditionalData();
            this.payerAlias = paylineRequest.getContractConfiguration().getProperty(ConfigurationServiceImpl.KEY).getValue();
            this.amount = paylineRequest.getAmount().getAmountInSmallestUnit().toString();
            this.currency = paylineRequest.getAmount().getCurrency().getCurrencyCode();
            this.message = paylineRequest.getOrder().getReference();

            // verify  fields
            this.verify();

            // create and return a SwishPaymentRequest
            return new SwishRefundRequest(this);
        }


        /**
         * Check if mandatory fields are missing and log it
         */
        public void verify() {
            if (DataChecker.isEmpty(this.originalPaymentReference)) {
                LOGGER.warn("originalPaymentReference is missing");
            }
            if (DataChecker.isEmpty(this.callbackUrl)) {
                LOGGER.warn("callbackUrl is missing");
            }
            if (DataChecker.isEmpty(this.payerAlias)) {
                LOGGER.warn("payeeAlias is missing");
            }
            if (DataChecker.isEmpty(this.amount)) {
                LOGGER.warn("amount is missing");
            }
            if (DataChecker.isEmpty(this.currency)) {
                LOGGER.warn("currency is missing");
            }

            if (!this.currency.equals(SWEDISH_CURRENCY)) {
                LOGGER.warn("Currency must be: " + SWEDISH_CURRENCY, "currency");
            }
        }
    }
}
