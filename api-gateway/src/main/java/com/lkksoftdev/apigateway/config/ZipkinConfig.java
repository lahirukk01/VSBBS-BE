package com.lkksoftdev.apigateway.config;

import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.exporter.zipkin.ZipkinSpanExporter;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;

import io.opentelemetry.semconv.ResourceAttributes;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ZipkinConfig {
    private SdkTracerProvider tracerProvider;

    @Value("${spring.application.name}")
    private String serviceName;

    @Value("${zipkin.tracerEndpoint}")
    private String endpoint;

    @Bean
    public Tracer initTracer() {
        ZipkinSpanExporter exporter = ZipkinSpanExporter.builder()
                .setEndpoint(endpoint)
                .build();

        Resource serviceNameResource = Resource.getDefault().toBuilder()
                .put(ResourceAttributes.SERVICE_NAME, serviceName)
                .build();

        tracerProvider = SdkTracerProvider.builder()
                .addSpanProcessor(SimpleSpanProcessor.create(exporter))
                .setResource(serviceNameResource)
                .build();

        return tracerProvider.get("io.opentelemetry.example.ZipkinExample");
    }

    @PreDestroy
    public void shutdown() {
        if (tracerProvider != null) {
            tracerProvider.shutdown();
        }
    }
}