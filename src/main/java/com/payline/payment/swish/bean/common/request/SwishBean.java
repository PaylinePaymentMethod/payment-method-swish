package com.payline.payment.swish.bean.common.request;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class SwishBean {
    static final String SWEDISH_CURRENCY = "SEK";

    static final Logger LOGGER = LogManager.getLogger(SwishBean.class);

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
