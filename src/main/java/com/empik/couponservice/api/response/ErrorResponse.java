package com.empik.couponservice.api.response;

import java.util.Map;

public record ErrorResponse(
    String code,
    String message,
    Map<String, String> validationErrors
) {
    public static ErrorResponse of(String code, String message) {
        return new ErrorResponse(code, message, null);
    }

    public static ErrorResponse of(String code, String message, Map<String, String> validationErrors) {
        return new ErrorResponse(code, message, validationErrors);
    }
}