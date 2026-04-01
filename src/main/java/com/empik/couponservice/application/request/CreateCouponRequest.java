package com.empik.couponservice.application.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record CreateCouponRequest(
        @NotBlank String code,
        @Positive int maxUsages,
        @NotBlank String countryCode
) {}