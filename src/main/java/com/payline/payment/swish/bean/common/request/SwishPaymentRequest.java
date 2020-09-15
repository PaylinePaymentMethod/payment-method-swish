package com.payline.payment.swish.bean.common.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Builder
public class SwishPaymentRequest extends SwishBean {
    @Getter
    private String payeePaymentReference;
    @Getter
    @NonNull
    private String callbackUrl;
    @Getter
    private String payerAlias;
    @Getter
    @NonNull
    private String payeeAlias;
    @Getter
    private String amount;
    @Getter
    @NonNull
    private String currency;
    @Getter
    private String message;

}
