package com.empik.couponservice.persistence.repository;

import com.empik.couponservice.persistence.entity.CouponRedemptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CouponRedemptionRepository extends JpaRepository<CouponRedemptionEntity, UUID> {

    boolean existsByCouponIdAndUserId(UUID couponId, String userId);
}