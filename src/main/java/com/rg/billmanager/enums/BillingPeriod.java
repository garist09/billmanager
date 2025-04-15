package com.rg.billmanager.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public enum BillingPeriod {
    TRIAL("-100", "trial"),
    DAILY("-50", "daily"),
    MONTHLY("1", "monthly"),
    QUARTERLY("3", "quarterly"),
    SEMI_ANNUAL("6", "semi-annual"),
    ANNUAL("12", "annual"),
    BIENNIAL("24", "biennial"),
    TRIENNIAL("36", "triennial"),
    ONE_TIME("0", "one-time");

    private final String code;
    private final String label;

    private static final Map<String, BillingPeriod> CODE_MAP = new HashMap<>();

    static {
        for (BillingPeriod period: values()) {
            CODE_MAP.put(period.code, period);
        }
    }

    public static String getLabelByCode(String code) {
        BillingPeriod period = CODE_MAP.get(code);
        return period != null ? period.getLabel() : "unknown";
    }
}
