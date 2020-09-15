package com.payline.payment.swish.utils;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Currency;

public class PluginUtils {


    private PluginUtils() {
        // ras.
    }

    /**
     * Return a string which was converted from cents to euro
     *
     * @param amount
     * @return
     */
    public static String createStringAmount(BigInteger amount, Currency currency) {
        //récupérer le nombre de digits dans currency
        int nbDigits = currency.getDefaultFractionDigits();

        StringBuilder sb = new StringBuilder();
        sb.append(amount);

        for (int i = sb.length(); i < 3; i++) {
            sb.insert(0, "0");
        }

        sb.insert(sb.length() - nbDigits, ".");
        return sb.toString();
    }

    /**
     * Return a Float which was converted from cents to euro
     *
     * @param amount
     * @return
     */
    public static Float createFloatAmount(BigInteger amount, Currency currency) {
        if (amount == null || currency == null) {
            return null;
        }
        return Float.parseFloat(createStringAmount(amount, currency));
    }

    public static String truncate(String value, int length) {
        if (value != null && value.length() > length) {
            value = value.substring(0, length);
        }
        return value;
    }

    /**
     * Convert an InputStream into a String
     * @param stream the InputStream to convert
     * @return the converted String encoded in UTF-8
     */
    public static String inputStreamToString(InputStream stream) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = stream.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        return result.toString(StandardCharsets.UTF_8.name());
    }

}