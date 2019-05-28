package com.payline.payment.swish.utils.properties.service;

import com.payline.payment.swish.utils.properties.service.PropertiesService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Properties;

class PropertiesServiceTest {


    @Test
    void readProperties() {


        Throwable exception = Assertions.assertThrows(RuntimeException.class, () -> {
            FakePropertiesService2.INSTANCE.readProperties(null);
        });
        Assertions.assertEquals("Unable to load the file test_file", exception.getMessage());

    }

    @Test
    void readPropertiesKo() {

        Throwable exception = Assertions.assertThrows(RuntimeException.class, () -> {
            FakePropertiesService.INSTANCE.readProperties(null);
        });
        Assertions.assertEquals("No file's name found", exception.getMessage());

    }


    private enum FakePropertiesService implements PropertiesService {


        INSTANCE;

        FakePropertiesService() {
        }

        @Override
        public String getFilename() {
            return null;
        }

        @Override
        public Properties getProperties() {
            return null;
        }
    }

    private enum FakePropertiesService2 implements PropertiesService {


        INSTANCE;

        FakePropertiesService2() {
        }

        @Override
        public String getFilename() {
            return "test_file";
        }

        @Override
        public Properties getProperties() {
            return null;
        }
    }
}