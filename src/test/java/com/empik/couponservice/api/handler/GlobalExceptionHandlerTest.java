package com.empik.couponservice.api.handler;

import com.empik.couponservice.api.response.ErrorResponse;
import com.empik.couponservice.application.exception.CouponAlreadyExistsException;
import com.empik.couponservice.application.exception.GeoServiceUnavailableException;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void shouldHandleCouponAlreadyExists() {
        ErrorResponse response = handler.handleCouponAlreadyExists(
            new CouponAlreadyExistsException("duplicate")
        );

        assertThat(response.code()).isEqualTo("COUPON_ALREADY_EXISTS");
        assertThat(response.message()).isEqualTo("duplicate");
    }

    @Test
    void shouldHandleGeoServiceUnavailable() {
        ErrorResponse response = handler.handleGeoServiceUnavailable(
            new GeoServiceUnavailableException("geo down", null)
        );

        assertThat(response.code()).isEqualTo("GEO_SERVICE_UNAVAILABLE");
        assertThat(response.message()).isEqualTo("geo down");
    }

    @Test
    void shouldHandleValidationErrors() {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "request");
        bindingResult.addError(new FieldError("request", "code", "must not be blank"));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(
            mock(org.springframework.core.MethodParameter.class), bindingResult
        );

        ErrorResponse response = handler.handleValidation(ex);

        assertThat(response.code()).isEqualTo("VALIDATION_ERROR");
        assertThat(response.validationErrors()).containsEntry("code", "must not be blank");
    }

    @Test
    void shouldHandleGenericException() {
        ErrorResponse response = handler.handleGeneric(new RuntimeException("something broke"));

        assertThat(response.code()).isEqualTo("INTERNAL_SERVER_ERROR");
        assertThat(response.message()).isEqualTo("Unexpected error");
    }
}
