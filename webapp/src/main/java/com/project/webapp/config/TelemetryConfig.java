package com.project.webapp.config;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.trace.Tracer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenTelemetry configuration for custom telemetry
 * Works alongside the Application Insights Java Agent
 */
@Configuration
public class TelemetryConfig {

    public static final String INSTRUMENTATION_NAME = "webapp-telemetry";
    public static final String INSTRUMENTATION_VERSION = "1.0.0";

    @Bean
    public OpenTelemetry openTelemetry() {
        return GlobalOpenTelemetry.get();
    }

    @Bean
    public Tracer tracer(OpenTelemetry openTelemetry) {
        return openTelemetry.getTracer(INSTRUMENTATION_NAME, INSTRUMENTATION_VERSION);
    }

    @Bean
    public Meter meter(OpenTelemetry openTelemetry) {
        return openTelemetry.meterBuilder(INSTRUMENTATION_NAME)
                .setInstrumentationVersion(INSTRUMENTATION_VERSION)
                .build();
    }
}