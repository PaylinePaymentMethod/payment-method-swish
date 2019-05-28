package com.payline.payment.swish.utils.properties.service;

import com.payline.payment.swish.utils.properties.constants.LogoConstants;
import com.payline.payment.swish.utils.properties.service.LogoPropertiesEnum;
import com.payline.payment.swish.utils.properties.service.PropertiesService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Properties;

class LogoPropertiesEnumTest {

    private PropertiesService service = LogoPropertiesEnum.INSTANCE;

    @Test
    void getFilename() {
        Assertions.assertEquals(LogoConstants.LOGO_PROPERTIES, service.getFilename());
    }


    @Test
    void getProperties() {

        Properties properties = service.getProperties();
        Assertions.assertNotNull(properties);
        Assertions.assertFalse(properties.isEmpty());
        Assertions.assertEquals(7, properties.size());
    }
}