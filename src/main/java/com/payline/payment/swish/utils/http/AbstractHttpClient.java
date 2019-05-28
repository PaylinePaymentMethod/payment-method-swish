package com.payline.payment.swish.utils.http;

import com.payline.payment.swish.exception.HttpCallException;
import com.payline.payment.swish.exception.InvalidDataException;
import com.payline.payment.swish.exception.PluginTechnicalException;
import com.payline.payment.swish.utils.properties.service.ConfigPropertiesEnum;
import com.payline.payment.swish.utils.security.RSAHolder;
import com.payline.pmapi.bean.configuration.PartnerConfiguration;
import com.payline.pmapi.logger.LogManager;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.SSLContext;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.payline.payment.swish.utils.properties.constants.ConfigurationConstants.*;

/**
 * This utility class provides a basic HTTP client to send requests, using OkHttp library.
 * It must be extended to match each payment method needs.
 */
public abstract class AbstractHttpClient {

    private static final Logger LOGGER = LogManager.getLogger(AbstractHttpClient.class);

    public static final String PARTNER_CONFIGURATION_CERT = "clientCertificateChain";
    public static final String PARTNER_CONFIGURATION_PK = "clientPrivateKey";

    private CloseableHttpClient client;
    private AtomicBoolean initialized = new AtomicBoolean();

    /**
     * Instantiate a HTTP client.
     */
    protected AbstractHttpClient() {
    }

    /**
     * Initialize HTTP client from partner configuration
     *
     * @param partnerConfiguration the partner configuration
     * @throws PluginTechnicalException If an error occurs building the SSL context
     */
    public void init( PartnerConfiguration partnerConfiguration ) throws PluginTechnicalException {
        if( this.initialized.compareAndSet(false, true) ) {
            int connectTimeout = Integer.parseInt(ConfigPropertiesEnum.INSTANCE.get(CONFIG_HTTP_CONNECT_TIMEOUT));
            int requestTimeout = Integer.parseInt(ConfigPropertiesEnum.INSTANCE.get(CONFIG_HTTP_WRITE_TIMEOUT));
            int readTimeout = Integer.parseInt(ConfigPropertiesEnum.INSTANCE.get(CONFIG_HTTP_READ_TIMEOUT));


            final RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(connectTimeout * 1000)
                    .setConnectionRequestTimeout(requestTimeout * 1000)
                    .setSocketTimeout(readTimeout * 1000)
                    .build();

            SSLContext sslContext;
            try {
                if( partnerConfiguration.getProperty( PARTNER_CONFIGURATION_CERT ) == null ){
                    throw new InvalidDataException("Missing client certificate chain from partner configuration (sentitive properties)", "partnerConfiguration." + PARTNER_CONFIGURATION_CERT);
                }
                if( partnerConfiguration.getProperty( PARTNER_CONFIGURATION_PK ) == null ){
                    throw new InvalidDataException("Missing client private key from partner configuration (sentitive properties)", "partnerConfiguration." + PARTNER_CONFIGURATION_PK);
                }

                RSAHolder rsaHolder = new RSAHolder.RSAHolderBuilder()
                        .parseChain( partnerConfiguration.getSensitiveProperties().get( PARTNER_CONFIGURATION_CERT ) )
                        .parsePrivateKey( partnerConfiguration.getSensitiveProperties().get( PARTNER_CONFIGURATION_PK ) )
                        .build();

                // SSL context
                sslContext = SSLContexts.custom()
                        .loadKeyMaterial(rsaHolder.getKeyStore(), rsaHolder.getPrivateKeyPassword())
                        .build();
            } catch ( IOException | GeneralSecurityException | IllegalStateException e ){
                throw new PluginTechnicalException( e, "A problem occurred initializing SSL context" );
            }

            this.client = HttpClientBuilder.create()
                    .useSystemProperties()
                    .setDefaultRequestConfig(requestConfig)
                    .setDefaultCredentialsProvider(new BasicCredentialsProvider())
                    .setSSLContext( sslContext )
                    .build();
        }
    }

    /**
     * Send a POST request.
     *
     * @param url  URL scheme + host
     * @param path URL path
     * @param body Request body
     * @return The response returned from the HTTP call
     * @throws HttpCallException COMMUNICATION_ERROR
     */
    protected StringResponse doPost(String url, String path, Header[] headers, HttpEntity body) throws HttpCallException {
        final String methodName = "doPost";

        try {
            URI uri = new URI(url + path);

            final HttpPost httpPostRequest = new HttpPost(uri);
            httpPostRequest.setHeaders(headers);
            httpPostRequest.setHeaders(headers);
            httpPostRequest.setEntity(body);

            return getStringResponse(url, methodName, httpPostRequest);

        } catch (URISyntaxException e) {
            LOGGER.error(e.getMessage(), e);
            throw new HttpCallException(e, "AbstractHttpClient.doPost.URISyntaxException");
        }


    }

    /**
     * Send a GET request
     *
     * @param url  URL RL scheme + host
     * @param path URL path
     * @return The response returned from the HTTP call
     * @throws HttpCallException COMMUNICATION_ERROR
     */
    protected StringResponse doGet(String url, String path, Header[] headers) throws HttpCallException {

        final String methodName = "doGet";
        try {
            URI uri = new URI(url + path);

            final HttpGet httpGetRequest = new HttpGet(uri);
            httpGetRequest.setHeaders(headers);

            return getStringResponse(url, methodName, httpGetRequest);
        } catch (URISyntaxException e) {
            throw new HttpCallException(e, "AbstractHttpClient.doGet.URISyntaxException");
        }
    }

    private StringResponse getStringResponse(String url, String methodName, HttpRequestBase httpPostRequest) throws HttpCallException {
        final long start = System.currentTimeMillis();
        int count = 0;
        StringResponse strResponse = null;
        String errMsg = null;

        int nbrEssais = Integer.parseInt(ConfigPropertiesEnum.INSTANCE.get(HTTP_RETRY_NBR));

        while (count < nbrEssais && strResponse == null) {
            LOGGER.info("Start partner call... [URL: {}]", url);

            try (CloseableHttpResponse httpResponse = this.client.execute(httpPostRequest)) {

                strResponse = new StringResponse();
                strResponse.setCode(httpResponse.getStatusLine().getStatusCode());

                // get the transactioId in header if exists
                if (httpResponse.getFirstHeader("Location") != null){
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
                throw new HttpCallException("Http response is empty", "AbstractHttpClient." + methodName + " : empty partner response");
            }
            throw new HttpCallException(errMsg, "AbstractHttpClient." + methodName + ".IOException");
        }
        return strResponse;
    }

}
