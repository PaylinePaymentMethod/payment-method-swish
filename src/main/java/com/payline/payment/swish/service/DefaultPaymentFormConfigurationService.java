package com.payline.payment.swish.service;


import com.payline.payment.swish.utils.i18n.I18nService;
import com.payline.payment.swish.utils.properties.service.LogoPropertiesEnum;
import com.payline.pmapi.bean.paymentform.bean.PaymentFormLogo;
import com.payline.pmapi.bean.paymentform.request.PaymentFormLogoRequest;
import com.payline.pmapi.bean.paymentform.response.logo.PaymentFormLogoResponse;
import com.payline.pmapi.bean.paymentform.response.logo.impl.PaymentFormLogoResponseFile;
import com.payline.pmapi.logger.LogManager;
import com.payline.pmapi.service.PaymentFormConfigurationService;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Locale;

import static com.payline.payment.swish.utils.properties.constants.LogoConstants.*;


public interface DefaultPaymentFormConfigurationService extends PaymentFormConfigurationService {

    Logger LOGGER = LogManager.getLogger(DefaultPaymentFormConfigurationService.class);

    I18nService i18n = I18nService.getInstance();

    ClassLoader CLASS_LOADER = DefaultPaymentFormConfigurationService.class.getClassLoader();

    @Override
    default PaymentFormLogoResponse getPaymentFormLogo(PaymentFormLogoRequest paymentFormLogoRequest) {

        Locale locale = paymentFormLogoRequest.getLocale();

        return PaymentFormLogoResponseFile.PaymentFormLogoResponseFileBuilder.aPaymentFormLogoResponseFile()
                .withHeight(Integer.valueOf(LogoPropertiesEnum.INSTANCE.get(LOGO_HEIGHT)))
                .withWidth(Integer.valueOf(LogoPropertiesEnum.INSTANCE.get(LOGO_WIDTH)))
                .withTitle(i18n.getMessage(LogoPropertiesEnum.INSTANCE.get(LOGO_TITLE), locale))
                .withAlt(i18n.getMessage(LogoPropertiesEnum.INSTANCE.get(LOGO_ALT), locale))
                .build();
    }

    @Override
    default PaymentFormLogo getLogo(String s, Locale locale) {
        try {
            String fileName = LogoPropertiesEnum.INSTANCE.get(LOGO_FILE_NAME);
            InputStream input = CLASS_LOADER.getResourceAsStream(fileName);


            // Read logo file
            BufferedImage logo = ImageIO.read(input);

            // Recover byte array from image
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(logo, LogoPropertiesEnum.INSTANCE.get(LOGO_FORMAT), baos);

            return PaymentFormLogo.PaymentFormLogoBuilder.aPaymentFormLogo()
                    .withFile(baos.toByteArray())
                    .withContentType(LogoPropertiesEnum.INSTANCE.get(LOGO_CONTENT_TYPE))
                    .build();
        } catch (Exception e) {
            LOGGER.error("Unable to load the logo", e);
            throw new RuntimeException("Unable to load the logo " + LOGO_FILE_NAME);
        }
    }

}
