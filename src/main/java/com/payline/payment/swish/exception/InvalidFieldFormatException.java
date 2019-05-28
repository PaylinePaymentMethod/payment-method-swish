package com.payline.payment.swish.exception;

import com.payline.pmapi.bean.common.FailureCause;

public class InvalidFieldFormatException extends PluginTechnicalException {

    /**
     * @param message the complete error message (as print in log files)
     * @param field   the misformated filed : 'Object.Field'
     */
    public InvalidFieldFormatException(String message, String field) {
        super(message, field);
    }

    @Override
    public FailureCause getFailureCause() {
        return FailureCause.INVALID_FIELD_FORMAT;

    }
}
