package com.empik.couponservice.infrastructure.geo;

import com.empik.couponservice.application.exception.GeoServiceUnavailableException;
import com.empik.couponservice.domain.CountryCodeNormalizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class ExternalIpCountryResolver implements IpCountryResolver {

    private final RestClient restClient;
    private final CountryCodeNormalizer countryCodeNormalizer;
    private final String baseUrl;

    public ExternalIpCountryResolver(
        RestClient restClient,
        CountryCodeNormalizer countryCodeNormalizer,
        @Value("${app.geo.ip.base-url}") String baseUrl
    ) {
        this.restClient = restClient;
        this.countryCodeNormalizer = countryCodeNormalizer;
        this.baseUrl = baseUrl;
    }

    @Override
    public String resolveCountry(String ipAddress) {
        try {
            String response = restClient.get()
                .uri(baseUrl + "/" + ipAddress + "/country/")
                .retrieve()
                .body(String.class);

            if (response == null || response.isBlank()) {
                throw new GeoServiceUnavailableException("Geo service returned empty country code", null);
            }

            return countryCodeNormalizer.normalize(response);
        } catch (RestClientException ex) {
            throw new GeoServiceUnavailableException("Geo service unavailable", ex);
        }
    }
}