package com.empik.couponservice.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CouponCodeNormalizerTest {

    private final CouponCodeNormalizer normalizer = new CouponCodeNormalizer();

    @Test
    void shouldConvertToUpperCase() {
        assertThat(normalizer.normalize("summer2024")).isEqualTo("SUMMER2024");
    }

    @Test
    void shouldTrimWhitespace() {
        assertThat(normalizer.normalize("  code  ")).isEqualTo("CODE");
    }

    @Test
    void shouldHandleAlreadyUpperCase() {
        assertThat(normalizer.normalize("UPPER")).isEqualTo("UPPER");
    }

    @Test
    void shouldHandleMixedCase() {
        assertThat(normalizer.normalize("SuMmEr")).isEqualTo("SUMMER");
    }
}
