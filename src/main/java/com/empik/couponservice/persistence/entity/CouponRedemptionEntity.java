package com.empik.couponservice.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Entity
@Table(name = "coupon_redemption")
public class CouponRedemptionEntity {

    @Id
    private UUID id;

    @Column(name = "coupon_id", nullable = false)
    private UUID couponId;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "redeemed_at", nullable = false)
    private Instant redeemedAt;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "country_code")
    private String countryCode;
}