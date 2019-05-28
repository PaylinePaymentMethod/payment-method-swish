package com.payline.payment.swish.utils.properties.service;

import com.payline.payment.swish.utils.properties.constants.ConfigurationConstants;

import java.util.Properties;

/**
 * Utility class which reads and provides config properties.
 */
public enum ConfigPropertiesEnum implements PropertiesService {

    INSTANCE;

    private static final String FILENAME = ConfigurationConstants.CONFIG_PROPERTIES;

    private final Properties properties;

    /* This class has only static methods: no need to instantiate it */
    ConfigPropertiesEnum() {
        properties = new Properties();
        // init of the Properties
        readProperties(properties);
    }


    @Override
    public String getFilename() {
        return FILENAME;
    }

    @Override
    public Properties getProperties() {
        return properties;
    }
}
