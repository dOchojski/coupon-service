package com.empik.couponservice.domain;

import com.empik.couponservice.persistence.entity.CouponRedemptionEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CouponRedemptionFactory {
    private final Clock clock;

    public CouponRedemptionEntity create(UUID couponId, String userId, String ipAddress, String countryCode) {
        return new CouponRedemptionEntity(
                UUID.randomUUID(),
                couponId,
                userId,
                Instant.now(clock),
                ipAddress,
                countryCode
        );
    }
}