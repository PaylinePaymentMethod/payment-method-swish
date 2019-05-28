package com.payline.payment.swish.bean.common.request;

import com.payline.payment.swish.service.impl.ConfigurationServiceImpl;
import com.payline.payment.swish.service.impl.PaymentFormConfigurationServiceImpl;
import com.payline.payment.swish.utils.DataChecker;
import com.payline.pmapi.bean.configuration.request.ContractParametersCheckRequest;
import com.payline.pmapi.bean.payment.request.PaymentRequest;

public class SwishPaymentRequest extends SwishBean {
    private String payeePaymentReference;
    private String callbackUrl;
    private String payerAlias;
    private String payeeAlias;
    private String amount;
    private String currency;
    private String message;

    private SwishPaymentRequest(Builder builder) {
        this.payeePaymentReference = builder.payeePaymentReference;
        this.callbackUrl = builder.callbackUrl;
        this.payerAlias = builder.payerAlias;
        this.payeeAlias = builder.payeeAlias;
        this.amount = builder.amount;
        this.currency = builder.currency;
        this.message = builder.message;
    }

    public String getPayeePaymentReference() {
        return payeePaymentReference;
    }

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
        private static final String DEFAULT_PAYEE_PAYMENT_REF = "123456";
        private static final String DEFAULT_CALLBACK_URL = "https://this.is.an.url";
        private static final String DEFAULT_PAYER_ALIAS = "0612345678";
        private static final String DEFAULT_AMOUNT = "1";
        private static final String DEFAULT_MESSAGE = "test connection";

        private String payeePaymentReference;
        private String callbackUrl;
        private String payerAlias;
        private String payeeAlias;
        private String amount;
        private String currency;
        private String message;

        /**
         * Create a transaction request from a Payline PaymentRequest.
         *
         * @param paylineRequest the request to convert into a SwishPaymentRequest
         * @return a SwishPaymentRequest after validating it's fields
         */
        public SwishPaymentRequest fromPaylineRequest(PaymentRequest paylineRequest) {
            this.payeePaymentReference = paylineRequest.getOrder().getReference();
            this.callbackUrl = paylineRequest.getEnvironment().getNotificationURL();
            this.payerAlias = paylineRequest.getPaymentFormContext().getPaymentFormParameter().get(PaymentFormConfigurationServiceImpl.PHONE_KEY);
            this.payeeAlias = paylineRequest.getContractConfiguration().getProperty(ConfigurationServiceImpl.KEY).getValue();
            this.amount = paylineRequest.getAmount().getAmountInSmallestUnit().toString();
            this.currency = paylineRequest.getAmount().getCurrency().getCurrencyCode();
            this.message = paylineRequest.getSoftDescriptor();

            // verify  fields
            this.verify();

            // create and return a SwishPaymentRequest
            return new SwishPaymentRequest(this);
        }


        /**
         * Create a fake trasaction request from a ContractParametersCheckRequest
         * it is used to test the connection with Swish backend
         *
         * @param paylineRequest the request containing the merchant account
         * @return a SwishPaymentRequest after completing other field with dummy values
         */
        public SwishPaymentRequest fromPaylineRequest(ContractParametersCheckRequest paylineRequest) {
            this.payeePaymentReference = DEFAULT_PAYEE_PAYMENT_REF;
            this.callbackUrl = DEFAULT_CALLBACK_URL;
            this.payerAlias = DEFAULT_PAYER_ALIAS;
            this.payeeAlias = paylineRequest.getAccountInfo().get(ConfigurationServiceImpl.KEY);
            this.amount = DEFAULT_AMOUNT;
            this.currency = SWEDISH_CURRENCY;
            this.message = DEFAULT_MESSAGE;

            // create and return a SwishPaymentRequest
            return new SwishPaymentRequest(this);
        }


        /**
         * Check if mandatory fields are missing and log it
         */
        private void verify() {
            if (DataChecker.isEmpty(this.callbackUrl)) {
                LOGGER.warn("callbackUrl is missing");
            }
            if (DataChecker.isEmpty(this.payeeAlias)) {
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
