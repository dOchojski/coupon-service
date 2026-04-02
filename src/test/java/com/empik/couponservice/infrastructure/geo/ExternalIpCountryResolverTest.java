package com.empik.couponservice.infrastructure.geo;

import com.empik.couponservice.application.exception.GeoServiceUnavailableException;
import com.empik.couponservice.domain.CountryCodeNormalizer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExternalIpCountryResolverTest {

    private final CountryCodeNormalizer normalizer = new CountryCodeNormalizer();

    @SuppressWarnings("unchecked")
    @Test
    void shouldResolveCountrySuccessfully() {
        RestClient restClient = mock(RestClient.class);
        var uriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        var headersSpec = mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(restClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri("https://ipapi.co/1.2.3.4/country/")).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(String.class)).thenReturn("pl\n");

        ExternalIpCountryResolver resolver = new ExternalIpCountryResolver(restClient, normalizer, "https://ipapi.co");

        String result = resolver.resolveCountry("1.2.3.4");

        assertThat(result).isEqualTo("PL");
    }

    @SuppressWarnings("unchecked")
    @Test
    void shouldThrowWhenResponseIsNull() {
        RestClient restClient = mock(RestClient.class);
        var uriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        var headersSpec = mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(restClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri("https://ipapi.co/1.2.3.4/country/")).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(String.class)).thenReturn(null);

        ExternalIpCountryResolver resolver = new ExternalIpCountryResolver(restClient, normalizer, "https://ipapi.co");

        assertThatThrownBy(() -> resolver.resolveCountry("1.2.3.4"))
            .isInstanceOf(GeoServiceUnavailableException.class)
            .hasMessageContaining("empty country code");
    }

    @SuppressWarnings("unchecked")
    @Test
    void shouldThrowWhenResponseIsBlank() {
        RestClient restClient = mock(RestClient.class);
        var uriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        var headersSpec = mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(restClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri("https://ipapi.co/1.2.3.4/country/")).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(String.class)).thenReturn("  ");

        ExternalIpCountryResolver resolver = new ExternalIpCountryResolver(restClient, normalizer, "https://ipapi.co");

        assertThatThrownBy(() -> resolver.resolveCountry("1.2.3.4"))
            .isInstanceOf(GeoServiceUnavailableException.class);
    }

    @SuppressWarnings("unchecked")
    @Test
    void shouldThrowWhenRestClientFails() {
        RestClient restClient = mock(RestClient.class);
        var uriSpec = mock(RestClient.RequestHeadersUriSpec.class);

        when(restClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri("https://ipapi.co/1.2.3.4/country/")).thenThrow(new RestClientException("timeout"));

        ExternalIpCountryResolver resolver = new ExternalIpCountryResolver(restClient, normalizer, "https://ipapi.co");

        assertThatThrownBy(() -> resolver.resolveCountry("1.2.3.4"))
            .isInstanceOf(GeoServiceUnavailableException.class)
            .hasMessageContaining("unavailable");
    }
}
