package com.empik.couponservice.application.service;

import com.empik.couponservice.application.command.RedeemCouponCommand;
import com.empik.couponservice.application.result.RedeemCouponResult;
import com.empik.couponservice.application.result.RedeemCouponStatus;
import com.empik.couponservice.domain.CouponCodeNormalizer;
import com.empik.couponservice.domain.CouponRedemptionFactory;
import com.empik.couponservice.domain.RedeemCouponValidator;
import com.empik.couponservice.persistence.entity.CouponEntity;
import com.empik.couponservice.persistence.entity.CouponRedemptionEntity;
import com.empik.couponservice.persistence.repository.CouponRedemptionRepository;
import com.empik.couponservice.persistence.repository.CouponRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedeemCouponTransactionalServiceTest {

    @Mock
    private CouponRepository couponRepository;
    @Mock
    private CouponRedemptionRepository couponRedemptionRepository;
    @Mock
    private CouponCodeNormalizer couponCodeNormalizer;
    @Mock
    private RedeemCouponValidator redeemCouponValidator;
    @Mock
    private CouponRedemptionFactory couponRedemptionFactory;

    @InjectMocks
    private RedeemCouponTransactionalService service;

    private CouponEntity coupon() {
        return new CouponEntity(
            UUID.randomUUID(), "CODE", "CODE", Instant.now(), 10, 0, "PL"
        );
    }

    @Test
    void shouldReturnCouponNotFoundWhenCouponDoesNotExist() {
        when(couponCodeNormalizer.normalize("code")).thenReturn("CODE");
        when(couponRepository.findWithLockByCodeNormalized("CODE")).thenReturn(Optional.empty());

        RedeemCouponCommand command = new RedeemCouponCommand("code", "user1", "1.2.3.4");
        RedeemCouponResult result = service.redeem(command, "PL");

        assertThat(result.status()).isEqualTo(RedeemCouponStatus.COUPON_NOT_FOUND);
        verify(couponRedemptionRepository, never()).save(any());
    }

    @Test
    void shouldReturnValidationResultWhenValidationFails() {
        CouponEntity coupon = coupon();
        when(couponCodeNormalizer.normalize("code")).thenReturn("CODE");
        when(couponRepository.findWithLockByCodeNormalized("CODE")).thenReturn(Optional.of(coupon));

        RedeemCouponCommand command = new RedeemCouponCommand("code", "user1", "1.2.3.4");
        when(redeemCouponValidator.validate(coupon, command, "DE"))
            .thenReturn(Optional.of(RedeemCouponResult.countryNotAllowed()));

        RedeemCouponResult result = service.redeem(command, "DE");

        assertThat(result.status()).isEqualTo(RedeemCouponStatus.COUNTRY_NOT_ALLOWED);
        verify(couponRedemptionRepository, never()).save(any());
    }

    @Test
    void shouldRedeemSuccessfully() {
        CouponEntity coupon = coupon();
        UUID couponId = coupon.getId();
        when(couponCodeNormalizer.normalize("code")).thenReturn("CODE");
        when(couponRepository.findWithLockByCodeNormalized("CODE")).thenReturn(Optional.of(coupon));

        RedeemCouponCommand command = new RedeemCouponCommand("code", "user1", "1.2.3.4");
        when(redeemCouponValidator.validate(coupon, command, "PL")).thenReturn(Optional.empty());

        CouponRedemptionEntity redemption = new CouponRedemptionEntity(
            UUID.randomUUID(), couponId, "user1", Instant.now(), "1.2.3.4", "PL"
        );
        when(couponRedemptionFactory.create(couponId, "user1", "1.2.3.4", "PL")).thenReturn(redemption);

        RedeemCouponResult result = service.redeem(command, "PL");

        assertThat(result.status()).isEqualTo(RedeemCouponStatus.REDEEMED);
        assertThat(coupon.getCurrentUsages()).isEqualTo(1);
        verify(couponRedemptionRepository).save(redemption);
    }
}
