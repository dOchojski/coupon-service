package com.empik.couponservice.persistence.entity;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CouponEntityTest {

    @Test
    void shouldIncrementUsage() {
        CouponEntity coupon = new CouponEntity(
            UUID.randomUUID(), "CODE", "CODE", Instant.now(), 10, 0, "PL"
        );

        coupon.incrementUsage();

        assertThat(coupon.getCurrentUsages()).isEqualTo(1);
    }

    @Test
    void shouldIncrementUsageMultipleTimes() {
        CouponEntity coupon = new CouponEntity(
            UUID.randomUUID(), "CODE", "CODE", Instant.now(), 10, 5, "PL"
        );

        coupon.incrementUsage();
        coupon.incrementUsage();

        assertThat(coupon.getCurrentUsages()).isEqualTo(7);
    }
}
