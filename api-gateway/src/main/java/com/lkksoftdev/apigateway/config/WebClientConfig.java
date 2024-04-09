package com.lkksoftdev.apigateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Value("${services.registration.urls.introspect}")
    private String introspectUrl;

    @Bean
    public WebClient introspectWebClient() {
        return WebClient.builder()
                .baseUrl(introspectUrl)
                .build();
    }
}
