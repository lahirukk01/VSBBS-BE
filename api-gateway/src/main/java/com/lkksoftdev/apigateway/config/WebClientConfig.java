package com.lkksoftdev.apigateway.config;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Value("${services.registration.urls.introspect}")
    private String introspectUrl;

    private final Tracer tracer;

    public WebClientConfig(Tracer tracer) {
        this.tracer = tracer;
    }

    @Bean
    public WebClient introspectWebClient() {
        return WebClient.builder()
                .baseUrl(introspectUrl)
                .filter((request, next) -> {
                    Span span = tracer.spanBuilder(request.url().toString()).startSpan();
                    try (Scope ignored = span.makeCurrent()) {
                        return next.exchange(request).doOnEach(signal -> span.end());
                    }
                })
                .build();
    }
}
