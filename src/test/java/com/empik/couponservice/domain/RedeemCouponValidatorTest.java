package com.empik.couponservice.domain;

import com.empik.couponservice.application.command.RedeemCouponCommand;
import com.empik.couponservice.application.result.RedeemCouponResult;
import com.empik.couponservice.application.result.RedeemCouponStatus;
import com.empik.couponservice.persistence.entity.CouponEntity;
import com.empik.couponservice.persistence.repository.CouponRedemptionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedeemCouponValidatorTest {

    @Mock
    private CouponRedemptionRepository couponRedemptionRepository;

    @InjectMocks
    private RedeemCouponValidator validator;

    private CouponEntity coupon(String countryCode, int maxUsages, int currentUsages) {
        return new CouponEntity(
            UUID.randomUUID(), "CODE", "CODE", Instant.now(),
            maxUsages, currentUsages, countryCode
        );
    }

    @Test
    void shouldReturnEmptyWhenValid() {
        CouponEntity coupon = coupon("PL", 10, 0);
        RedeemCouponCommand command = new RedeemCouponCommand("CODE", "user1", "1.2.3.4");

        when(couponRedemptionRepository.existsByCouponIdAndUserId(coupon.getId(), "user1"))
            .thenReturn(false);

        Optional<RedeemCouponResult> result = validator.validate(coupon, command, "PL");

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnCountryNotAllowed() {
        CouponEntity coupon = coupon("PL", 10, 0);
        RedeemCouponCommand command = new RedeemCouponCommand("CODE", "user1", "1.2.3.4");

        Optional<RedeemCouponResult> result = validator.validate(coupon, command, "DE");

        assertThat(result).isPresent();
        assertThat(result.get().status()).isEqualTo(RedeemCouponStatus.COUNTRY_NOT_ALLOWED);
    }

    @Test
    void shouldReturnCouponExhausted() {
        CouponEntity coupon = coupon("PL", 5, 5);
        RedeemCouponCommand command = new RedeemCouponCommand("CODE", "user1", "1.2.3.4");

        Optional<RedeemCouponResult> result = validator.validate(coupon, command, "PL");

        assertThat(result).isPresent();
        assertThat(result.get().status()).isEqualTo(RedeemCouponStatus.COUPON_EXHAUSTED);
    }

    @Test
    void shouldReturnAlreadyRedeemedByUser() {
        CouponEntity coupon = coupon("PL", 10, 1);
        RedeemCouponCommand command = new RedeemCouponCommand("CODE", "user1", "1.2.3.4");

        when(couponRedemptionRepository.existsByCouponIdAndUserId(coupon.getId(), "user1"))
            .thenReturn(true);

        Optional<RedeemCouponResult> result = validator.validate(coupon, command, "PL");

        assertThat(result).isPresent();
        assertThat(result.get().status()).isEqualTo(RedeemCouponStatus.ALREADY_REDEEMED_BY_USER);
    }

    @Test
    void shouldSkipUserCheckWhenUserIdIsNull() {
        CouponEntity coupon = coupon("PL", 10, 0);
        RedeemCouponCommand command = new RedeemCouponCommand("CODE", null, "1.2.3.4");

        Optional<RedeemCouponResult> result = validator.validate(coupon, command, "PL");

        assertThat(result).isEmpty();
    }

    @Test
    void shouldSkipUserCheckWhenUserIdIsBlank() {
        CouponEntity coupon = coupon("PL", 10, 0);
        RedeemCouponCommand command = new RedeemCouponCommand("CODE", "  ", "1.2.3.4");

        Optional<RedeemCouponResult> result = validator.validate(coupon, command, "PL");

        assertThat(result).isEmpty();
    }
}
