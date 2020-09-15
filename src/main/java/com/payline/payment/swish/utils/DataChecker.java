package com.payline.payment.swish.utils;


public class DataChecker {

    private DataChecker() {
        // ras.
    }

    /**
     * check if a String is null or empty
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

}
