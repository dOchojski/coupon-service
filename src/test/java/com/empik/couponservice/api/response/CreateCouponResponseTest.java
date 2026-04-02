package com.empik.couponservice.api.response;

import com.empik.couponservice.application.result.CreateCouponResult;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CreateCouponResponseTest {

    @Test
    void shouldMapFromResult() {
        UUID id = UUID.randomUUID();
        CreateCouponResult result = new CreateCouponResult(id, "SUMMER", 100, "PL");

        CreateCouponResponse response = CreateCouponResponse.from(result);

        assertThat(response.id()).isEqualTo(id);
        assertThat(response.code()).isEqualTo("SUMMER");
        assertThat(response.maxUsages()).isEqualTo(100);
        assertThat(response.countryCode()).isEqualTo("PL");
    }
}
