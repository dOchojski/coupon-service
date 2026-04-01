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
@Table(name = "coupon")
public class CouponEntity {

    @Id
    private UUID id;

    @Column(name = "code_original", nullable = false)
    private String codeOriginal;

    @Column(name = "code_normalized", nullable = false, unique = true)
    private String codeNormalized;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "max_usages", nullable = false)
    private int maxUsages;

    @Column(name = "current_usages", nullable = false)
    private int currentUsages;

    @Column(name = "country_code", nullable = false)
    private String countryCode;

    public void incrementUsage() {
        this.currentUsages++;
    }
}