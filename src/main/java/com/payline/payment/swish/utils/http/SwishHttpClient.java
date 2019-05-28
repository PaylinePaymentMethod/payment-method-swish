package com.payline.payment.swish.utils.http;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.payline.payment.swish.bean.common.request.SwishPaymentRequest;
import com.payline.payment.swish.bean.common.request.SwishRefundRequest;
import com.payline.payment.swish.bean.common.response.SwishErrorResponse;
import com.payline.payment.swish.bean.common.response.SwishRefundResponse;
import com.payline.payment.swish.exception.HttpCallException;
import com.payline.payment.swish.exception.InvalidDataException;
import com.payline.payment.swish.exception.PluginTechnicalException;
import com.payline.pmapi.bean.configuration.request.ContractParametersCheckRequest;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.refund.request.RefundRequest;
import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;

import java.io.UnsupportedEncodingException;


/**
 * Created by Thales on  27/11/2018
 */
public class SwishHttpClient extends AbstractHttpClient {
    // partnerConfiguration keys
    public static final String URL_KEY = "url";

    // paths data
    private static final String PATH_CREATE_TRANSACTION = "/api/v1/paymentrequests";
    private static final String PATH_CREATE_REFUND = "/api/v1/refunds";

    // headers data
    private static final String CONTENT_TYPE_KEY = "Content-Type";
    private static final String CONTENT_TYPE = "application/json";

    private Gson parser;

    // messages data
    private static final String BODY_CREATION_ERROR = "unable to create body";

    /**
     * Instantiate a HTTP client with default values.
     */
    private SwishHttpClient() {
        super();
        this.parser = new GsonBuilder().create();
    }

    /**
     * Singleton Holder
     */
    private static class SingletonHolder {
        private static final SwishHttpClient INSTANCE = new SwishHttpClient();
    }

    /**
     * @return the singleton instance
     */
    public static SwishHttpClient getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private Header[] createHeaders() {
        Header[] headers = new Header[1];
        headers[0] = new BasicHeader(CONTENT_TYPE_KEY, CONTENT_TYPE);
        return headers;
    }


    /**
     * create a transaction request, call Swish server and check the response
     *
     * @param paylineRequest the request
     * @throws PluginTechnicalException
     */
    public void testConnection(ContractParametersCheckRequest paylineRequest) throws PluginTechnicalException {
        try {
            // create all data needed to do the call
            SwishPaymentRequest swishRequest = new SwishPaymentRequest.Builder().fromPaylineRequest(paylineRequest);
            String body = swishRequest.toString();
            String url = paylineRequest.getPartnerConfiguration().getProperty(URL_KEY);
            Header[] headers = createHeaders();

            // do the http call
            StringResponse response = super.doPost(url, PATH_CREATE_TRANSACTION, headers, new StringEntity(body));

            // check the response
            checkHttpCode(response);

        } catch (UnsupportedEncodingException e) {
            throw new HttpCallException(BODY_CREATION_ERROR, "SwishHttpClient.testConnection");
        }
    }

    /**
     * create a transaction request, call Swish server, check the response and return the transactionId extracted from the response header
     *
     * @param paylineRequest
     * @throws PluginTechnicalException
     */
    public String createTransaction(PaymentRequest paylineRequest) throws PluginTechnicalException {
        try {
            // create all data needed to do the call
            SwishPaymentRequest swishRequest = new SwishPaymentRequest.Builder().fromPaylineRequest(paylineRequest);
            String body = swishRequest.toString();
            String url = paylineRequest.getPartnerConfiguration().getProperty(URL_KEY);
            Header[] headers = createHeaders();

            // do the http call
            StringResponse response = super.doPost(url, PATH_CREATE_TRANSACTION, headers, new StringEntity(body));

            // check the response
            checkHttpCode(response);

            // get the transaction Id and return it
            return response.getMessage().split(PATH_CREATE_TRANSACTION + "/")[1];// todo verifier que ya bien ce qu'il faut (pareil pour le refund!!)
        } catch (UnsupportedEncodingException e) {
            throw new HttpCallException(BODY_CREATION_ERROR, "SwishHttpClient.createTransaction");
        }

    }

    /**
     * create a refund request, call Swish server, check the response and return the refundId extracted from the response header
     *
     * @param paylineRequest
     * @return
     * @throws PluginTechnicalException
     */
    public String createRefund(RefundRequest paylineRequest) throws PluginTechnicalException {
        try {
            // create all data needed to do the call
            SwishRefundRequest swishRequest = new SwishRefundRequest.Builder().fromPaylineRequest(paylineRequest);
            String body = swishRequest.toString();
            String url = paylineRequest.getPartnerConfiguration().getProperty(URL_KEY);
            Header[] headers = createHeaders();

            // do the http call
            StringResponse response = super.doPost(url, PATH_CREATE_REFUND, headers, new StringEntity(body));

            // check the response
            checkHttpCode(response);

            // get the refund Id and return it
            return response.getMessage().split(PATH_CREATE_REFUND + "/")[1];

        } catch (UnsupportedEncodingException e) {
            throw new HttpCallException(BODY_CREATION_ERROR, "SwishHttpClient.createRefund");
        }
    }

    /**
     * Call Swish server to get the status of a refund
     *
     * @param paylineRequest
     * @param id
     * @return
     * @throws PluginTechnicalException
     */
    public synchronized SwishRefundResponse getRefundStatus(RefundRequest paylineRequest, String id) throws PluginTechnicalException {
        // create all data needed to do the call
        String url = paylineRequest.getPartnerConfiguration().getProperty(URL_KEY);
        Header[] headers = createHeaders();
        String path = PATH_CREATE_REFUND + "/" + id;

        // do the http call
        StringResponse response = super.doGet(url, path, headers);

        // check the response
        checkHttpCode(response);

        // get the response body and return it
        return parser.fromJson(response.getContent(), SwishRefundResponse.class);
    }


    /**
     * Check the HttpCode response and throw an {@link InvalidDataException} if the httpCode is not 200 or 201
     *
     * @param response
     * @throws PluginTechnicalException
     */
    public void checkHttpCode(StringResponse response) throws PluginTechnicalException {
        switch (response.getCode()) {
            case 200:
            case 201:
                // response OK
                return;
            case 400:
            case 403:
            case 422:
                // INVALID_DATA

                // get the first error and create an Exception from it
                SwishErrorResponse[] errorResponse = parser.fromJson(response.getContent(), SwishErrorResponse[].class);
                throw new InvalidDataException(errorResponse[0].getErrorMessage(), errorResponse[0].getErrorCode());
            case 401:
            case 415:
                // COMMUNICATION_ERROR
                throw new HttpCallException(String.valueOf(response.getCode()), "SwishHttpClient.checkHttpCode");
            case 500:
                // PARTNER_UNKNOWN_ERROR
            default:
                throw new PluginTechnicalException("500", "SwishHttpClient.checkHttpCode");
        }
    }
}
