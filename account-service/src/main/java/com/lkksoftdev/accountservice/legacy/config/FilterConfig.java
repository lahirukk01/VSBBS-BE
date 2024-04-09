package com.lkksoftdev.accountservice.legacy.config;

import com.lkksoftdev.accountservice.legacy.auth.AuthTokenValidationFilter;
import com.lkksoftdev.accountservice.legacy.IntrospectClient;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

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
