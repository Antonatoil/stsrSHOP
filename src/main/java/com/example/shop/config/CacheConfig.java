package com.example.shop.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@Slf4j
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        log.info("Инициализация Redis CacheManager");
        return RedisCacheManager.builder(connectionFactory).build();
    }

    @Bean
    public SimpleKeyGenerator keyGenerator() {
        log.debug("Создание SimpleKeyGenerator для генерации ключей кэша");
        return new SimpleKeyGenerator();
    }
}
