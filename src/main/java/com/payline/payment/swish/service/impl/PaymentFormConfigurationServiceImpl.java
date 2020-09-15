package com.payline.payment.swish.service.impl;

import com.payline.payment.swish.service.LogoPaymentFormConfigurationService;
import com.payline.payment.swish.utils.i18n.I18nService;
import com.payline.payment.swish.utils.properties.constants.ConfigurationConstants;
import com.payline.pmapi.bean.paymentform.bean.field.*;
import com.payline.pmapi.bean.paymentform.bean.form.CustomForm;
import com.payline.pmapi.bean.paymentform.request.PaymentFormConfigurationRequest;
import com.payline.pmapi.bean.paymentform.response.configuration.PaymentFormConfigurationResponse;
import com.payline.pmapi.bean.paymentform.response.configuration.impl.PaymentFormConfigurationResponseSpecific;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Created by Thales on 27/08/2018.
 */
public class PaymentFormConfigurationServiceImpl extends LogoPaymentFormConfigurationService {
    public static final String PHONE_KEY = "phone";
    private static final String PHONE_LABEL = "phone.label";
    private static final String MESSAGE_LABEL = "message.label";
    private static final String PHONE_PLACEHOLDER = "0606060606";

    private static final Pattern PHONE_VALIDATION = Pattern.compile("\\d{10}|(?:\\d{3}-){2}\\d{4}|\\(\\d{3}\\)\\d{3}-?\\d{4}");

    // message keys
    private static final String EMPTY_PHONE = "error.phone.missing";
    private static final String INVALID_PHONE = "error.phone.invalid";

    public PaymentFormConfigurationServiceImpl() {
        i18n = I18nService.getInstance();
    }


    @Override
    public PaymentFormConfigurationResponse getPaymentFormConfiguration(PaymentFormConfigurationRequest request) {
        Locale locale = request.getLocale();
        List<PaymentFormField> fields = new ArrayList<>();

        // add the input text field to enter phone number
        PaymentFormInputFieldText phoneForm = PaymentFormInputFieldText
                .PaymentFormFieldTextBuilder
                .aPaymentFormFieldText()
                .withInputType(InputType.TEL)
                .withFieldIcon(FieldIcon.PHONE)
                .withKey(PHONE_KEY)
                .withLabel(i18n.getMessage(PHONE_LABEL, locale))
                .withPlaceholder(PHONE_PLACEHOLDER)
                .withRequired(true)
                .withRequiredErrorMessage(i18n.getMessage(EMPTY_PHONE, locale))
                .withSecured(false)
                .withValidation(PHONE_VALIDATION)
                .withValidationErrorMessage(i18n.getMessage(INVALID_PHONE, locale))
                .build();
        fields.add(phoneForm);

        // add a text field to inform the user to open his app
        PaymentFormDisplayFieldText message = PaymentFormDisplayFieldText
                .PaymentFormDisplayFieldTextBuilder
                .aPaymentFormDisplayFieldText()
                .withContent(i18n.getMessage(MESSAGE_LABEL, request.getLocale()))
                .build();
        fields.add(message);

        CustomForm customForm = CustomForm.builder()
                .withCustomFields(fields)
                .withButtonText(i18n.getMessage(ConfigurationConstants.PAYMENT_BUTTON_TEXT, locale))
                .withDescription(i18n.getMessage(ConfigurationConstants.PAYMENT_BUTTON_DESC, locale))
                .withDisplayButton(true)
                .build();

        return PaymentFormConfigurationResponseSpecific.PaymentFormConfigurationResponseSpecificBuilder
                .aPaymentFormConfigurationResponseSpecific()
                .withPaymentForm(customForm)
                .build();
    }

}