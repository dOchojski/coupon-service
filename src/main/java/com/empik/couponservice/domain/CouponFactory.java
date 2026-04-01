package com.empik.couponservice.domain;

import com.empik.couponservice.persistence.entity.CouponEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CouponFactory {
    private final Clock clock;

    public CouponEntity create(String codeOriginal, String codeNormalized, int maxUsages, String countryCode) {
        return new CouponEntity(
                UUID.randomUUID(),
                codeOriginal,
                codeNormalized,
                Instant.now(clock),
                maxUsages,
                0,
                countryCode
        );
    }
}