package com.payline.payment.swish.bean.common.response;

public class SwishErrorResponse {
    private String errorCode;
    private String errorMessage;
    private String additionalInformation;

    public SwishErrorResponse() {
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getAdditionalInformation() {
        return additionalInformation;
    }
}
