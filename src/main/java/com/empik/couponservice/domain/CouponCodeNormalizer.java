package com.empik.couponservice.domain;

import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class CouponCodeNormalizer {

    public String normalize(String rawCode) {
        return rawCode.trim().toUpperCase(Locale.ROOT);
    }
}