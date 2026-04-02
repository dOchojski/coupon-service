package com.empik.couponservice.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CountryCodeNormalizerTest {

    private final CountryCodeNormalizer normalizer = new CountryCodeNormalizer();

    @Test
    void shouldConvertToUpperCase() {
        assertThat(normalizer.normalize("pl")).isEqualTo("PL");
    }

    @Test
    void shouldTrimWhitespace() {
        assertThat(normalizer.normalize("  de  ")).isEqualTo("DE");
    }

    @Test
    void shouldHandleAlreadyUpperCase() {
        assertThat(normalizer.normalize("US")).isEqualTo("US");
    }

    @Test
    void shouldHandleMixedCase() {
        assertThat(normalizer.normalize("pL")).isEqualTo("PL");
    }
}
