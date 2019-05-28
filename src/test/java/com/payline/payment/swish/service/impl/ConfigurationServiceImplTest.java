package com.payline.payment.swish.service.impl;


import com.payline.payment.swish.exception.InvalidDataException;
import com.payline.payment.swish.utils.TestUtils;
import com.payline.payment.swish.utils.http.SwishHttpClient;
import com.payline.payment.swish.utils.properties.constants.ConfigurationConstants;
import com.payline.pmapi.bean.configuration.ReleaseInformation;
import com.payline.pmapi.bean.configuration.parameter.AbstractParameter;
import com.payline.pmapi.bean.configuration.request.ContractParametersCheckRequest;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Tested;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class ConfigurationServiceImplTest {

    @Tested
    private ConfigurationServiceImpl service;

    @Mocked
    private SwishHttpClient client;

    @Test
    public void testGetParameters() {

        List<AbstractParameter> parameters = service.getParameters(Locale.FRANCE);
        //Assert we have 1 parameters
        Assertions.assertNotNull(parameters);

        Assertions.assertEquals(1, parameters.size());
    }

    @Test
    public void checkOK() throws Exception {
        // Mock the http call
        new Expectations() {{
            client.testConnection((ContractParametersCheckRequest) any);
        }};

        ContractParametersCheckRequest request = TestUtils.createContractParametersCheckRequest();
        Map<String, String> errors = service.check(request);

        Assertions.assertEquals(0, errors.size());
    }

    @Test
    public void checkKOWrongParameters() {
        ContractParametersCheckRequest request = TestUtils.createContractParametersCheckRequest();
        request.getAccountInfo().remove(ConfigurationServiceImpl.KEY);

        Map<String, String> errors = service.check(request);

        Assertions.assertEquals(1, errors.size());
        Assertions.assertTrue(errors.containsKey(ConfigurationServiceImpl.KEY));
    }

    @Test
    public void checkKOCall() throws Exception {
        new Expectations() {{
            client.testConnection((ContractParametersCheckRequest) any);
            result = new InvalidDataException("message", "field");
        }};

        ContractParametersCheckRequest request = TestUtils.createContractParametersCheckRequest();

        Map<String, String> errors = service.check(request);

        Assertions.assertEquals(1, errors.size());
        Assertions.assertTrue(errors.containsKey(ContractParametersCheckRequest.GENERIC_ERROR));
    }

    @Test
    public void getReleaseInformation() {
        LocalDate date = LocalDate.parse("11/09/1973", DateTimeFormatter.ofPattern(ConfigurationConstants.RELEASE_DATE_FORMAT));
        ReleaseInformation releaseInformation = service.getReleaseInformation();
        Assertions.assertNotNull(releaseInformation);
        Assertions.assertEquals(date, releaseInformation.getDate());
        Assertions.assertEquals("99.0", releaseInformation.getVersion());
    }

    @Test
    public void getName() {
        String name = service.getName(Locale.FRANCE);
        Assertions.assertNotNull(name);
        Assertions.assertFalse(name.isEmpty());
    }

}
