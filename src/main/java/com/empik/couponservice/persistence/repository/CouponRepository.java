package com.empik.couponservice.persistence.repository;

import com.empik.couponservice.persistence.entity.CouponEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface CouponRepository extends JpaRepository<CouponEntity, UUID> {

    Optional<CouponEntity> findByCodeNormalized(String codeNormalized);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM CouponEntity c WHERE c.codeNormalized = :codeNormalized")
    Optional<CouponEntity> findWithLockByCodeNormalized(String codeNormalized);
}