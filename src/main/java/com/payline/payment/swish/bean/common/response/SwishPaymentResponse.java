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

    public String getStatus() {
        return status;
    }

    public String getPayerAlias() {
        return payerAlias;
    }
}
