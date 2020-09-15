package com.payline.payment.swish.utils;

import com.payline.payment.swish.service.impl.ConfigurationServiceImpl;
import com.payline.payment.swish.service.impl.PaymentFormConfigurationServiceImpl;
import com.payline.payment.swish.utils.http.SwishHttpClient;
import com.payline.pmapi.bean.common.Amount;
import com.payline.pmapi.bean.common.Buyer;
import com.payline.pmapi.bean.common.Buyer.Address;
import com.payline.pmapi.bean.configuration.PartnerConfiguration;
import com.payline.pmapi.bean.configuration.request.ContractParametersCheckRequest;
import com.payline.pmapi.bean.notification.request.NotificationRequest;
import com.payline.pmapi.bean.payment.*;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.payment.request.RedirectionPaymentRequest;
import com.payline.pmapi.bean.payment.request.TransactionStatusRequest;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseSuccess;
import com.payline.pmapi.bean.paymentform.request.PaymentFormConfigurationRequest;
import com.payline.pmapi.bean.paymentform.request.PaymentFormLogoRequest;
import com.payline.pmapi.bean.refund.request.RefundRequest;
import com.payline.pmapi.logger.LogManager;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Class with method to generate mock easier
 */
public class TestUtils {
    private static final Logger LOGGER = LogManager.getLogger(TestUtils.class);

    private static final String MDP_IDENTIFIER = "paymentMethodIdentifier";

    // constants used in request creation
    private static final String SOFT_DESCRIPTOR = "softDescriptor";
    public static final String TRANSACTION_ID = "455454545415451198120";

    // constants used as Partner data
    private static final String URL = "https://mss.cpc.getswish.net/swish-cpcapi";

    // constants used as Contract data
    public static final String MERCHANT_ID = "1231181189";

    // constant used as Payment form context
    public static final String PAYER_PHONE = "33628692878";

    private static final Environment ENVIRONMENT = new Environment("https://succesurl.com/", "http://redirectionURL.com", "http://redirectionCancelURL.com", true);
    private static final Locale LOCALE_FR = Locale.FRANCE;
    private static final String CURRENCY = "SEK";

    private static final String TEST_PHONE_NUMBER = "0600000000";
    private static final String TEST_EMAIL = "foo@bar.baz";


    /**
     * Create a ContractParametersCheckRequest with default parameters
     *
     * @return
     */
    public static ContractParametersCheckRequest createContractParametersCheckRequest() {
        return ContractParametersCheckRequest
                .CheckRequestBuilder
                .aCheckRequest()
                .withContractConfiguration(createContractConfiguration())
                .withAccountInfo(createAccountInfo())
                .withEnvironment(ENVIRONMENT)
                .withLocale(LOCALE_FR)
                .withPartnerConfiguration(createPartnerConfiguration())
                .build();
    }


    /**
     * Create a paymentRequest with default parameters.
     *
     * @return paymentRequest created
     */
    public static PaymentRequest createDefaultPaymentRequest() {
        return createCompletePaymentBuilder().build();
    }

    /**
     * create a RedirectionPaymentRequest with defaut parameters
     *
     * @return
     */
    public static RedirectionPaymentRequest createRedirectionPaymentRequest() {
        return RedirectionPaymentRequest.builder().build();

    }

    /**
     * Create a default form context for Unit Test and IT Test
     *
     * @return PaymentFormContext which contain a mobile phone number and a iban
     */
    public static PaymentFormContext createDefaultPaymentFormContext(String phoneNumber) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put(PaymentFormConfigurationServiceImpl.PHONE_KEY, phoneNumber);

