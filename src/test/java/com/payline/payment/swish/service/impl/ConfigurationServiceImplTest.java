package com.payline.payment.swish.service.impl;


import com.payline.payment.swish.exception.InvalidDataException;
import com.payline.payment.swish.utils.TestUtils;
import com.payline.payment.swish.utils.http.SwishHttpClient;
import com.payline.payment.swish.utils.properties.constants.ConfigurationConstants;
import com.payline.pmapi.bean.configuration.ReleaseInformation;
import com.payline.pmapi.bean.configuration.parameter.AbstractParameter;
import com.payline.pmapi.bean.configuration.request.ContractParametersCheckRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


class ConfigurationServiceImplTest {

    @InjectMocks
    private ConfigurationServiceImpl service = new ConfigurationServiceImpl();

    @Mock
    private SwishHttpClient client;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testGetParameters() {

        List<AbstractParameter> parameters = service.getParameters(Locale.FRANCE);
        //Assert we have 1 parameters
        Assertions.assertNotNull(parameters);

        Assertions.assertEquals(1, parameters.size());
    }

    @Test
    void checkOK() {
        // Mock the http call
        doNothing().when(client).testConnection(any());

        ContractParametersCheckRequest request = TestUtils.createContractParametersCheckRequest();
        Map<String, String> errors = service.check(request);

        Assertions.assertEquals(0, errors.size());
        verify(client, atLeastOnce()).testConnection(any());
    }

    @Test
    void checkKOWrongParameters() {
        ContractParametersCheckRequest request = TestUtils.createContractParametersCheckRequest();
        request.getAccountInfo().remove(ConfigurationServiceImpl.KEY);

        Map<String, String> errors = service.check(request);

        Assertions.assertEquals(1, errors.size());
        Assertions.assertTrue(errors.containsKey(ConfigurationServiceImpl.KEY));
    }

    @Test
    void checkKOCall() {
        doThrow(new InvalidDataException("message")).when(client).testConnection(any());

        ContractParametersCheckRequest request = TestUtils.createContractParametersCheckRequest();

        Map<String, String> errors = service.check(request);

        Assertions.assertEquals(1, errors.size());
        Assertions.assertTrue(errors.containsKey(ContractParametersCheckRequest.GENERIC_ERROR));
    }

    @Test
    void getReleaseInformation() {
        LocalDate date = LocalDate.parse("11/09/1973", DateTimeFormatter.ofPattern(ConfigurationConstants.RELEASE_DATE_FORMAT));
        ReleaseInformation releaseInformation = service.getReleaseInformation();
        Assertions.assertNotNull(releaseInformation);
        Assertions.assertEquals(date, releaseInformation.getDate());
        Assertions.assertEquals("99.0", releaseInformation.getVersion());
    }

    @Test
    void getName() {
        String name = service.getName(Locale.FRANCE);
        Assertions.assertNotNull(name);
        Assertions.assertFalse(name.isEmpty());
    }

}
