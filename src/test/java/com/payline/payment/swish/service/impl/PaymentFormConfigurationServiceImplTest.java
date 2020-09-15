package com.payline.payment.swish.service.impl;

import com.payline.payment.swish.utils.TestUtils;
import com.payline.payment.swish.utils.properties.properties.ConfigProperties;
import com.payline.pmapi.bean.paymentform.bean.PaymentFormLogo;
import com.payline.pmapi.bean.paymentform.request.PaymentFormConfigurationRequest;
import com.payline.pmapi.bean.paymentform.request.PaymentFormLogoRequest;
import com.payline.pmapi.bean.paymentform.response.configuration.impl.PaymentFormConfigurationResponseSpecific;
import com.payline.pmapi.bean.paymentform.response.logo.PaymentFormLogoResponse;
import com.payline.pmapi.bean.paymentform.response.logo.impl.PaymentFormLogoResponseFile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Locale;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;


class PaymentFormConfigurationServiceImplTest {


    @InjectMocks
    private PaymentFormConfigurationServiceImpl service;

    @Mock
    protected ConfigProperties config = ConfigProperties.getInstance();

    private final String buttonText = "Payer avec Swish";
    private final String decription = "Payer avec Swish";
    private final int height = 24;
    private final int width = 24;
    private final String paymentMethodIdentifier = "paymentMethodIdentifier";

    @BeforeEach
    void setup() {
        service = new PaymentFormConfigurationServiceImpl();
        MockitoAnnotations.initMocks(this);
    }


    @Test
    void testGetPaymentFormConfiguration() {
        //Create a form config request
        PaymentFormConfigurationRequest paymentFormConfigurationRequest = TestUtils.createDefaultPaymentFormConfigurationRequest();

        PaymentFormConfigurationResponseSpecific paymentFormConfigurationResponse = (PaymentFormConfigurationResponseSpecific) service.getPaymentFormConfiguration(paymentFormConfigurationRequest);

        Assertions.assertNotNull(paymentFormConfigurationResponse.getPaymentForm());
        Assertions.assertEquals(buttonText, paymentFormConfigurationResponse.getPaymentForm().getButtonText());
        Assertions.assertEquals(decription, paymentFormConfigurationResponse.getPaymentForm().getDescription());
        Assertions.assertTrue(paymentFormConfigurationResponse.getPaymentForm().isDisplayButton());
    }

    @Test
    void testGetPaymentFormLogo() {
        //Mock PaymentFormLogoRequest
        doReturn("24").when(config).get("logo.height");
        doReturn("24").when(config).get("logo.width");
        PaymentFormLogoRequest paymentFormLogoRequest = TestUtils.createPaymentFormLogoRequest();
        PaymentFormLogoResponse paymentFormLogoResponse = service.getPaymentFormLogo(paymentFormLogoRequest);

        Assertions.assertNotNull(paymentFormLogoResponse);
        Assertions.assertTrue(paymentFormLogoResponse instanceof PaymentFormLogoResponseFile);

        PaymentFormLogoResponseFile casted = (PaymentFormLogoResponseFile) paymentFormLogoResponse;
        Assertions.assertEquals(height, casted.getHeight());
        Assertions.assertEquals(width, casted.getWidth());
    }

    @Test
    void testGetLogo() {
        // when: getLogo is called
        doReturn("forTests.png").when(config).get("logo.filename");
        doReturn("png").when(config).get("logo.format");
        doReturn("image/png").when(config).get("logo.contentType");

        PaymentFormLogo paymentFormLogo = service.getLogo(paymentMethodIdentifier, Locale.FRANCE);

        // then: returned elements are not null
        Assertions.assertNotNull(paymentFormLogo.getFile());
        Assertions.assertNotNull(paymentFormLogo.getContentType());
        Assertions.assertEquals("image/png", paymentFormLogo.getContentType());
    }

    @Test
    void testGetLogo_Err01() {
        // when: getLogo is called
        doReturn(null).when(config).get(anyString());

        Assertions.assertThrows(RuntimeException.class, () -> {
            service.getLogo(paymentMethodIdentifier, Locale.FRANCE);

        });
    }


}

