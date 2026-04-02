package com.empik.couponservice.infrastructure.geo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CachedIpCountryResolverTest {

    @Mock
    private StringRedisTemplate redisTemplate;
    @Mock
    private ExternalIpCountryResolver externalResolver;
    @Mock
    private ValueOperations<String, String> valueOps;

    private CachedIpCountryResolver cachedResolver;

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        cachedResolver = new CachedIpCountryResolver(redisTemplate, externalResolver, 3600);
    }

    @Test
    void shouldReturnCachedValueWhenPresent() {
        when(valueOps.get("ip:country:1.2.3.4")).thenReturn("PL");

        String result = cachedResolver.resolveCountry("1.2.3.4");

        assertThat(result).isEqualTo("PL");
        verify(externalResolver, never()).resolveCountry(any());
    }

    @Test
    void shouldCallExternalResolverWhenCacheMiss() {
        when(valueOps.get("ip:country:1.2.3.4")).thenReturn(null);
        when(externalResolver.resolveCountry("1.2.3.4")).thenReturn("DE");

        String result = cachedResolver.resolveCountry("1.2.3.4");

        assertThat(result).isEqualTo("DE");
        verify(valueOps).set("ip:country:1.2.3.4", "DE", Duration.ofSeconds(3600));
    }

    @Test
    void shouldCallExternalResolverWhenCachedValueIsBlank() {
        when(valueOps.get("ip:country:1.2.3.4")).thenReturn("  ");
        when(externalResolver.resolveCountry("1.2.3.4")).thenReturn("US");

        String result = cachedResolver.resolveCountry("1.2.3.4");

        assertThat(result).isEqualTo("US");
        verify(valueOps).set("ip:country:1.2.3.4", "US", Duration.ofSeconds(3600));
    }
}
