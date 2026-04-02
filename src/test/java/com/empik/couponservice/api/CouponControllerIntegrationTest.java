package com.empik.couponservice.api;

import com.empik.couponservice.TestcontainersConfiguration;
import com.empik.couponservice.infrastructure.geo.IpCountryResolver;
import com.empik.couponservice.persistence.entity.CouponEntity;
import com.empik.couponservice.persistence.repository.CouponRedemptionRepository;
import com.empik.couponservice.persistence.repository.CouponRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@AutoConfigureMockMvc
class CouponControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private CouponRedemptionRepository couponRedemptionRepository;

    @MockitoBean(name = "cachedIpCountryResolver")
    private IpCountryResolver ipCountryResolver;

    @BeforeEach
    void setUp() {
        couponRedemptionRepository.deleteAll();
        couponRepository.deleteAll();
        when(ipCountryResolver.resolveCountry(anyString())).thenReturn("PL");
    }

    @Test
    void createCoupon_shouldReturn201WithCouponData() throws Exception {
        mockMvc.perform(post("/api/coupons")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"code": "SUMMER2024", "maxUsages": 100, "countryCode": "PL"}
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.code").value("SUMMER2024"))
            .andExpect(jsonPath("$.maxUsages").value(100))
            .andExpect(jsonPath("$.countryCode").value("PL"))
            .andExpect(jsonPath("$.id").isNotEmpty());
    }

    @Test
    void createCoupon_shouldReturn409WhenDuplicateCode() throws Exception {
        couponRepository.save(new CouponEntity(
            UUID.randomUUID(), "SUMMER2024", "SUMMER2024", Instant.now(), 100, 0, "PL"
        ));

        mockMvc.perform(post("/api/coupons")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"code": "summer2024", "maxUsages": 50, "countryCode": "PL"}
                    """))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value("COUPON_ALREADY_EXISTS"));
    }

    @Test
    void createCoupon_shouldReturn400WhenCodeIsBlank() throws Exception {
        mockMvc.perform(post("/api/coupons")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"code": "", "maxUsages": 100, "countryCode": "PL"}
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
            .andExpect(jsonPath("$.validationErrors.code").isNotEmpty());
    }

    @Test
    void createCoupon_shouldReturn400WhenMaxUsagesIsZero() throws Exception {
        mockMvc.perform(post("/api/coupons")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"code": "VALID", "maxUsages": 0, "countryCode": "PL"}
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
            .andExpect(jsonPath("$.validationErrors.maxUsages").isNotEmpty());
    }

    @Test
    void createCoupon_shouldReturn400WhenCountryCodeInvalid() throws Exception {
        mockMvc.perform(post("/api/coupons")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"code": "VALID", "maxUsages": 10, "countryCode": "POL"}
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
            .andExpect(jsonPath("$.validationErrors.countryCode").isNotEmpty());
    }

    @Test
    void createCoupon_shouldReturn400WhenCountryCodeIsNumeric() throws Exception {
        mockMvc.perform(post("/api/coupons")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"code": "VALID", "maxUsages": 10, "countryCode": "12"}
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void redeemCoupon_shouldReturn200WithRedeemedStatus() throws Exception {
        couponRepository.save(new CouponEntity(
            UUID.randomUUID(), "REDEEM_ME", "REDEEM_ME", Instant.now(), 10, 0, "PL"
        ));

        mockMvc.perform(post("/api/coupons/redeem")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"code": "redeem_me", "userId": "user1", "ipAddress": "1.2.3.4"}
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("REDEEMED"))
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Coupon redeemed successfully"));
    }

    @Test
    void redeemCoupon_shouldReturnCouponNotFound() throws Exception {
        mockMvc.perform(post("/api/coupons/redeem")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"code": "NONEXISTENT", "userId": "user1", "ipAddress": "1.2.3.4"}
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("COUPON_NOT_FOUND"))
            .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void redeemCoupon_shouldReturnCouponExhausted() throws Exception {
        couponRepository.save(new CouponEntity(
            UUID.randomUUID(), "EXHAUSTED", "EXHAUSTED", Instant.now(), 1, 1, "PL"
        ));

        mockMvc.perform(post("/api/coupons/redeem")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"code": "EXHAUSTED", "userId": "user1", "ipAddress": "1.2.3.4"}
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("COUPON_EXHAUSTED"))
            .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void redeemCoupon_shouldReturnCountryNotAllowed() throws Exception {
        when(ipCountryResolver.resolveCountry(anyString())).thenReturn("DE");

        couponRepository.save(new CouponEntity(
            UUID.randomUUID(), "PL_ONLY", "PL_ONLY", Instant.now(), 10, 0, "PL"
        ));

        mockMvc.perform(post("/api/coupons/redeem")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"code": "PL_ONLY", "userId": "user1", "ipAddress": "1.2.3.4"}
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("COUNTRY_NOT_ALLOWED"))
            .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void redeemCoupon_shouldReturnAlreadyRedeemedByUser() throws Exception {
        couponRepository.save(new CouponEntity(
            UUID.randomUUID(), "ONCE", "ONCE", Instant.now(), 10, 0, "PL"
        ));

        mockMvc.perform(post("/api/coupons/redeem")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"code": "ONCE", "userId": "same-user", "ipAddress": "1.2.3.4"}
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("REDEEMED"));

        mockMvc.perform(post("/api/coupons/redeem")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"code": "ONCE", "userId": "same-user", "ipAddress": "1.2.3.4"}
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("ALREADY_REDEEMED_BY_USER"))
            .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void redeemCoupon_shouldReturn400WhenCodeIsBlank() throws Exception {
        mockMvc.perform(post("/api/coupons/redeem")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"code": "", "userId": "user1", "ipAddress": "1.2.3.4"}
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void redeemCoupon_shouldReturn400WhenIpAddressIsBlank() throws Exception {
        mockMvc.perform(post("/api/coupons/redeem")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"code": "VALID", "userId": "user1", "ipAddress": ""}
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void redeemCoupon_shouldSucceedWithoutUserId() throws Exception {
        couponRepository.save(new CouponEntity(
            UUID.randomUUID(), "NO_USER", "NO_USER", Instant.now(), 10, 0, "PL"
        ));

        mockMvc.perform(post("/api/coupons/redeem")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"code": "NO_USER", "ipAddress": "1.2.3.4"}
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("REDEEMED"))
            .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void redeemCoupon_shouldHandleGeoServiceUnavailable() throws Exception {
        when(ipCountryResolver.resolveCountry(anyString()))
            .thenThrow(new com.empik.couponservice.application.exception.GeoServiceUnavailableException("Geo down", null));

        mockMvc.perform(post("/api/coupons/redeem")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"code": "SOME_CODE", "userId": "user1", "ipAddress": "1.2.3.4"}
                    """))
            .andExpect(status().isServiceUnavailable())
            .andExpect(jsonPath("$.code").value("GEO_SERVICE_UNAVAILABLE"));
    }
}
