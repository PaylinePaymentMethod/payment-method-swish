package com.payline.payment.swish.service.impl;

import com.payline.payment.swish.bean.common.response.SwishRefundResponse;
import com.payline.payment.swish.exception.PluginException;
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
    private static final Logger LOGGER = LogManager.getLogger(RefundServiceImpl.class);

    public RefundServiceImpl() {
        this.httpClient = SwishHttpClient.getInstance();
    }

    @Override
    public RefundResponse refundRequest(RefundRequest refundRequest) {
        String refundId = "UNKNOWN";
        RefundResponse refundResponse = null;
        try {
            httpClient.init();

            // do the first call to create a refund
            refundId = httpClient.createRefund(refundRequest);

            // multiple call to get the refund status until we get a final status
            int i = 1;
            while (refundResponse == null) {
                refundResponse = getRefundStatus(refundRequest, refundId, i);
                i++;
            }
        } catch (PluginException e) {
            LOGGER.error("unable to execute the refund", e);
            refundResponse = e.toRefundResponseFailureBuilder()
                    .withPartnerTransactionId(refundId)
                    .build();

        }
        return refundResponse;
    }

    @Override
    public boolean canMultiple() {
        return true;
    }

    @Override
    public boolean canPartial() {
        return true;
    }

    private synchronized RefundResponse getRefundStatus(RefundRequest refundRequest, String refundId, int i){
        RefundResponse refundResponse;

        try {
            // do the second call to get the refund status
            SwishRefundResponse response = httpClient.getRefundStatus(refundRequest, refundId);

            // get the status of the refund
            switch (response.getStatus()) {
                case "PAID":
                    refundResponse = createResponseSuccess(response);
                    break;
                case "CREATED":
                case "DEBITED":
                    if (i < NB_LOOP) {
                        this.wait(WAITING_TIME);
                        refundResponse = null;
                    } else {
                        refundResponse = createResponseFailure(response);
                    }
                    break;
                case "ERROR":
                default:
                    refundResponse = createResponseFailure(response);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.error("unable to wait before calling again", e);
            refundResponse = RefundResponseFailure.RefundResponseFailureBuilder
                    .aRefundResponseFailure()
                    .withPartnerTransactionId(refundId)
                    .withFailureCause(FailureCause.INTERNAL_ERROR)
                    .withErrorCode("Thread interrupted while waiting")
                    .build();
        }

        return refundResponse;
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
