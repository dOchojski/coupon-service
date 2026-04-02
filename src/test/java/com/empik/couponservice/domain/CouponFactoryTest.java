package com.empik.couponservice.domain;

import com.empik.couponservice.persistence.entity.CouponEntity;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

class CouponFactoryTest {

    private static final Instant FIXED_INSTANT = Instant.parse("2024-01-01T12:00:00Z");
    private final Clock clock = Clock.fixed(FIXED_INSTANT, ZoneOffset.UTC);
    private final CouponFactory factory = new CouponFactory(clock);

    @Test
    void shouldCreateCouponWithCorrectFields() {
        CouponEntity coupon = factory.create("Summer2024", "SUMMER2024", 100, "PL");

        assertThat(coupon.getId()).isNotNull();
        assertThat(coupon.getCodeOriginal()).isEqualTo("Summer2024");
        assertThat(coupon.getCodeNormalized()).isEqualTo("SUMMER2024");
        assertThat(coupon.getMaxUsages()).isEqualTo(100);
        assertThat(coupon.getCurrentUsages()).isZero();
        assertThat(coupon.getCountryCode()).isEqualTo("PL");
        assertThat(coupon.getCreatedAt()).isEqualTo(FIXED_INSTANT);
    }
}
