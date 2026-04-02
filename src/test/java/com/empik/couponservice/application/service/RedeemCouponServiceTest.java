package com.empik.couponservice.application.service;

import com.empik.couponservice.application.command.RedeemCouponCommand;
import com.empik.couponservice.application.result.RedeemCouponResult;
import com.empik.couponservice.application.result.RedeemCouponStatus;
import com.empik.couponservice.infrastructure.geo.IpCountryResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedeemCouponServiceTest {

    @Mock
    private IpCountryResolver ipCountryResolver;
    @Mock
    private RedeemCouponTransactionalService redeemCouponTransactionalService;

    @InjectMocks
    private RedeemCouponService redeemCouponService;

    @Test
    void shouldResolveCountryAndDelegateToTransactionalService() {
        RedeemCouponCommand command = new RedeemCouponCommand("CODE", "user1", "1.2.3.4");

        when(ipCountryResolver.resolveCountry("1.2.3.4")).thenReturn("PL");
        when(redeemCouponTransactionalService.redeem(command, "PL"))
            .thenReturn(RedeemCouponResult.redeemed());

        RedeemCouponResult result = redeemCouponService.redeem(command);

        assertThat(result.status()).isEqualTo(RedeemCouponStatus.REDEEMED);
        verify(ipCountryResolver).resolveCountry("1.2.3.4");
        verify(redeemCouponTransactionalService).redeem(command, "PL");
    }
}
