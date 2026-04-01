package com.empik.couponservice.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .requestFactory(new SimpleClientHttpRequestFactory() {{
                    setConnectTimeout(2000);
                    setReadTimeout(2000);
                }})
                .build();
    }
}