package com.payline.payment.swish.exception;

import com.payline.pmapi.bean.common.FailureCause;

public class HttpCallException extends PluginException {


    /**
     * @param message the complete error message (as print in log files)
     */
    public HttpCallException(String message) {
        super(message, FailureCause.COMMUNICATION_ERROR);
    }

    /**
     * @param e      the original catched Exception
     * @param origin method and type of exception : 'Class.Method.Exception'
     */
    public HttpCallException(String origin, Exception e) {
        super(origin, FailureCause.COMMUNICATION_ERROR, e);

    }
}
