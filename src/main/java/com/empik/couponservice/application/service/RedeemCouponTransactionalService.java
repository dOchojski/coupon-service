package com.empik.couponservice.application.service;

import com.empik.couponservice.application.command.RedeemCouponCommand;
import com.empik.couponservice.application.result.RedeemCouponResult;
import com.empik.couponservice.domain.CouponCodeNormalizer;
import com.empik.couponservice.domain.CouponRedemptionFactory;
import com.empik.couponservice.domain.RedeemCouponValidator;
import com.empik.couponservice.persistence.entity.CouponEntity;
import com.empik.couponservice.persistence.entity.CouponRedemptionEntity;
import com.empik.couponservice.persistence.repository.CouponRedemptionRepository;
import com.empik.couponservice.persistence.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RedeemCouponTransactionalService {

    private final CouponRepository couponRepository;
    private final CouponRedemptionRepository couponRedemptionRepository;
    private final CouponCodeNormalizer couponCodeNormalizer;
    private final RedeemCouponValidator redeemCouponValidator;
    private final CouponRedemptionFactory couponRedemptionFactory;

    @Transactional
    public RedeemCouponResult redeem(RedeemCouponCommand command, String resolvedCountry) {
        String normalizedCode = couponCodeNormalizer.normalize(command.code());

        CouponEntity coupon = couponRepository.findWithLockByCodeNormalized(normalizedCode)
            .orElse(null);

        if (coupon == null) {
            return RedeemCouponResult.couponNotFound();
        }

        var validationResult = redeemCouponValidator.validate(coupon, command, resolvedCountry);
        if (validationResult.isPresent()) {
            return validationResult.get();
        }

        CouponRedemptionEntity redemption = couponRedemptionFactory.create(
            coupon.getId(),
            command.userId(),
            command.ipAddress(),
            resolvedCountry
        );

        couponRedemptionRepository.save(redemption);
        coupon.incrementUsage();

        return RedeemCouponResult.redeemed();
    }
}