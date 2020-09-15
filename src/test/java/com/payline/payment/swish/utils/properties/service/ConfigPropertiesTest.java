package com.payline.payment.swish.utils.properties.service;

import com.payline.payment.swish.utils.properties.properties.ConfigProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class ConfigPropertiesTest {

    private ConfigProperties service = ConfigProperties.getInstance();

    private String key;


    @Test
    void getFromKeyKO() {
        key = service.get("BadKey");
        Assertions.assertNull(key);

    }

    @Test
    void getFromKeyOK() {
        key = service.get("http.connectTimeout");
        Assertions.assertNotNull(key);
    }
}