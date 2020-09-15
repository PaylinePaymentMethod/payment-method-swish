package com.payline.payment.swish.service.impl;

import com.payline.payment.swish.exception.PluginException;
import com.payline.payment.swish.utils.DataChecker;
import com.payline.payment.swish.utils.http.SwishHttpClient;
import com.payline.payment.swish.utils.i18n.I18nService;
import com.payline.payment.swish.utils.properties.constants.ConfigurationConstants;
import com.payline.payment.swish.utils.properties.properties.ReleaseProperties;
import com.payline.pmapi.bean.configuration.ReleaseInformation;
import com.payline.pmapi.bean.configuration.parameter.AbstractParameter;
import com.payline.pmapi.bean.configuration.parameter.impl.InputParameter;
import com.payline.pmapi.bean.configuration.request.ContractParametersCheckRequest;
import com.payline.pmapi.service.ConfigurationService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ConfigurationServiceImpl implements ConfigurationService {
    public static final String KEY = "merchantId";
    private static final String LABEL = "merchantId.label";
    private static final String DESCRIPTION = "merchantId.description";

    // message keys
    private static final String EMPTY_MERCHANTID = "error.merchantId.missing";
    private ReleaseProperties releaseProperties = ReleaseProperties.getInstance();


    private I18nService i18n = I18nService.getInstance();
    private SwishHttpClient httpClient = SwishHttpClient.getInstance();

    @Override
    public List<AbstractParameter> getParameters(Locale locale) {
        List<AbstractParameter> parameters = new ArrayList<>();

        // merchantId inputParameter
        AbstractParameter merchantId = new InputParameter();
        merchantId.setKey(KEY);
        merchantId.setLabel(i18n.getMessage(LABEL, locale));
        merchantId.setDescription(i18n.getMessage(DESCRIPTION, locale));
        merchantId.setRequired(true);
        parameters.add(merchantId);

        return parameters;
    }

    @Override
    public Map<String, String> check(ContractParametersCheckRequest contractParametersCheckRequest) {
        Locale locale = contractParametersCheckRequest.getLocale();
        final Map<String, String> errors = new HashMap<>();

        // check validity of fields filed by the merchant
        Map<String, String> accountInfo = contractParametersCheckRequest.getAccountInfo();
        if (DataChecker.isEmpty(accountInfo.get(KEY))) {
            errors.put(KEY, i18n.getMessage(EMPTY_MERCHANTID, locale));
        }

        // test the connection by creating a fake transaction
        if (errors.size() == 0) {
            try {
                httpClient.init();
                httpClient.testConnection(contractParametersCheckRequest);
            } catch (PluginException e) {
                errors.put(ContractParametersCheckRequest.GENERIC_ERROR, e.getMessage());
            }
        }
        return errors;
    }

    @Override
    public ReleaseInformation getReleaseInformation() {
        LocalDate date = LocalDate.parse(releaseProperties.get(ConfigurationConstants.RELEASE_DATE),
                DateTimeFormatter.ofPattern(ConfigurationConstants.RELEASE_DATE_FORMAT));
        return ReleaseInformation.ReleaseBuilder.aRelease()
                .withDate(date)
                .withVersion(releaseProperties.get(ConfigurationConstants.RELEASE_VERSION))
                .build();
    }

    @Override
    public String getName(Locale locale) {
        return this.i18n.getMessage(ConfigurationConstants.PAYMENT_METHOD_NAME, locale);
    }
}
