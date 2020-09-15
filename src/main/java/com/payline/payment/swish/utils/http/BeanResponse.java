package com.payline.payment.swish.utils.http;

import com.payline.payment.swish.bean.common.request.SwishBean;

public abstract class BeanResponse extends SwishBean {

    private int code;
    private String message;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}