package com.empik.couponservice.api.response;

public record RedeemCouponResponse(
    String status,
    boolean success,
    String message
) {
    public static RedeemCouponResponse from(String status) {
        return switch (status) {
            case "REDEEMED" -> new RedeemCouponResponse(status, true, "Coupon redeemed successfully");
            case "COUPON_NOT_FOUND" -> new RedeemCouponResponse(status, false, "Coupon not found");
            case "COUPON_EXHAUSTED" -> new RedeemCouponResponse(status, false, "Coupon usage limit reached");
            case "COUNTRY_NOT_ALLOWED" -> new RedeemCouponResponse(status, false, "Coupon not valid for this country");
            case "ALREADY_REDEEMED_BY_USER" -> new RedeemCouponResponse(status, false, "User already used this coupon");
            default -> new RedeemCouponResponse(status, false, "Unknown status");
        };
    }
}