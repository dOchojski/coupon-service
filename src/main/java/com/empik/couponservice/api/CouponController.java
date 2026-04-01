package com.empik.couponservice.api;

import com.empik.couponservice.api.response.RedeemCouponResponse;
import com.empik.couponservice.application.command.CreateCouponCommand;
import com.empik.couponservice.application.command.RedeemCouponCommand;
import com.empik.couponservice.application.request.CreateCouponRequest;
import com.empik.couponservice.application.request.RedeemCouponRequest;
import com.empik.couponservice.application.result.CreateCouponResult;
import com.empik.couponservice.application.result.RedeemCouponResult;
import com.empik.couponservice.application.service.CreateCouponService;
import com.empik.couponservice.application.service.RedeemCouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CreateCouponService createCouponService;
    private final RedeemCouponService redeemCouponService;

    @PostMapping
    public CreateCouponResult createCoupon(@RequestBody @Valid CreateCouponRequest request) {
        return createCouponService.create(
                new CreateCouponCommand(request.code(), request.maxUsages(), request.countryCode())
        );
    }

    @PostMapping("/redeem")
    public RedeemCouponResponse redeem(@RequestBody @Valid RedeemCouponRequest request) {

        var result = redeemCouponService.redeem(
            new RedeemCouponCommand(
                request.code(),
                request.userId(),
                request.ipAddress()
            )
        );
        return RedeemCouponResponse.from(result.status().name());
    }
}