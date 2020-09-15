package com.payline.payment.swish.bean.common;

import com.payline.payment.swish.bean.common.request.RequestFactory;
import com.payline.payment.swish.bean.common.request.SwishRefundRequest;
import com.payline.payment.swish.utils.TestUtils;
import com.payline.pmapi.bean.refund.request.RefundRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SwishRefundRequestTest {

    @Test
    void creation() {
        RefundRequest request = TestUtils.createRefundRequest("123456", "654321");
        SwishRefundRequest swishPaymentRequest = RequestFactory.fromPaylineRequest(request);

        Assertions.assertNotNull(swishPaymentRequest.getPayerPaymentReference());
        Assertions.assertNotNull(swishPaymentRequest.getOriginalPaymentReference());
//        Assertions.assertNotNull(swishPaymentRequest.getPaymentReference());
        Assertions.assertNotNull(swishPaymentRequest.getCallbackUrl());
        Assertions.assertNotNull(swishPaymentRequest.getPayerAlias());
        Assertions.assertNotNull(swishPaymentRequest.getPayeeAlias());
        Assertions.assertNotNull(swishPaymentRequest.getAmount());
        Assertions.assertNotNull(swishPaymentRequest.getCurrency());
        Assertions.assertNotNull(swishPaymentRequest.getMessage());
    }
}
