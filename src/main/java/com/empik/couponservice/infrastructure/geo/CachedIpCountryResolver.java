package com.empik.couponservice.infrastructure.geo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@Primary
public class CachedIpCountryResolver implements IpCountryResolver {

    private static final String KEY_PREFIX = "ip:country:";

    private final StringRedisTemplate redisTemplate;
    private final ExternalIpCountryResolver externalIpCountryResolver;
    private final Duration ttl;

    public CachedIpCountryResolver(
        StringRedisTemplate redisTemplate,
        ExternalIpCountryResolver externalIpCountryResolver,
        @Value("${app.cache.ip-country-ttl-seconds:3600}") long ttlSeconds
    ) {
        this.redisTemplate = redisTemplate;
        this.externalIpCountryResolver = externalIpCountryResolver;
        this.ttl = Duration.ofSeconds(ttlSeconds);
    }

    @Override
    public String resolveCountry(String ipAddress) {
        String key = KEY_PREFIX + ipAddress;

        String cachedCountry = redisTemplate.opsForValue().get(key);
        if (cachedCountry != null && !cachedCountry.isBlank()) {
            return cachedCountry;
        }

        String resolvedCountry = externalIpCountryResolver.resolveCountry(ipAddress);
        redisTemplate.opsForValue().set(key, resolvedCountry, ttl);

        return resolvedCountry;
    }
}