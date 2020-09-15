package com.payline.payment.swish.bean.common.request;

import com.payline.payment.swish.exception.InvalidDataException;
import com.payline.payment.swish.service.impl.ConfigurationServiceImpl;
import com.payline.payment.swish.service.impl.PaymentFormConfigurationServiceImpl;
import com.payline.pmapi.bean.configuration.request.ContractParametersCheckRequest;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.refund.request.RefundRequest;

import static com.payline.payment.swish.bean.common.request.SwishBean.SWEDISH_CURRENCY;

public class RequestFactory {

    private RequestFactory() {
        throw new IllegalStateException("Utility class");
    }


    /**
     * Create a transaction request from a Payline PaymentRequest.
     *
     * @param request the request to convert into a SwishPaymentRequest
     * @return a SwishPaymentRequest after validating it's fields
     */
    public static SwishPaymentRequest fromPaylineRequest(PaymentRequest request) {
        return SwishPaymentRequest.builder()
                .payeePaymentReference(request.getOrder().getReference())
                .callbackUrl(request.getEnvironment().getNotificationURL())
                .payerAlias(request.getPaymentFormContext().getPaymentFormParameter().get(PaymentFormConfigurationServiceImpl.PHONE_KEY))
                .payeeAlias(request.getContractConfiguration().getProperty(ConfigurationServiceImpl.KEY).getValue())
                .amount(request.getAmount().getAmountInSmallestUnit().toString())
                .currency(request.getAmount().getCurrency().getCurrencyCode())
                .message(request.getSoftDescriptor())
                .build();
    }

    /**
     * Create a fake trasaction request from a ContractParametersCheckRequest
     * it is used to test the connection with Swish backend
     *
     * @param paylineRequest the request containing the merchant account
     * @return a SwishPaymentRequest after completing other field with dummy values
     */
    public static SwishPaymentRequest fromPaylineRequest(ContractParametersCheckRequest paylineRequest) {
        return SwishPaymentRequest.builder()
                .payeePaymentReference("123456")
                .callbackUrl("https://this.is.an.url")
                .payerAlias("0612345678")
                .payeeAlias(paylineRequest.getAccountInfo().get(ConfigurationServiceImpl.KEY))
                .amount("1")
                .currency(SWEDISH_CURRENCY)
                .message("test connection")
                .build();
    }




    /**
     * create a refund request from a Payline refundRequest
     *
     * @param paylineRequest the request to convert into a SwishRefundRequest
     * @return a SwishRefundRequest after validating it's fields
     * @throws InvalidDataException
     */
    public static SwishRefundRequest fromPaylineRequest(RefundRequest paylineRequest) {
        return SwishRefundRequest.builder()
                .payerPaymentReference(paylineRequest.getSoftDescriptor())
                .originalPaymentReference(paylineRequest.getPartnerTransactionId())
                .paymentReference(paylineRequest.getTransactionId())
                .callbackUrl(paylineRequest.getEnvironment().getNotificationURL())
                .payeeAlias(paylineRequest.getTransactionAdditionalData())
                .payerAlias(paylineRequest.getContractConfiguration().getProperty(ConfigurationServiceImpl.KEY).getValue())
                .amount(paylineRequest.getAmount().getAmountInSmallestUnit().toString())
                .currency(paylineRequest.getAmount().getCurrency().getCurrencyCode())
                .message(paylineRequest.getOrder().getReference())
                .build();
    }

}
