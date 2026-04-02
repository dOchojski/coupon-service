package com.empik.couponservice.application.result;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RedeemCouponResultTest {

    @Test
    void redeemedShouldBeSuccess() {
        assertThat(RedeemCouponResult.redeemed().isSuccess()).isTrue();
        assertThat(RedeemCouponResult.redeemed().status()).isEqualTo(RedeemCouponStatus.REDEEMED);
    }

    @Test
    void couponNotFoundShouldNotBeSuccess() {
        assertThat(RedeemCouponResult.couponNotFound().isSuccess()).isFalse();
        assertThat(RedeemCouponResult.couponNotFound().status()).isEqualTo(RedeemCouponStatus.COUPON_NOT_FOUND);
    }

    @Test
    void couponExhaustedShouldNotBeSuccess() {
        assertThat(RedeemCouponResult.couponExhausted().isSuccess()).isFalse();
        assertThat(RedeemCouponResult.couponExhausted().status()).isEqualTo(RedeemCouponStatus.COUPON_EXHAUSTED);
    }

    @Test
    void countryNotAllowedShouldNotBeSuccess() {
        assertThat(RedeemCouponResult.countryNotAllowed().isSuccess()).isFalse();
        assertThat(RedeemCouponResult.countryNotAllowed().status()).isEqualTo(RedeemCouponStatus.COUNTRY_NOT_ALLOWED);
    }

    @Test
    void alreadyRedeemedByUserShouldNotBeSuccess() {
        assertThat(RedeemCouponResult.alreadyRedeemedByUser().isSuccess()).isFalse();
        assertThat(RedeemCouponResult.alreadyRedeemedByUser().status()).isEqualTo(RedeemCouponStatus.ALREADY_REDEEMED_BY_USER);
    }
}
