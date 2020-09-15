package com.payline.payment.swish.bean.common.response;

public class SwishPaymentResponse {
    private String id;
    private String payeePaymentReference;
    private String paymentReference;
    private String callbackUrl;
    private String payerAlias;
    private String payeeAlias;
    private String amount;
    private String currency;
    private String message;
    private String status;
    private String dateCreated;
    private String datePaid;

    public String getId() {
        return id;
    }

    public String getPayeePaymentReference() {
        return payeePaymentReference;
    }

    public String getPaymentReference() {
        return paymentReference;
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

    public String getStatus() {
        return status;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public String getDatePaid() {
        return datePaid;
    }
}
