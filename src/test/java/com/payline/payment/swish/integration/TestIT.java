package com.payline.payment.swish.integration;

import com.payline.payment.swish.service.impl.ConfigurationServiceImpl;
import com.payline.payment.swish.service.impl.NotificationServiceImpl;
import com.payline.payment.swish.service.impl.PaymentServiceImpl;
import com.payline.payment.swish.service.impl.RefundServiceImpl;
import com.payline.payment.swish.utils.TestUtils;
import com.payline.payment.swish.utils.http.AbstractHttpClient;
import com.payline.pmapi.bean.configuration.request.ContractParametersCheckRequest;
import com.payline.pmapi.bean.notification.request.NotificationRequest;
import com.payline.pmapi.bean.notification.response.NotificationResponse;
import com.payline.pmapi.bean.notification.response.impl.PaymentResponseByNotificationResponse;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseActiveWaiting;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseSuccess;
import com.payline.pmapi.bean.refund.request.RefundRequest;
import com.payline.pmapi.bean.refund.response.RefundResponse;
import com.payline.pmapi.bean.refund.response.impl.RefundResponseSuccess;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;


public class TestIT {
    static final Logger LOGGER = LogManager.getLogger(TestIT.class);


    private ConfigurationServiceImpl configurationService = new ConfigurationServiceImpl();
    private PaymentServiceImpl paymentService = new PaymentServiceImpl();
    private NotificationServiceImpl notificationService = new NotificationServiceImpl();
    private RefundServiceImpl refundService = new RefundServiceImpl();

    String bodyNotification = "{     " +
            "\"id\": \"AB23D7406ECE4542A80152D909EF9F6B\"," +
            "     \"payeePaymentReference\": \"0123456789\"," +
            "     \"paymentReference\": \"6D6CD7406ECE4542A80152D909EF9F6B\"," +
            "     \"callbackUrl\": \"https://example.com/api/swishcb/paymentrequests\"," +
            "     \"payerAlias\": \"" + TestUtils.PAYER_PHONE + "\"," +
            "     \"payeeAlias\": \"" + TestUtils.MERCHANT_ID + "\"," +
            "     \"amount\": \"100\"," +
            "     \"currency\": \"SEK\"," +
            "     \"message\": \"Kingston USB Flash Drive 8 GB\"," +
            "     \"status\": \"PAID\"," +
            "     \"dateCreated\": \"2015-02-19T22:01:53+01:00\"," +
            "     \"datePaid\": \"2015-02-19T22:03:53+01:00\" " +
            "}";

    @Test
    public void fullPaymentTest() throws IOException {
        // create sensitive properties map, containing the client certificate and private key
        Map<String, String> sensitiveProperties = new HashMap<>();
        sensitiveProperties.put( AbstractHttpClient.PARTNER_CONFIGURATION_CERT, new String(Files.readAllBytes(Paths.get(System.getProperty("project.certificateChainPath")))) );
        sensitiveProperties.put( AbstractHttpClient.PARTNER_CONFIGURATION_PK, new String(Files.readAllBytes(Paths.get(System.getProperty("project.pkPath")))) );

        // connection to Swish backend test
        LOGGER.info("Testing Check request");
        ContractParametersCheckRequest checkRequest = TestUtils.createContractParametersCheckRequest();
        // Since the HTTP client is a singleton, initialized at first use, we only need to pass sensitive properties once
        checkRequest.getPartnerConfiguration().getSensitiveProperties().putAll( sensitiveProperties );
        Map errors = configurationService.check(checkRequest);
        Assertions.assertEquals(0, errors.size());

        // payment test
        LOGGER.info("Testing payment request");
        PaymentRequest paymentRequest = TestUtils.createDefaultPaymentRequest();
        PaymentResponse paymentResponse = paymentService.paymentRequest(paymentRequest);
        Assertions.assertEquals(PaymentResponseActiveWaiting.class, paymentResponse.getClass());

        // notification reception test
        LOGGER.info("Testing notification reception");
        NotificationRequest notificationRequest = TestUtils.createNotificationRequest(bodyNotification);
        NotificationResponse notificationResponse = notificationService.parse(notificationRequest);
        Assertions.assertEquals(PaymentResponseByNotificationResponse.class, notificationResponse.getClass());
        PaymentResponseByNotificationResponse paymentResponseByNotificationResponse = (PaymentResponseByNotificationResponse) notificationResponse;
        Assertions.assertEquals(PaymentResponseSuccess.class, paymentResponseByNotificationResponse.getPaymentResponse().getClass());
        PaymentResponseSuccess paymentResponseSuccess = (PaymentResponseSuccess) paymentResponseByNotificationResponse.getPaymentResponse();

        // refund test
        LOGGER.info("Testing refund request");
        RefundRequest refundRequest = TestUtils.createRefundFromPaymentRequest(paymentRequest, paymentResponseSuccess);
        RefundResponse refundResponse = refundService.refundRequest(refundRequest);
        Assertions.assertEquals(RefundResponseSuccess.class, refundResponse.getClass());
    }
}