package com.payline.payment.swish.utils.http;

import com.payline.payment.swish.exception.InvalidDataException;
import org.apache.http.client.utils.URIBuilder;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

public class URIService {
    private static final String BASE_PATH = "api";
    private static final String VERSION_PATH = "v1";
    private static final String CREATE_PATH = "paymentrequests";
    private static final String REFUND_PATH = "refunds";


    private URIService() {
        throw new IllegalStateException("Utility class");
    }

    public static URI createCreateTransactionUri(String baseUrl){
        try {
            URIBuilder builder = new URIBuilder(baseUrl);
            builder.setPathSegments(BASE_PATH, VERSION_PATH, CREATE_PATH);
            return builder.build().toURL().toURI();
        } catch (MalformedURLException | URISyntaxException e) {
            throw new InvalidDataException("Unable to create URL to call createTransaction", e);
        }
    }

    public static URI createCreateRefundUri(String baseUrl){
        try {
            URIBuilder builder = new URIBuilder(baseUrl);
            builder.setPathSegments(BASE_PATH, VERSION_PATH, REFUND_PATH);
            return builder.build().toURL().toURI();
        } catch (MalformedURLException | URISyntaxException e) {
            throw new InvalidDataException("Unable to create URL to call createRefund", e);
        }
    }

    public static URI createRefundStatusUri(String baseUrl, String id){
        try {
            URIBuilder builder = new URIBuilder(baseUrl);
            builder.setPathSegments(BASE_PATH, VERSION_PATH, REFUND_PATH, id);
            return builder.build().toURL().toURI();
        } catch (MalformedURLException | URISyntaxException e) {
            throw new InvalidDataException("Unable to create URL to call getRefundStatus", e);
        }
    }


}
