package com.payline.payment.swish.service.impl;

import com.payline.pmapi.service.TransactionManagerService;

import java.util.HashMap;
import java.util.Map;

public class TransactionManagerServiceImpl implements TransactionManagerService {
    private static final String BUYER_PHONE = "Buyer's phone";

    @Override
    public Map<String, String> readAdditionalData(String s, String s1) {
        Map<String, String> additionalData = new HashMap<>();
        additionalData.put(BUYER_PHONE, s);

        return  additionalData;
    }
}
