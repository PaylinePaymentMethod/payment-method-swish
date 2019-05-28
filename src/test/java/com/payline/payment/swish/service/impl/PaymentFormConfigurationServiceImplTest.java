package com.payline.payment.swish.service.impl;

import com.payline.payment.swish.service.impl.PaymentFormConfigurationServiceImpl;
import com.payline.payment.swish.utils.TestUtils;
import com.payline.payment.swish.utils.properties.service.PropertiesService;
import com.payline.pmapi.bean.paymentform.bean.PaymentFormLogo;
import com.payline.pmapi.bean.paymentform.request.PaymentFormConfigurationRequest;
import com.payline.pmapi.bean.paymentform.request.PaymentFormLogoRequest;
import com.payline.pmapi.bean.paymentform.response.configuration.impl.PaymentFormConfigurationResponseSpecific;
import com.payline.pmapi.bean.paymentform.response.logo.PaymentFormLogoResponse;
import com.payline.pmapi.bean.paymentform.response.logo.impl.PaymentFormLogoResponseFile;
import mockit.Capturing;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Tested;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Locale;

import static com.payline.payment.swish.utils.properties.constants.LogoConstants.LOGO_FILE_NAME;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PaymentFormConfigurationServiceImplTest {


    @Tested
    private PaymentFormConfigurationServiceImpl service;

    private final String buttonText = "Payer avec Swish";
    private final String decription = "Payer avec Swish";
    private final int height = 24;
    private final int width = 24;
    private final String paymentMethodIdentifier = "paymentMethodIdentifier";

    @BeforeAll
    public void setup() {

        service = new PaymentFormConfigurationServiceImpl();


    }


    @Test
    public void testGetPaymentFormConfiguration() {
        //Create a form config request
        PaymentFormConfigurationRequest paymentFormConfigurationRequest = TestUtils.createDefaultPaymentFormConfigurationRequest();

        PaymentFormConfigurationResponseSpecific paymentFormConfigurationResponse = (PaymentFormConfigurationResponseSpecific) service.getPaymentFormConfiguration(paymentFormConfigurationRequest);

        Assertions.assertNotNull(paymentFormConfigurationResponse.getPaymentForm());
        Assertions.assertEquals(buttonText, paymentFormConfigurationResponse.getPaymentForm().getButtonText());
        Assertions.assertEquals(decription, paymentFormConfigurationResponse.getPaymentForm().getDescription());
        Assertions.assertTrue(paymentFormConfigurationResponse.getPaymentForm().isDisplayButton());
    }

    @Test
    public void testGetPaymentFormLogo(@Mocked PaymentFormLogoRequest paymentFormLogoRequest) {
        //Mock PaymentFormLogoRequest

        new Expectations() {{
            paymentFormLogoRequest.getLocale();
            result = Locale.FRANCE;

        }};

        PaymentFormLogoResponse paymentFormLogoResponse = service.getPaymentFormLogo(paymentFormLogoRequest);

        Assertions.assertNotNull(paymentFormLogoResponse);
        Assertions.assertTrue(paymentFormLogoResponse instanceof PaymentFormLogoResponseFile);

        PaymentFormLogoResponseFile casted = (PaymentFormLogoResponseFile) paymentFormLogoResponse;
        Assertions.assertEquals(height, casted.getHeight());
        Assertions.assertEquals(width, casted.getWidth());
    }

    @Test
    public void testGetLogo() {
        // when: getLogo is called

        PaymentFormLogo paymentFormLogo = service.getLogo(paymentMethodIdentifier, Locale.FRANCE);


        // then: returned elements are not null
        Assertions.assertNotNull(paymentFormLogo.getFile());
        Assertions.assertNotNull(paymentFormLogo.getContentType());

    }

    @Test
    public void testGetLogo_Err01(@Capturing PropertiesService propertiesService) {
        // when: getLogo is called

        new Expectations() {{
            propertiesService.get((String) any);
            result = null;

        }};

        Throwable exception = Assertions.assertThrows(RuntimeException.class, () -> {
            service.getLogo(paymentMethodIdentifier, Locale.FRANCE);

        });
        Assertions.assertEquals("Unable to load the logo " + LOGO_FILE_NAME, exception.getMessage());


    }


}

