package com.payline.payment.swish.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Currency;

public class PluginUtilsTest {


    @Test
    public void createStringAmount() {
        BigInteger int1 = BigInteger.ZERO;
        BigInteger int2 = BigInteger.ONE;
        BigInteger int3 = BigInteger.TEN;
        BigInteger int4 = BigInteger.valueOf(100);
        BigInteger int5 = BigInteger.valueOf(1000);

        Assertions.assertEquals("0.00", PluginUtils.createStringAmount(int1, Currency.getInstance("EUR")));
        Assertions.assertEquals("0.01", PluginUtils.createStringAmount(int2, Currency.getInstance("EUR")));
        Assertions.assertEquals("0.10", PluginUtils.createStringAmount(int3, Currency.getInstance("EUR")));
        Assertions.assertEquals("1.00", PluginUtils.createStringAmount(int4, Currency.getInstance("EUR")));
        Assertions.assertEquals("10.00", PluginUtils.createStringAmount(int5, Currency.getInstance("EUR")));
    }

    @Test
    public void createFloatAmount() {
        BigInteger int1 = BigInteger.ZERO;
        BigInteger int2 = BigInteger.ONE;
        BigInteger int3 = BigInteger.TEN;
        BigInteger int4 = BigInteger.valueOf(100);
        BigInteger int5 = BigInteger.valueOf(1000);

        Assertions.assertEquals(new Float("00.00"), PluginUtils.createFloatAmount(int1, Currency.getInstance("EUR")));
        Assertions.assertEquals(new Float("00.01"), PluginUtils.createFloatAmount(int2, Currency.getInstance("EUR")));
        Assertions.assertEquals(new Float("00.10"), PluginUtils.createFloatAmount(int3, Currency.getInstance("EUR")));
        Assertions.assertEquals(new Float("1.00"), PluginUtils.createFloatAmount(int4, Currency.getInstance("EUR")));
        Assertions.assertEquals(new Float("10.00"), PluginUtils.createFloatAmount(int5, Currency.getInstance("EUR")));
    }


    @Test
    public void createFloatAmountNullCheck() {
        Assertions.assertNull(PluginUtils.createFloatAmount(null, Currency.getInstance("EUR")));
        Assertions.assertNull(PluginUtils.createFloatAmount(BigInteger.ONE, null));
        Assertions.assertNull(PluginUtils.createFloatAmount(null, null));
    }

}
