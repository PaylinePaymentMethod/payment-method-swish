package com.payline.payment.swish.utils.http;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.payline.payment.swish.bean.common.request.RequestFactory;
import com.payline.payment.swish.bean.common.request.SwishPaymentRequest;
import com.payline.payment.swish.bean.common.request.SwishRefundRequest;
import com.payline.payment.swish.bean.common.response.SwishErrorResponse;
import com.payline.payment.swish.bean.common.response.SwishRefundResponse;
import com.payline.payment.swish.exception.HttpCallException;
import com.payline.payment.swish.exception.InvalidDataException;
import com.payline.payment.swish.exception.PluginException;
import com.payline.payment.swish.utils.properties.constants.ConfigurationConstants;
import com.payline.payment.swish.utils.properties.properties.ConfigProperties;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.configuration.request.ContractParametersCheckRequest;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.refund.request.RefundRequest;
import com.payline.pmapi.logger.LogManager;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Created by Thales on  27/11/2018
 */
public class SwishHttpClient {
    // partnerConfiguration keys
    public static final String URL_KEY = "url";
    private int retries;

    // headers data
    private static final String CONTENT_TYPE_KEY = "Content-Type";
    private static final String CONTENT_TYPE = "application/json";

    private static final Logger LOGGER = LogManager.getLogger(SwishHttpClient.class);
    private final Gson parser;
    private CloseableHttpClient client;
    protected ConfigProperties config = ConfigProperties.getInstance();
    private AtomicBoolean initialized = new AtomicBoolean();

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

    /**
     * Initialize the instance.
     */
    public void init() {
        if (this.initialized.compareAndSet(false, true)) {
            // Retrieve config properties
            int connectionRequestTimeout;
            int connectTimeout;
            int socketTimeout;
            try {
                // request config timeouts (in seconds)
                connectionRequestTimeout = Integer.parseInt(config.get(ConfigurationConstants.CONFIG_HTTP_READ_TIMEOUT));
                connectTimeout = Integer.parseInt(config.get(ConfigurationConstants.CONFIG_HTTP_CONNECT_TIMEOUT));
                socketTimeout = Integer.parseInt(config.get(ConfigurationConstants.CONFIG_HTTP_WRITE_TIMEOUT));

                // number of retry attempts
                this.retries = Integer.parseInt(config.get(ConfigurationConstants.HTTP_RETRY_NBR));
            } catch (NumberFormatException e) {
                throw new PluginException("plugin error: http.* properties must be integers", e);
            }

            // Create RequestConfig
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectionRequestTimeout(connectionRequestTimeout * 1000)
                    .setConnectTimeout(connectTimeout * 1000)
                    .setSocketTimeout(socketTimeout * 1000)
                    .build();

            // Instantiate Apache HTTP client
            this.client = HttpClientBuilder.create()
                    .useSystemProperties()
                    .setDefaultRequestConfig(requestConfig)
                    .setSSLSocketFactory(new SSLConnectionSocketFactory(HttpsURLConnection.getDefaultSSLSocketFactory(), SSLConnectionSocketFactory.getDefaultHostnameVerifier()))
                    .build();
        }
    }

    /**
     * Send a POST request.
     *
     * @param body Request body
     * @return The response returned from the HTTP call
     * @throws HttpCallException COMMUNICATION_ERROR
     */
    StringResponse doPost(URI uri, Header[] headers, HttpEntity body) {
        final HttpPost httpPostRequest = new HttpPost(uri);
        httpPostRequest.setHeaders(headers);
        httpPostRequest.setHeaders(headers);
        httpPostRequest.setEntity(body);

        return getStringResponse(httpPostRequest);
    }


    StringResponse doGet(URI uri, Header[] headers) {
        final HttpGet httpGetRequest = new HttpGet(uri);
        httpGetRequest.setHeaders(headers);

        return getStringResponse(httpGetRequest);
    }

    private StringResponse getStringResponse(HttpRequestBase httpRequest) {
        final long start = System.currentTimeMillis();
        int count = 0;
        StringResponse strResponse = null;
        String errMsg = null;

        while (count < retries && strResponse == null) {
            LOGGER.info("Start call to partner API [{} {}] (attempt {})", httpRequest.getMethod(), httpRequest.getURI(), count);

            try (CloseableHttpResponse httpResponse = this.client.execute(httpRequest)) {

                strResponse = new StringResponse();
                strResponse.setCode(httpResponse.getStatusLine().getStatusCode());

                // get the transactionId in header if exists
                if (httpResponse.getFirstHeader("Location") != null) {
                    strResponse.setMessage(httpResponse.getFirstHeader("Location").getElements()[0].getName());
                }

                // get the body response if exists
                if (httpResponse.getEntity() != null) {
                    final String responseAsString = EntityUtils.toString(httpResponse.getEntity());
                    strResponse.setContent(responseAsString);
                }

                final long end = System.currentTimeMillis();
                LOGGER.info("End partner call [T: {}ms] [CODE: {}]", end - start, strResponse.getCode());

            } catch (final IOException e) {
                LOGGER.error("Error while partner call [T: {}ms]", System.currentTimeMillis() - start, e);
                strResponse = null;
                errMsg = e.getMessage();
            } finally {
                count++;
            }
        }

        if (strResponse == null) {
            if (errMsg == null) {
                throw new HttpCallException("Http response is empty");
            }
            throw new HttpCallException(errMsg);
        }
        return strResponse;
    }

