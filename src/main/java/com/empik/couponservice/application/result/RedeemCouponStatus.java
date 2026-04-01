package com.empik.couponservice.application.result;

public enum RedeemCouponStatus {
    REDEEMED,
    COUPON_NOT_FOUND,
    COUPON_EXHAUSTED,
    COUNTRY_NOT_ALLOWED,
    ALREADY_REDEEMED_BY_USER
}