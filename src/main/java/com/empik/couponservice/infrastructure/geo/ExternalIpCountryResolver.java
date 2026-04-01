package com.empik.couponservice.infrastructure.geo;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class ExternalIpCountryResolver implements IpCountryResolver {

    private final RestClient restClient;

    @Value("${app.geo.ip.base-url}")
    private String baseUrl;

    @Override
    public String resolveCountry(String ipAddress) {
        return restClient.get()
                .uri(baseUrl + "/" + ipAddress + "/country/")
                .retrieve()
                .body(String.class);
    }
}