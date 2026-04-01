package com.empik.couponservice.domain;

import com.empik.couponservice.application.command.RedeemCouponCommand;
import com.empik.couponservice.application.result.RedeemCouponResult;
import com.empik.couponservice.persistence.entity.CouponEntity;
import com.empik.couponservice.persistence.repository.CouponRedemptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RedeemCouponValidator {

    private final CouponRedemptionRepository couponRedemptionRepository;

    public Optional<RedeemCouponResult> validate(CouponEntity coupon, RedeemCouponCommand command, String countryCode) {

        if (!Objects.equals(coupon.getCountryCode(), countryCode)) {
            return Optional.of(RedeemCouponResult.countryNotAllowed());
        }

        if (coupon.getCurrentUsages() >= coupon.getMaxUsages()) {
            return Optional.of(RedeemCouponResult.couponExhausted());
        }

        if (command.userId() != null && !command.userId().isBlank()) {
            boolean alreadyRedeemed = couponRedemptionRepository.existsByCouponIdAndUserId(
                    coupon.getId(),
                    command.userId()
            );

            if (alreadyRedeemed) {
                return Optional.of(RedeemCouponResult.alreadyRedeemedByUser());
            }
        }

        return Optional.empty();
    }
}