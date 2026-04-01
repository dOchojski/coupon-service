package com.empik.couponservice.application.result;

import java.util.UUID;

public record CreateCouponResult(
        UUID id,
        String code,
        int maxUsages,
        String countryCode
) {
}