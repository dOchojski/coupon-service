package com.empik.couponservice;

import com.empik.couponservice.application.command.RedeemCouponCommand;
import com.empik.couponservice.application.result.RedeemCouponResult;
import com.empik.couponservice.application.result.RedeemCouponStatus;
import com.empik.couponservice.application.service.RedeemCouponService;
import com.empik.couponservice.infrastructure.geo.IpCountryResolver;
import com.empik.couponservice.persistence.entity.CouponEntity;
import com.empik.couponservice.persistence.repository.CouponRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class RedeemCouponConcurrencyTest {

    @Autowired
    private RedeemCouponService redeemCouponService;

    @Autowired
    private CouponRepository couponRepository;

    @MockitoBean(name = "cachedIpCountryResolver")
    private IpCountryResolver ipCountryResolver;

    @Test
    void should_not_exceed_max_usages_under_concurrency() throws Exception {
        when(ipCountryResolver.resolveCountry(anyString())).thenReturn("PL");

        String code = "TEST_CONCURRENCY";
        String normalized = code.toUpperCase();

        CouponEntity coupon = new CouponEntity(
            UUID.randomUUID(),
            code,
            normalized,
            Instant.now(),
            10,
            0,
            "PL"
        );

        couponRepository.save(coupon);

        int threads = 50;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        List<Callable<RedeemCouponResult>> tasks = new ArrayList<>();

        for (int i = 0; i < threads; i++) {
            int userIndex = i;

            tasks.add(() -> redeemCouponService.redeem(
                new RedeemCouponCommand(
                    code,
                    "user-" + userIndex,
                    "127.0.0.1"
                )
            ));
        }

        List<Future<RedeemCouponResult>> futures = executor.invokeAll(tasks);

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        List<RedeemCouponStatus> statuses = new ArrayList<>();
        for (Future<RedeemCouponResult> future : futures) {
            statuses.add(future.get().status());
        }

        long successCount = statuses.stream()
            .filter(status -> status == RedeemCouponStatus.REDEEMED)
            .count();

        long exhaustedCount = statuses.stream()
            .filter(status -> status == RedeemCouponStatus.COUPON_EXHAUSTED)
            .count();

        CouponEntity updated = couponRepository.findById(coupon.getId()).orElseThrow();

        assertThat(successCount).isEqualTo(10);
        assertThat(updated.getCurrentUsages()).isEqualTo(10);
        assertThat(successCount + exhaustedCount).isEqualTo(threads);
    }
}