package com.payline.payment.swish.exception;

import com.payline.pmapi.bean.common.FailureCause;

public class InvalidDataException extends PluginTechnicalException {


    /**
     * @param message the complete error message (as print in log files)
     * @param field   the required filed : 'Object.Field'
     */
    public InvalidDataException(String message, String field) {
        super(message, field);
    }

    @Override
    public FailureCause getFailureCause() {
        return FailureCause.INVALID_DATA;
    }

}
