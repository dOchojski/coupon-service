package com.empik.couponservice.domain;

import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class CountryCodeNormalizer {

    public String normalize(String countryCode) {
        return countryCode.trim().toUpperCase(Locale.ROOT);
    }
}
