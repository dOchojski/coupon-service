package com.empik.couponservice.application.exception;

public class GeoServiceUnavailableException extends RuntimeException {

    public GeoServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}