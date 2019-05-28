package com.payline.payment.swish.utils.http;

import com.payline.payment.swish.bean.common.response.SwishRefundResponse;
import com.payline.payment.swish.exception.HttpCallException;
import com.payline.payment.swish.exception.InvalidDataException;
import com.payline.payment.swish.exception.PluginTechnicalException;
import com.payline.payment.swish.utils.TestUtils;
import com.payline.pmapi.bean.configuration.request.ContractParametersCheckRequest;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.refund.request.RefundRequest;
import mockit.Capturing;
import mockit.Expectations;
import mockit.Tested;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SwishHttpClientTest {
    @Tested
    @Capturing
    private SwishHttpClient client = SwishHttpClient.getInstance();
    private String goodContent = "{" +
            "     \"id\": \"ABC2D7406ECE4542A80152D909EF9F6B\"," +
            "     \"payerPaymentReference\": \"0123456789\"," +
            "     \"originalPaymentReference\": \"6D6CD7406ECE4542A80152D909EF9F6B\"," +
            "     \"callbackUrl\": \"https://example.com/api/swishcb/refunds\"," +
            "     \"payerAlias\": \"1231234567890\"," +
            "     \"payeeAlias\": \"07211234567\"," +
            "     \"amount\": \"100\"," +
            "     \"currency\": \"SEK\"," +
            "     \"message\": \"Refund for Kingston USB Flash Drive 8 GB\"," +
            "     \"status\": \"PAID\"," +
            "     \"dateCreated\": \"2015-02-19T22:01:53+01:00\"," +
            "     \"datePaid\": \"2015-02-19T22:03:53+01:00\"" +
            "}";

    private String badContent = "[{" +
            "     \"errorCode\": \"FF08\"," +
            "     \"errorMessage\": \"\"," +
            "     \"additionalInformation\": \"payerPaymentReference is invalid\"" +
            "}]";



    @Test
    void testConnection() throws Exception {
        ContractParametersCheckRequest request = TestUtils.createContractParametersCheckRequest();

        // Mock the httpCall
        StringResponse response = new StringResponse() {{
            setCode(200);
            setMessage("a message");
            setContent("a content");
        }};
        new Expectations() {{
            client.doPost(anyString, anyString, (Header[]) any, (HttpEntity) any);
            result = response;
        }};

        client.testConnection(request);
    }


    @Test
    void createTransaction() throws PluginTechnicalException {
        PaymentRequest request = TestUtils.createDefaultPaymentRequest();

        // Mock the httpCall
        StringResponse response = new StringResponse() {{
            setCode(200);
            setMessage("http://this.is.an.url/api/v1/paymentrequests/transactionId");
            setContent("a content");
        }};
        new Expectations() {{
            client.doPost(anyString, anyString, (Header[]) any, (HttpEntity) any);
            result = response;
        }};

        String transactionId = client.createTransaction(request);
        Assertions.assertNotNull(transactionId);
    }


    @Test
    void createRefund() throws PluginTechnicalException {
        RefundRequest request = TestUtils.createRefundRequest("1","2");

        // Mock the httpCall
        StringResponse response = new StringResponse() {{
            setCode(200);
            setMessage("http://this.is.an.url/api/v1/refunds/transactionId");
            setContent("a content");
        }};
        new Expectations() {{
            client.doPost(anyString, anyString, (Header[]) any, (HttpEntity) any);
            result = response;
        }};

        String transactionId = client.createRefund(request);
        Assertions.assertNotNull(transactionId);
    }

    @Test
    void getRefundStatus() throws PluginTechnicalException {
        String partnerTransactionId = "foo";
        RefundRequest request = TestUtils.createRefundRequest("1",partnerTransactionId);

        // Mock the httpCall
        StringResponse response = new StringResponse() {{
            setCode(200);
            setMessage("this is a message");
            setContent(goodContent);
        }};
        new Expectations() {{
            client.doGet(anyString, anyString, (Header[]) any);
            result = response;
        }};

        SwishRefundResponse refundResponse = client.getRefundStatus(request, partnerTransactionId);
        Assertions.assertNotNull(refundResponse);
    }


    @Test
    void checkHttpCode200() throws Exception {
        StringResponse stringResponse = new StringResponse();
        stringResponse.setCode(200);
        stringResponse.setMessage("a message");
        stringResponse.setContent("a content");

        client.checkHttpCode(stringResponse);
    }

    @Test
    void checkHttpCode400() throws Exception {
        StringResponse stringResponse = new StringResponse();
        stringResponse.setCode(400);
        stringResponse.setMessage("a message");
        stringResponse.setContent(badContent);

        Assertions.assertThrows(InvalidDataException.class, () -> client.checkHttpCode(stringResponse));
    }

    @Test
    void checkHttpCode415() throws Exception {
        StringResponse stringResponse = new StringResponse();
        stringResponse.setCode(415);
        stringResponse.setMessage("a message");
        stringResponse.setContent(badContent);

        Assertions.assertThrows(HttpCallException.class, () -> client.checkHttpCode(stringResponse));
    }

    @Test
    void checkHttpCode500() throws Exception {
        StringResponse stringResponse = new StringResponse();
        stringResponse.setCode(500);
        stringResponse.setMessage("a message");
        stringResponse.setContent(badContent);

        Assertions.assertThrows(PluginTechnicalException.class, () -> client.checkHttpCode(stringResponse));
    }

}
