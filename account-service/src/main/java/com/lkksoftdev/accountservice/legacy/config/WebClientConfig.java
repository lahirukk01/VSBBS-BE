package com.lkksoftdev.accountservice.legacy.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

// Not using this configuration anymore
//@Configuration
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
