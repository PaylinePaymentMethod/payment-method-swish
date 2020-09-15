package com.payline.payment.swish.utils.http;

import com.payline.payment.swish.exception.PluginException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.URI;

class URIServiceTest {
    String baseUrl = "http://foo.bar.baz";

    @Test
    void createCreateTransactionUri() {
        URI url = URIService.createCreateTransactionUri(baseUrl);
        Assertions.assertEquals("http://foo.bar.baz/api/v1/paymentrequests", url.toString());
    }

    @Test
    void createCreateTransactionUriException() {
        Assertions.assertThrows(PluginException.class, ()-> URIService.createCreateTransactionUri("//##") );
    }

    @Test
    void createCreateRefundUri() {
        URI url = URIService.createCreateRefundUri(baseUrl);
        Assertions.assertEquals("http://foo.bar.baz/api/v1/refunds", url.toString());
    }

    @Test
    void createCreateRefundUriException() {
        Assertions.assertThrows(PluginException.class, ()-> URIService.createCreateRefundUri("//##") );
    }

    @Test
    void createRefundStatusUri() {
        URI url = URIService.createRefundStatusUri(baseUrl, "123");
        Assertions.assertEquals("http://foo.bar.baz/api/v1/refunds/123", url.toString());
    }

    @Test
    void createRefundStatusUriException() {
        Assertions.assertThrows(PluginException.class, ()-> URIService.createRefundStatusUri("//##", "foo") );
    }
}