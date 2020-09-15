package com.payline.payment.swish.bean.common.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Builder
public class SwishRefundRequest extends SwishBean {

    @Getter
    private String payerPaymentReference;

    @Getter
    @NonNull
    private String originalPaymentReference;

    @Getter
    private String paymentReference;

    @Getter
    @NonNull
    private String callbackUrl;

    @Getter
    private String payerAlias;

    @Getter
    @NonNull
    private String payeeAlias;

    @Getter
    @NonNull
    private String amount;

    @Getter
    @NonNull
    private String currency;

    @Getter
    private String message;
}
