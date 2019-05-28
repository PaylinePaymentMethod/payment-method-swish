package com.payline.payment.swish.bean.common.response;

public class SwishRefundResponse {
    private String id;
    private String payerPaymentReference;
    private String originalPaymentReference;
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
}
