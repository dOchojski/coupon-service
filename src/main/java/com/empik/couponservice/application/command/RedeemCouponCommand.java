package com.empik.couponservice.application.command;

public record RedeemCouponCommand(
        String code,
        String userId,
        String ipAddress
) {
}