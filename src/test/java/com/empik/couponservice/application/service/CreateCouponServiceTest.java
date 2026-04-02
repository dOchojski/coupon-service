package com.empik.couponservice.application.service;

import com.empik.couponservice.application.command.CreateCouponCommand;
import com.empik.couponservice.application.exception.CouponAlreadyExistsException;
import com.empik.couponservice.application.result.CreateCouponResult;
import com.empik.couponservice.domain.CouponCodeNormalizer;
import com.empik.couponservice.domain.CouponFactory;
import com.empik.couponservice.domain.CountryCodeNormalizer;
import com.empik.couponservice.persistence.entity.CouponEntity;
import com.empik.couponservice.persistence.repository.CouponRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateCouponServiceTest {

    @Mock
    private CouponRepository couponRepository;
    @Mock
    private CouponCodeNormalizer couponCodeNormalizer;
    @Mock
    private CountryCodeNormalizer countryCodeNormalizer;
    @Mock
    private CouponFactory couponFactory;

    @InjectMocks
    private CreateCouponService createCouponService;

    @Test
    void shouldCreateCouponSuccessfully() {
        UUID id = UUID.randomUUID();
        CouponEntity entity = new CouponEntity(id, "Summer2024", "SUMMER2024", Instant.now(), 100, 0, "PL");

        when(couponCodeNormalizer.normalize("Summer2024")).thenReturn("SUMMER2024");
        when(countryCodeNormalizer.normalize("pl")).thenReturn("PL");
        when(couponFactory.create("Summer2024", "SUMMER2024", 100, "PL")).thenReturn(entity);
        when(couponRepository.save(entity)).thenReturn(entity);

        CreateCouponResult result = createCouponService.create(new CreateCouponCommand("Summer2024", 100, "pl"));

        assertThat(result.id()).isEqualTo(id);
        assertThat(result.code()).isEqualTo("Summer2024");
        assertThat(result.maxUsages()).isEqualTo(100);
        assertThat(result.countryCode()).isEqualTo("PL");

        verify(couponRepository).save(entity);
    }

    @Test
    void shouldThrowWhenDuplicateCouponCode() {
        when(couponCodeNormalizer.normalize("DUP")).thenReturn("DUP");
        when(countryCodeNormalizer.normalize("PL")).thenReturn("PL");
        when(couponFactory.create(any(), any(), anyInt(), any()))
            .thenReturn(new CouponEntity(UUID.randomUUID(), "DUP", "DUP", Instant.now(), 10, 0, "PL"));
        when(couponRepository.save(any())).thenThrow(new DataIntegrityViolationException("duplicate"));

        assertThatThrownBy(() -> createCouponService.create(new CreateCouponCommand("DUP", 10, "PL")))
            .isInstanceOf(CouponAlreadyExistsException.class)
            .hasMessageContaining("already exists");
    }
}
