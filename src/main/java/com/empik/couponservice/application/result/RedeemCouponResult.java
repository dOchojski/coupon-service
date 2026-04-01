package com.empik.couponservice.application.result;

public record RedeemCouponResult(
    RedeemCouponStatus status
) {

    public boolean isSuccess() {
        return status == RedeemCouponStatus.REDEEMED;
    }

    public static RedeemCouponResult redeemed() {
        return new RedeemCouponResult(RedeemCouponStatus.REDEEMED);
    }

    public static RedeemCouponResult couponNotFound() {
        return new RedeemCouponResult(RedeemCouponStatus.COUPON_NOT_FOUND);
    }

    public static RedeemCouponResult couponExhausted() {
        return new RedeemCouponResult(RedeemCouponStatus.COUPON_EXHAUSTED);
    }

    public static RedeemCouponResult countryNotAllowed() {
        return new RedeemCouponResult(RedeemCouponStatus.COUNTRY_NOT_ALLOWED);
    }

    public static RedeemCouponResult alreadyRedeemedByUser() {
        return new RedeemCouponResult(RedeemCouponStatus.ALREADY_REDEEMED_BY_USER);
    }
}