    private Header[] createHeaders() {
        Header[] headers = new Header[1];
        headers[0] = new BasicHeader(CONTENT_TYPE_KEY, CONTENT_TYPE);
        return headers;
    }


    /**
     * create a transaction request, call Swish server and check the response
     *
     * @param contractParametersCheckRequest the request
     * @throws PluginException
     */
    public void testConnection(ContractParametersCheckRequest contractParametersCheckRequest) {
        try {
            // create all data needed to do the call
            SwishPaymentRequest swishRequest = RequestFactory.fromPaylineRequest(contractParametersCheckRequest);
            String body = swishRequest.toString();
            String baseUrl = contractParametersCheckRequest.getPartnerConfiguration().getProperty(URL_KEY);
            Header[] headers = createHeaders();
            URI uri = URIService.createCreateTransactionUri(baseUrl);

            // do the http call
            StringResponse response = doPost(uri, headers, new StringEntity(body));

            // check the response
            checkHttpCode(response);

        } catch (UnsupportedEncodingException e) {
            throw new HttpCallException(BODY_CREATION_ERROR);
        }
    }

    /**
     * create a transaction request, call Swish server, check the response and return the transactionId extracted from the response header
     *
     * @param paymentRequest
     * @throws PluginException
     */
    public String createTransaction(PaymentRequest paymentRequest) {
        try {
            // create all data needed to do the call
            SwishPaymentRequest swishRequest = RequestFactory.fromPaylineRequest(paymentRequest);
            String body = swishRequest.toString();
            String baseUrl = paymentRequest.getPartnerConfiguration().getProperty(URL_KEY);
            Header[] headers = createHeaders();
            URI uri = URIService.createCreateTransactionUri(baseUrl);

            // do the http call
            StringResponse response = doPost(uri, headers, new StringEntity(body));

            // check the response
            checkHttpCode(response);

            // get the transaction Id and return it
            return response.getMessage().split("/api/v1/paymentrequests/")[1];
        } catch (UnsupportedEncodingException e) {
            throw new HttpCallException(BODY_CREATION_ERROR);
        }

    }

    /**
     * create a refund request, call Swish server, check the response and return the refundId extracted from the response header
     *
     * @param refundRequest
     * @return
     * @throws PluginException
     */
    public String createRefund(RefundRequest refundRequest) {
        try {
            // create all data needed to do the call
            SwishRefundRequest swishRequest = RequestFactory.fromPaylineRequest(refundRequest);
            String body = swishRequest.toString();
            String baseUrl = refundRequest.getPartnerConfiguration().getProperty(URL_KEY);
            Header[] headers = createHeaders();
            URI uri = URIService.createCreateRefundUri(baseUrl);

            // do the http call
            StringResponse response = doPost(uri, headers, new StringEntity(body));

            // check the response
            checkHttpCode(response);

            // get the refund Id and return it
            return response.getMessage().split("/api/v1/refunds/")[1];

        } catch (UnsupportedEncodingException e) {
            throw new HttpCallException(BODY_CREATION_ERROR);
        }
    }

    /**
     * Call Swish server to get the status of a refund
     *
     * @param refundRequest
     * @param id
     * @return
     * @throws PluginException
     */
    public synchronized SwishRefundResponse getRefundStatus(RefundRequest refundRequest, String id) {
        // create all data needed to do the call
        String baseUrl = refundRequest.getPartnerConfiguration().getProperty(URL_KEY);
        Header[] headers = createHeaders();
        URI uri = URIService.createRefundStatusUri(baseUrl, id);

        // do the http call
        StringResponse response = doGet(uri, headers);

        // check the response
        checkHttpCode(response);

        // get the response body and return it
        return parser.fromJson(response.getContent(), SwishRefundResponse.class);
    }


    /**
     * Check the HttpCode response and throw an {@link InvalidDataException} if the httpCode is not 200 or 201
     *
     * @param response
     * @throws PluginException
     */
    public void checkHttpCode(StringResponse response) {
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
                String errorMessage = errorResponse[0].getErrorMessage() + ":" + errorResponse[0].getErrorCode();
                throw new InvalidDataException(errorMessage);
            case 401:
            case 415:
                // COMMUNICATION_ERROR
                throw new HttpCallException(String.valueOf(response.getCode()));
            case 500:
                // PARTNER_UNKNOWN_ERROR
            default:
                throw new PluginException(String.valueOf(response.getCode()), FailureCause.PAYMENT_PARTNER_ERROR);
        }
    }
}
