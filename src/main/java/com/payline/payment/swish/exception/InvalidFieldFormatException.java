package com.payline.payment.swish.exception;

import com.payline.pmapi.bean.common.FailureCause;

public class InvalidFieldFormatException extends PluginException {

    public InvalidFieldFormatException( String message ){
        super( message, FailureCause.INVALID_FIELD_FORMAT );
    }

    public InvalidFieldFormatException( String message, Exception cause ){
        super( message, FailureCause.INVALID_FIELD_FORMAT, cause );
    }

}
