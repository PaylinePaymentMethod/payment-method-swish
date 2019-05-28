package com.payline.payment.swish.service.impl;

import com.payline.payment.swish.service.impl.NotificationServiceImpl;
import com.payline.payment.swish.utils.TestUtils;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.notification.request.NotificationRequest;
import com.payline.pmapi.bean.notification.response.NotificationResponse;
import com.payline.pmapi.bean.notification.response.impl.PaymentResponseByNotificationResponse;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFailure;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseSuccess;
import mockit.Tested;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NotificationServiceImplTest {
    String bodyTemplate = "{     " +
            "\"id\": \"AB23D7406ECE4542A80152D909EF9F6B\"," +
            "     \"payeePaymentReference\": \"0123456789\"," +
            "     \"paymentReference\": \"6D6CD7406ECE4542A80152D909EF9F6B\"," +
            "     \"callbackUrl\": \"https://example.com/api/swishcb/paymentrequests\"," +
            "     \"payerAlias\": \"46701234567\"," +
            "     \"payeeAlias\": \"1231234567890\"," +
            "     \"amount\": \"100\"," +
            "     \"currency\": \"SEK\"," +
            "     \"message\": \"Kingston USB Flash Drive 8 GB\"," +
            "     \"status\": \"XXX\"," +
            "     \"dateCreated\": \"2015-02-19T22:01:53+01:00\"," +
            "     \"datePaid\": \"2015-02-19T22:03:53+01:00\" " +
            "}";

    String bodyPAID = bodyTemplate.replace("XXX", "PAID");
    String bodyREFUSED = bodyTemplate.replace("XXX", "DECLINED");
    String bodyERROR = bodyTemplate.replace("XXX", "ERROR");
    String bodyException = "hello world";


    @Tested
    private NotificationServiceImpl service;


    @Test
    public void parse() {
        NotificationRequest request = TestUtils.createNotificationRequest(bodyPAID);

        NotificationResponse response = service.parse(request);
        Assertions.assertNotNull(response);

        Assertions.assertEquals(PaymentResponseByNotificationResponse.class, response.getClass());
        PaymentResponseByNotificationResponse paymentResponseByNotificationResponse = (PaymentResponseByNotificationResponse) response;
        Assertions.assertEquals(PaymentResponseSuccess.class, paymentResponseByNotificationResponse.getPaymentResponse().getClass());

    }

    @Test
    public void parseERROR() {
        NotificationRequest request = TestUtils.createNotificationRequest(bodyERROR);

        NotificationResponse response = service.parse(request);
        Assertions.assertNotNull(response);

        Assertions.assertEquals(PaymentResponseByNotificationResponse.class, response.getClass());
        PaymentResponseByNotificationResponse paymentResponseByNotificationResponse = (PaymentResponseByNotificationResponse) response;
        Assertions.assertEquals(PaymentResponseFailure.class, paymentResponseByNotificationResponse.getPaymentResponse().getClass());
        PaymentResponseFailure responseFailure = (PaymentResponseFailure) paymentResponseByNotificationResponse.getPaymentResponse();
        Assertions.assertEquals(FailureCause.PAYMENT_PARTNER_ERROR, responseFailure.getFailureCause());
    }

    @Test
    public void parseREFUSED() {
        NotificationRequest request = TestUtils.createNotificationRequest(bodyREFUSED);

        NotificationResponse response = service.parse(request);
        Assertions.assertNotNull(response);

        Assertions.assertEquals(PaymentResponseByNotificationResponse.class, response.getClass());
        PaymentResponseByNotificationResponse paymentResponseByNotificationResponse = (PaymentResponseByNotificationResponse) response;
        Assertions.assertEquals(PaymentResponseFailure.class, paymentResponseByNotificationResponse.getPaymentResponse().getClass());
        PaymentResponseFailure responseFailure = (PaymentResponseFailure) paymentResponseByNotificationResponse.getPaymentResponse();
        Assertions.assertEquals(FailureCause.REFUSED, responseFailure.getFailureCause());
    }

    @Test
    public void parseEXCEPTION() {
        NotificationRequest request = TestUtils.createNotificationRequest(bodyException);

        NotificationResponse response = service.parse(request);
        Assertions.assertNotNull(response);

        Assertions.assertEquals(PaymentResponseByNotificationResponse.class, response.getClass());
        PaymentResponseByNotificationResponse paymentResponseByNotificationResponse = (PaymentResponseByNotificationResponse) response;
        Assertions.assertEquals(PaymentResponseFailure.class, paymentResponseByNotificationResponse.getPaymentResponse().getClass());

    }

    @Test
    public void notifyTransactionStatus() {
        service.notifyTransactionStatus(null);
        // void ras
    }
}
