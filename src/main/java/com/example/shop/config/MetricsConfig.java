package com.example.shop.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class MetricsConfig {

    @Bean
    public MeterRegistry meterRegistry() {
        log.info("Создание MeterRegistry с commonTags application=shop");
        MeterRegistry registry = new SimpleMeterRegistry();
        registry.config()
                .commonTags("application", "shop");
        return registry;
    }
}
