package com.empik.couponservice.api.response;

import com.empik.couponservice.application.result.RedeemCouponResult;
import com.empik.couponservice.application.result.RedeemCouponStatus;

public record RedeemCouponResponse(
    String status,
    boolean success,
    String message
) {
    public static RedeemCouponResponse from(RedeemCouponResult result) {
        RedeemCouponStatus status = result.status();

        return switch (status) {
            case REDEEMED -> new RedeemCouponResponse(status.name(), true, "Coupon redeemed successfully");
            case COUPON_NOT_FOUND -> new RedeemCouponResponse(status.name(), false, "Coupon not found");
            case COUPON_EXHAUSTED -> new RedeemCouponResponse(status.name(), false, "Coupon usage limit reached");
            case COUNTRY_NOT_ALLOWED -> new RedeemCouponResponse(status.name(), false, "Coupon not valid for this country");
            case ALREADY_REDEEMED_BY_USER -> new RedeemCouponResponse(status.name(), false, "User already used this coupon");
        };
    }
}