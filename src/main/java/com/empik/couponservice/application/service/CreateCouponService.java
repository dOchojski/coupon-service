package com.empik.couponservice.application.service;

import com.empik.couponservice.application.command.CreateCouponCommand;
import com.empik.couponservice.application.exception.CouponAlreadyExistsException;
import com.empik.couponservice.application.result.CreateCouponResult;
import com.empik.couponservice.domain.CouponCodeNormalizer;
import com.empik.couponservice.domain.CouponFactory;
import com.empik.couponservice.domain.CountryCodeNormalizer;
import com.empik.couponservice.persistence.entity.CouponEntity;
import com.empik.couponservice.persistence.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateCouponService {

    private final CouponRepository couponRepository;
    private final CouponCodeNormalizer couponCodeNormalizer;
    private final CountryCodeNormalizer countryCodeNormalizer;
    private final CouponFactory couponFactory;

    public CreateCouponResult create(CreateCouponCommand command) {
        String normalizedCode = couponCodeNormalizer.normalize(command.code());
        String normalizedCountryCode = countryCodeNormalizer.normalize(command.countryCode());

        CouponEntity entity = couponFactory.create(
            command.code(),
            normalizedCode,
            command.maxUsages(),
            normalizedCountryCode
        );

        try {
            couponRepository.save(entity);
        } catch (DataIntegrityViolationException e) {
            throw new CouponAlreadyExistsException("Coupon with this code already exists");
        }

        return new CreateCouponResult(
            entity.getId(),
            entity.getCodeOriginal(),
            entity.getMaxUsages(),
            entity.getCountryCode()
        );
    }
}