package com.payline.payment.swish.utils.properties.service;

import com.payline.payment.swish.utils.properties.constants.ConfigurationConstants;
import com.payline.payment.swish.utils.properties.service.ConfigPropertiesEnum;
import com.payline.payment.swish.utils.properties.service.PropertiesService;
import mockit.Tested;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Properties;

class ConfigPropertiesEnumTest {

    @Tested
    private PropertiesService service = ConfigPropertiesEnum.INSTANCE;

    private String key;


    @Test
    void getFilename() {
        Assertions.assertEquals(ConfigurationConstants.CONFIG_PROPERTIES, service.getFilename());
    }


    @Test
    void getProperties() {

        Properties properties = service.getProperties();
        Assertions.assertNotNull(properties);
        Assertions.assertFalse(properties.isEmpty());
    }


    @Test
    public void getFromKeyKO() {
        key = ConfigPropertiesEnum.INSTANCE.get("BadKey");
        Assertions.assertNull(key);

    }

    @Test
    public void getFromKeyOK() {
        key = ConfigPropertiesEnum.INSTANCE.get("http.connectTimeout");
        Assertions.assertNotNull(key);
    }
}