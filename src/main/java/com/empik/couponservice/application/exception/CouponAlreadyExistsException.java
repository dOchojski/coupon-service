package com.empik.couponservice.application.exception;

public class CouponAlreadyExistsException extends RuntimeException {

    public CouponAlreadyExistsException(String message) {
        super(message);
    }
}