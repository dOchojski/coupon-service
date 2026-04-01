package com.empik.couponservice.api.handler;

import com.empik.couponservice.api.response.ErrorResponse;
import com.empik.couponservice.application.exception.CouponAlreadyExistsException;
import com.empik.couponservice.application.exception.GeoServiceUnavailableException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CouponAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleCouponAlreadyExists(CouponAlreadyExistsException ex) {
        return ErrorResponse.of("COUPON_ALREADY_EXISTS", ex.getMessage());
    }

    @ExceptionHandler(GeoServiceUnavailableException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ErrorResponse handleGeoServiceUnavailable(GeoServiceUnavailableException ex) {
        return ErrorResponse.of("GEO_SERVICE_UNAVAILABLE", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> validationErrors = new LinkedHashMap<>();

        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            validationErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return ErrorResponse.of("VALIDATION_ERROR", "Request validation failed", validationErrors);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGeneric(Exception ex) {
        return ErrorResponse.of("INTERNAL_SERVER_ERROR", "Unexpected error");
    }
}