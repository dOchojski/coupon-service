package com.empik.couponservice.application.service;

import com.empik.couponservice.application.command.RedeemCouponCommand;
import com.empik.couponservice.application.result.RedeemCouponResult;
import com.empik.couponservice.infrastructure.geo.IpCountryResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedeemCouponService {

    private final @Qualifier("cachedIpCountryResolver") IpCountryResolver ipCountryResolver;
    private final RedeemCouponTransactionalService redeemCouponTransactionalService;

    public RedeemCouponResult redeem(RedeemCouponCommand command) {
        String resolvedCountry = ipCountryResolver.resolveCountry(command.ipAddress());

        return redeemCouponTransactionalService.redeem(command, resolvedCountry);
    }
}