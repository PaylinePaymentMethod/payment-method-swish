package com.payline.payment.swish.service.impl;

import com.payline.payment.swish.exception.PluginTechnicalException;
import com.payline.payment.swish.utils.http.SwishHttpClient;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseActiveWaiting;
import com.payline.pmapi.logger.LogManager;
import com.payline.pmapi.service.PaymentService;
import org.apache.logging.log4j.Logger;

public class PaymentServiceImpl implements PaymentService {

    private SwishHttpClient httpClient;

    private static final Logger LOGGER = LogManager.getLogger(PaymentServiceImpl.class);


    public PaymentServiceImpl() {
        this.httpClient = SwishHttpClient.getInstance();
    }

    @Override
    public PaymentResponse paymentRequest(PaymentRequest paymentRequest) {

        try {
            httpClient.init( paymentRequest.getPartnerConfiguration() );
            httpClient.createTransaction(paymentRequest);

            return new PaymentResponseActiveWaiting();
        } catch (PluginTechnicalException e) {
            LOGGER.error("unable init the payment", e);
            return e.toPaymentResponseFailure();
        }
    }
}
