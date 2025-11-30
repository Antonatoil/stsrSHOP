package com.example.shop.integration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Базовый интеграционный тест.
 * Поднимает PostgreSQL в Testcontainers и прокидывает настройки в Spring Boot.
 *
 * ВАЖНО:
 * - @DirtiesContext(AFTER_CLASS) заставляет Spring пересоздавать контекст
 *   для каждого тестового класса, чтобы URL БД соответствовал порту
 *   именно его контейнера.
 * - Redis и RabbitMQ по-прежнему отключены автоконфигом в @SpringBootTest.
 */
@SpringBootTest(properties = {
        "spring.cache.type=none",
        "spring.autoconfigure.exclude=" +
                "org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration," +
                "org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration," +
                "org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration"
})
@Testcontainers(disabledWithoutDocker = true)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class BaseIntegrationTest {

    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("shop")
            .withUsername("shop")
            .withPassword("shop");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.datasource.driver-class-name",
                () -> "org.postgresql.Driver");
    }

    @Test
    void contextLoads() {
        // простой smoke-тест, что контекст вообще поднимается
    }
}
