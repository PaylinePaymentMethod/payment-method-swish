package com.payline.payment.swish.service.impl;

import com.payline.payment.swish.bean.common.response.SwishRefundResponse;
import com.payline.payment.swish.exception.PluginTechnicalException;
import com.payline.payment.swish.utils.http.SwishHttpClient;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.refund.request.RefundRequest;
import com.payline.pmapi.bean.refund.response.RefundResponse;
import com.payline.pmapi.bean.refund.response.impl.RefundResponseFailure;
import com.payline.pmapi.bean.refund.response.impl.RefundResponseSuccess;
import com.payline.pmapi.logger.LogManager;
import com.payline.pmapi.service.RefundService;
import org.apache.logging.log4j.Logger;

public class RefundServiceImpl implements RefundService {
    private static final int NB_LOOP = 10;
    private static final int WAITING_TIME = 1000; // in millisecond

    private SwishHttpClient httpClient;
    private static final Logger LOGGER = LogManager.getLogger(PaymentServiceImpl.class);

    public RefundServiceImpl() {
        this.httpClient = SwishHttpClient.getInstance();
    }

    @Override
    public synchronized RefundResponse refundRequest(RefundRequest refundRequest) {
        String refundId = "UNKNOWN";
        try {
            httpClient.init( refundRequest.getPartnerConfiguration() );

            // do the first call to create a refund
            refundId = httpClient.createRefund(refundRequest);

            int i = 0;
            while (true) {
                try {
                    // do the second call to get the refund status
                    SwishRefundResponse response = httpClient.getRefundStatus(refundRequest, refundId);

                    // get the status of the refund
                    switch (response.getStatus()) {
                        case "PAID":
                            return createResponseSuccess(response);
                        case "ERROR":
                            return createResponseFailure(response);
                        case "CREATED":
                        case "DEBITED":
                            i += 1;
                            if (i < NB_LOOP) {
                                this.wait(WAITING_TIME);
                            } else {
                                return createResponseFailure(response);
                            }
                            break;
                        default:
                            return createResponseFailure(response);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    LOGGER.error("unable to wait before calling again", e);
                    return RefundResponseFailure.RefundResponseFailureBuilder
                            .aRefundResponseFailure()
                            .withPartnerTransactionId(refundId)
                            .withFailureCause(FailureCause.INTERNAL_ERROR)
                            .withErrorCode("Thread interrupted while waiting")
                            .build();

                }
            }
        } catch (PluginTechnicalException e) {
            LOGGER.error("unable to execute the refund", e);
            return e.toRefundResponseFailure(refundId);

        }
    }

    @Override
    public boolean canMultiple() {
        return true;
    }

    @Override
    public boolean canPartial() {
        return true;
    }


    private RefundResponseFailure createResponseFailure(SwishRefundResponse response) {
        return RefundResponseFailure.RefundResponseFailureBuilder
                .aRefundResponseFailure()
                .withPartnerTransactionId(response.getId())
                .withErrorCode(response.getStatus())
                .withFailureCause(FailureCause.PARTNER_UNKNOWN_ERROR)
                .build();
    }

    private RefundResponseSuccess createResponseSuccess(SwishRefundResponse response) {
        return RefundResponseSuccess.RefundResponseSuccessBuilder
                .aRefundResponseSuccess()
                .withPartnerTransactionId(response.getId())
                .withStatusCode(response.getStatus())
                .build();
    }

}
