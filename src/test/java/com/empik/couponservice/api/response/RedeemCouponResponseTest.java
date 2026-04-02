package com.empik.couponservice.api.response;

import com.empik.couponservice.application.result.RedeemCouponResult;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RedeemCouponResponseTest {

    @Test
    void shouldMapRedeemedStatus() {
        RedeemCouponResponse response = RedeemCouponResponse.from(RedeemCouponResult.redeemed());

        assertThat(response.status()).isEqualTo("REDEEMED");
        assertThat(response.success()).isTrue();
        assertThat(response.message()).isEqualTo("Coupon redeemed successfully");
    }

    @Test
    void shouldMapCouponNotFoundStatus() {
        RedeemCouponResponse response = RedeemCouponResponse.from(RedeemCouponResult.couponNotFound());

        assertThat(response.status()).isEqualTo("COUPON_NOT_FOUND");
        assertThat(response.success()).isFalse();
        assertThat(response.message()).isEqualTo("Coupon not found");
    }

    @Test
    void shouldMapCouponExhaustedStatus() {
        RedeemCouponResponse response = RedeemCouponResponse.from(RedeemCouponResult.couponExhausted());

        assertThat(response.status()).isEqualTo("COUPON_EXHAUSTED");
        assertThat(response.success()).isFalse();
        assertThat(response.message()).isEqualTo("Coupon usage limit reached");
    }

    @Test
    void shouldMapCountryNotAllowedStatus() {
        RedeemCouponResponse response = RedeemCouponResponse.from(RedeemCouponResult.countryNotAllowed());

        assertThat(response.status()).isEqualTo("COUNTRY_NOT_ALLOWED");
        assertThat(response.success()).isFalse();
        assertThat(response.message()).isEqualTo("Coupon not valid for this country");
    }

    @Test
    void shouldMapAlreadyRedeemedByUserStatus() {
        RedeemCouponResponse response = RedeemCouponResponse.from(RedeemCouponResult.alreadyRedeemedByUser());

        assertThat(response.status()).isEqualTo("ALREADY_REDEEMED_BY_USER");
        assertThat(response.success()).isFalse();
        assertThat(response.message()).isEqualTo("User already used this coupon");
    }
}
