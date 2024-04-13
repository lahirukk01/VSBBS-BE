package com.lkksoftdev.registrationservice.config;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsClientConfig {
    @Bean
    public AmazonSNS buildSnsClient() {
        return AmazonSNSClientBuilder.defaultClient();
    }

    @Bean
    AmazonSimpleEmailService buildSesClient() {
        return AmazonSimpleEmailServiceClientBuilder.standard().build();
    }
}
