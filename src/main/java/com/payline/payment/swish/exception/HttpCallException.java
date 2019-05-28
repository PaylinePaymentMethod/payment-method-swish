package com.payline.payment.swish.exception;

import com.payline.pmapi.bean.common.FailureCause;

public class HttpCallException extends PluginTechnicalException {


    /**
     * @param message the complete error message (as print in log files)
     * @param origin  method and type of exception : 'Class.Method.Exception'
     */
    public HttpCallException(String message, String origin) {
        super(message, origin);
    }

    /**
     * @param e      the original catched Exception
     * @param origin method and type of exception : 'Class.Method.Exception'
     */
    public HttpCallException(Exception e, String origin) {
        super(e, origin);

    }

    @Override
    public FailureCause getFailureCause() {
        return FailureCause.COMMUNICATION_ERROR;
    }

}
