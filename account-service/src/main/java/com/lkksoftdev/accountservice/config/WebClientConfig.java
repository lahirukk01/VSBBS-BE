package com.lkksoftdev.accountservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Value("${beneficiary.url}")
    private String beneficiaryUrl;

    @Bean
    public WebClient beneficiaryWebClient() {
        return WebClient.builder()
                .baseUrl(beneficiaryUrl)
                .build();
    }
}
