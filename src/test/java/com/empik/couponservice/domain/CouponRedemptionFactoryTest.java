package com.empik.couponservice.domain;

import com.empik.couponservice.persistence.entity.CouponRedemptionEntity;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CouponRedemptionFactoryTest {

    private static final Instant FIXED_INSTANT = Instant.parse("2024-06-15T10:00:00Z");
    private final Clock clock = Clock.fixed(FIXED_INSTANT, ZoneOffset.UTC);
    private final CouponRedemptionFactory factory = new CouponRedemptionFactory(clock);

    @Test
    void shouldCreateRedemptionWithCorrectFields() {
        UUID couponId = UUID.randomUUID();

        CouponRedemptionEntity redemption = factory.create(couponId, "user-1", "1.2.3.4", "PL");

        assertThat(redemption.getId()).isNotNull();
        assertThat(redemption.getCouponId()).isEqualTo(couponId);
        assertThat(redemption.getUserId()).isEqualTo("user-1");
        assertThat(redemption.getIpAddress()).isEqualTo("1.2.3.4");
        assertThat(redemption.getCountryCode()).isEqualTo("PL");
        assertThat(redemption.getRedeemedAt()).isEqualTo(FIXED_INSTANT);
    }

    @Test
    void shouldCreateRedemptionWithNullUserId() {
        UUID couponId = UUID.randomUUID();

        CouponRedemptionEntity redemption = factory.create(couponId, null, "1.2.3.4", "DE");

        assertThat(redemption.getUserId()).isNull();
    }
}
