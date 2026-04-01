package com.empik.couponservice.api.response;

import com.empik.couponservice.application.result.CreateCouponResult;

import java.util.UUID;

public record CreateCouponResponse(
    UUID id,
    String code,
    int maxUsages,
    String countryCode
) {
    public static CreateCouponResponse from(CreateCouponResult result) {
        return new CreateCouponResponse(
            result.id(),
            result.code(),
            result.maxUsages(),
            result.countryCode()
        );
    }
}