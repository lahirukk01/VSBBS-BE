package com.lkksoftdev.accountservice.config;

import com.lkksoftdev.accountservice.auth.AuthTokenValidationFilter;
import com.lkksoftdev.accountservice.feign.IntrospectClient;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Moving all authentication logic to api gateway
//@Configuration
public class FilterConfig {
    private final IntrospectClient introspectClient;

    public FilterConfig(IntrospectClient introspectClient) {
        this.introspectClient = introspectClient;
    }

    @Bean
    public FilterRegistrationBean<AuthTokenValidationFilter> jwtRequestFilter() {
        FilterRegistrationBean<AuthTokenValidationFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(new AuthTokenValidationFilter(introspectClient));
        registrationBean.addUrlPatterns("/account/*");

        return registrationBean;
    }
}
