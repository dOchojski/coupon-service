package com.empik.couponservice.application.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public record CreateCouponRequest(
    @NotBlank String code,
    @Positive int maxUsages,
    @NotBlank
    @Pattern(regexp = "^[A-Za-z]{2}$", message = "countryCode must contain exactly 2 letters")
    String countryCode
) {
}