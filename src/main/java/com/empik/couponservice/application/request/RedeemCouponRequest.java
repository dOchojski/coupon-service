package com.empik.couponservice.application.request;

import jakarta.validation.constraints.NotBlank;

public record RedeemCouponRequest(
    @NotBlank String code,
    String userId,
    @NotBlank String ipAddress
) {
}