        return PaymentFormContext.PaymentFormContextBuilder
                .aPaymentFormContext()
                .withPaymentFormParameter(parameters)
                .withSensitivePaymentFormParameter(null)
                .build();
    }

    /**
     * Create a RefundRequest from a given transaction and with default parameters
     *
     * @param transactionId
     * @return
     */
    public static RefundRequest createRefundRequest(String transactionId, String partnerTransactionId) { // todo avoir un transactioId et un partnerTransactionId ifferent
        return RefundRequest.RefundRequestBuilder.aRefundRequest()
                .withAmount(createAmount())
                .withOrder(createOrder())
                .withBuyer(createDefaultBuyer())
                .withContractConfiguration(createContractConfiguration())
                .withEnvironment(ENVIRONMENT)
                .withTransactionId(transactionId)
                .withPartnerTransactionId(transactionId)
                .withPartnerConfiguration(createPartnerConfiguration())
                .withTransactionAdditionalData(PAYER_PHONE)
                .withSoftDescriptor("adescriptor")
                .build();
    }

    public static RefundRequest createRefundFromPaymentRequest(PaymentRequest request, PaymentResponseSuccess response){
        return RefundRequest.RefundRequestBuilder.aRefundRequest()
                .withAmount(request.getAmount())
                .withSoftDescriptor(request.getSoftDescriptor())
                .withOrder(request.getOrder())
                .withBuyer(request.getBuyer())
                .withContractConfiguration(request.getContractConfiguration())
                .withEnvironment(request.getEnvironment())
                .withTransactionId(request.getTransactionId())
                .withPartnerTransactionId(response.getPartnerTransactionId())
                .withPartnerConfiguration(request.getPartnerConfiguration())
                .withTransactionAdditionalData(response.getTransactionAdditionalData())
                .build();
    }


    /**
     * Create a complete payment request used for Integration Tests
     *
     * @return PaymentRequest.Builder
     */
    public static PaymentRequest.Builder createCompletePaymentBuilder() {
        return PaymentRequest.builder()
                .withAmount(createAmount())
                .withBrowser(new Browser("", LOCALE_FR))
                .withContractConfiguration(createContractConfiguration())
                .withEnvironment(ENVIRONMENT)
                .withOrder(createOrder())
                .withLocale(LOCALE_FR)
                .withTransactionId(TRANSACTION_ID)
                .withSoftDescriptor(SOFT_DESCRIPTOR)
                .withPaymentFormContext(createDefaultPaymentFormContext(PAYER_PHONE))
                .withPartnerConfiguration(createPartnerConfiguration())
                .withLocale(LOCALE_FR)
                .withBuyer(createDefaultBuyer());
    }


    public static PaymentFormConfigurationRequest createDefaultPaymentFormConfigurationRequest() {
        return PaymentFormConfigurationRequest.PaymentFormConfigurationRequestBuilder.aPaymentFormConfigurationRequest()
                .withLocale(LOCALE_FR)
                .withBuyer(createDefaultBuyer())
                .withAmount(new Amount(null, Currency.getInstance("EUR")))
                .withContractConfiguration(createContractConfiguration())
                .withOrder(createOrder())
                .withEnvironment(ENVIRONMENT)
                .withPartnerConfiguration(createPartnerConfiguration())
                .build();
    }

    /**
     * Generate a valid {@link PaymentFormLogoRequest}.
     */
    public static PaymentFormLogoRequest createPaymentFormLogoRequest() {
        return PaymentFormLogoRequest.PaymentFormLogoRequestBuilder.aPaymentFormLogoRequest()
                .withContractConfiguration( createContractConfiguration() )
                .withEnvironment(  ENVIRONMENT )
                .withPartnerConfiguration( createPartnerConfiguration() )
                .withLocale( Locale.getDefault() )
                .build();
    }

    public static TransactionStatusRequest createDefaultTransactionStatusRequest() {
        return TransactionStatusRequest.TransactionStatusRequestBuilder
                .aNotificationRequest()
                .withTransactionId(TRANSACTION_ID)
                .withAmount(createAmount())
                .withContractConfiguration(createContractConfiguration())
                .withEnvironment(ENVIRONMENT)
                .withOrder(createOrder())
                .withBuyer(createDefaultBuyer())
                .withPartnerConfiguration(createPartnerConfiguration())
                .build();
    }

    public static NotificationRequest createNotificationRequest(String body){
        return NotificationRequest.NotificationRequestBuilder
                .aNotificationRequest()
                .withContent(new ByteArrayInputStream(body.getBytes()))
                .withHttpMethod("GET")
                .withPathInfo("foo")
                .withHeaderInfos(new HashMap<>())
                .build();
    }


    ///////////////////////////////////////////////////////////////////////////////////////
    // methods needed to create requests

    public static String createMerchantRequestId() {
        return "131217" + Calendar.getInstance().getTimeInMillis();
    }

    private static Map<Buyer.AddressType, Address> createDefaultAddresses() {
        Address address = createDefaultCompleteAddress();
        return createAddresses(address);
    }

    private static Address createDefaultCompleteAddress() {
        String street1 = "1 rue de la Republique";
        String street2 = "residence " + RandomStringUtils.random(9, true, false);
        return createCompleteAddress(street1, street2, "Marseille", "13015", "FR");
    }

    private static Address createCompleteAddress(String street, String street2, String city, String zip, String country) {
        return Address.AddressBuilder.anAddress()
                .withStreet1(street)
                .withStreet2(street2)
                .withCity(city)
                .withZipCode(zip)
                .withCountry(country)
                .withFullName(createFullName())
                .build();
    }

    private static Map<Buyer.AddressType, Address> createAddresses(Address address) {
        Map<Buyer.AddressType, Address> addresses = new HashMap<>();
        addresses.put(Buyer.AddressType.DELIVERY, address);
        addresses.put(Buyer.AddressType.BILLING, address);

        return addresses;
    }


    private static Amount createAmount() {
        return new Amount(BigInteger.TEN, Currency.getInstance(CURRENCY));
    }

    private static Order createOrder() {
        List<Order.OrderItem> orderItems = new ArrayList<>();
        orderItems.add(createOrderItem("item1", createAmount()));
        orderItems.add(createOrderItem("item2", createAmount()));
        return Order.OrderBuilder.anOrder()
                .withReference("123456789")
                .withAmount(createAmount())
                .withDate(new Date())
                .withItems(orderItems)
                .withDeliveryMode("1")
                .withDeliveryTime("2")
                .withExpectedDeliveryDate(new Date())
                .build();
    }

    private static Order.OrderItem createOrderItem(String reference, Amount amount) {
        return Order.OrderItem.OrderItemBuilder.anOrderItem()
                .withAmount(amount)
                .withQuantity(4L)
                .withCategory("20001")
                .withComment("some label")
                .withBrand("someBrand")
                .withReference(reference)
                .withTaxRatePercentage(BigDecimal.TEN)
                .build();
    }

    private static Order createOrder(String transactionID, Amount amount) {
        return Order.OrderBuilder.anOrder().withReference(transactionID).withAmount(amount).build();
    }

    private static Buyer.FullName createFullName() {
        return new Buyer.FullName(RandomStringUtils.random(7, true, false), RandomStringUtils.random(10, true, false), "4");
    }

    private static Map<Buyer.PhoneNumberType, String> createDefaultPhoneNumbers() {
        Map<Buyer.PhoneNumberType, String> phoneNumbers;
        phoneNumbers = new EnumMap<>(Buyer.PhoneNumberType.class);
        phoneNumbers.put(Buyer.PhoneNumberType.BILLING, TEST_PHONE_NUMBER);
        phoneNumbers.put(Buyer.PhoneNumberType.CELLULAR, TEST_PHONE_NUMBER);
        phoneNumbers.put(Buyer.PhoneNumberType.HOME, TEST_PHONE_NUMBER);
        phoneNumbers.put(Buyer.PhoneNumberType.UNDEFINED, TEST_PHONE_NUMBER);
        phoneNumbers.put(Buyer.PhoneNumberType.WORK, TEST_PHONE_NUMBER);

        return phoneNumbers;
    }

    private static Buyer createBuyer(Map<Buyer.PhoneNumberType, String> phoneNumbers, Map<Buyer.AddressType, Address> addresses, Buyer.FullName fullName) {
        return Buyer.BuyerBuilder.aBuyer()
                .withEmail(TEST_EMAIL)
                .withPhoneNumbers(phoneNumbers)
                .withAddresses(addresses)
                .withFullName(fullName)
                .withCustomerIdentifier("subscriber12")
                .withExtendedData(createDefaultExtendedData())
                .withBirthday(getBirthdayDate())
                .withLegalStatus(Buyer.LegalStatus.PERSON)
                .build();
    }

    private static Date getBirthdayDate() {
        try {
            return new SimpleDateFormat("dd/MM/yyyy").parse("04/05/1981");
        } catch (ParseException e) {
            LOGGER.error("parsing de la date de naissance impossible", e);
            return null;
        }
    }

    private static Map<String, String> createDefaultExtendedData() {
        return new HashMap<String, String>();
    }

    private static Buyer createDefaultBuyer() {
        return createBuyer(createDefaultPhoneNumbers(), createDefaultAddresses(), createFullName());
    }

    private static Map<String, String> createAccountInfo() {
        Map<String, String> accountInfo = new HashMap<>();
        accountInfo.put(ConfigurationServiceImpl.KEY, MERCHANT_ID);

        return accountInfo;
    }

    public static PartnerConfiguration createPartnerConfiguration() {
        Map<String, String> map = new HashMap<>();
        Map<String, String> sensitiveMap = new HashMap<>();
        map.put(SwishHttpClient.URL_KEY, URL);

        return new PartnerConfiguration(map, sensitiveMap);
    }

    private static ContractConfiguration createContractConfiguration() {
        Map<String, ContractProperty> map = new HashMap<>();
        map.put(ConfigurationServiceImpl.KEY, new ContractProperty(MERCHANT_ID));

        return new ContractConfiguration(MDP_IDENTIFIER, map);
    }

}
