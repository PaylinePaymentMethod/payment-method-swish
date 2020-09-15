package com.payline.payment.swish.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.payline.payment.swish.bean.common.response.SwishPaymentResponse;
import com.payline.payment.swish.utils.PluginUtils;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.common.TransactionCorrelationId;
import com.payline.pmapi.bean.notification.request.NotificationRequest;
import com.payline.pmapi.bean.notification.response.NotificationResponse;
import com.payline.pmapi.bean.notification.response.impl.PaymentResponseByNotificationResponse;
import com.payline.pmapi.bean.payment.request.NotifyTransactionStatusRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.buyerpaymentidentifier.impl.EmptyTransactionDetails;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFailure;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseSuccess;
import com.payline.pmapi.logger.LogManager;
import com.payline.pmapi.service.NotificationService;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class NotificationServiceImpl implements NotificationService {
    private static final Logger LOGGER = LogManager.getLogger(NotificationServiceImpl.class);

    private Gson parser;

    public NotificationServiceImpl() {
        this.parser = new GsonBuilder().create();
    }

    @Override
    public NotificationResponse parse(NotificationRequest notificationRequest) {
        PaymentResponse paymentResponse;
        String transactionId = "UNKNOWN";

        try {
            // get the body of the notification
            String notificationBody = PluginUtils.inputStreamToString(notificationRequest.getContent());

            // create a swish response from this body
            SwishPaymentResponse swishPaymentResponse = parser.fromJson(notificationBody, SwishPaymentResponse.class);

            // check the status of the paymentResponse
            if ("PAID".equals(swishPaymentResponse.getStatus())) {
                paymentResponse = PaymentResponseSuccess.PaymentResponseSuccessBuilder.aPaymentResponseSuccess()
                        .withStatusCode(swishPaymentResponse.getStatus())
                        .withPartnerTransactionId(swishPaymentResponse.getId())
                        .withTransactionDetails(new EmptyTransactionDetails())
                        .withTransactionAdditionalData(swishPaymentResponse.getPayerAlias())
                        .build();

            } else {// case "DECLINED" or "ERROR"
                FailureCause cause = FailureCause.PAYMENT_PARTNER_ERROR;
                if ("DECLINED".equals(swishPaymentResponse.getStatus())){
                    cause = FailureCause.REFUSED;
                }

                paymentResponse = PaymentResponseFailure.PaymentResponseFailureBuilder.aPaymentResponseFailure()
                        .withErrorCode(swishPaymentResponse.getStatus())
                        .withFailureCause(cause)
                        .withPartnerTransactionId(swishPaymentResponse.getId())
                        .build();
            }

        } catch (IOException | JsonSyntaxException e) {
            // Unable to read the body of notificationRequest
            LOGGER.error("Unable to read the body of notificationRequest",e);
            paymentResponse = PaymentResponseFailure.PaymentResponseFailureBuilder.aPaymentResponseFailure()
                    .withErrorCode("Unable to read Body")
                    .withFailureCause(FailureCause.INVALID_FIELD_FORMAT)
                    .withPartnerTransactionId(transactionId)
                    .build();
        }

        TransactionCorrelationId correlationId = TransactionCorrelationId.TransactionCorrelationIdBuilder
                .aCorrelationIdBuilder()
                .withType(TransactionCorrelationId.CorrelationIdType.PARTNER_TRANSACTION_ID)
                .withValue(transactionId)
                .build();

        return PaymentResponseByNotificationResponse.PaymentResponseByNotificationResponseBuilder
                .aPaymentResponseByNotificationResponseBuilder()
                .withPaymentResponse(paymentResponse)
                .withTransactionCorrelationId(correlationId)
                .build();
    }

    @Override
    public void notifyTransactionStatus(NotifyTransactionStatusRequest notifyTransactionStatusRequest) {
        //ras.

    }
}
