package com.example.shop.integration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Базовый интеграционный тест.
 * Поднимает PostgreSQL в Testcontainers и
 * подсовывает его Spring Boot через DynamicPropertySource.
 *
 * Redis и RabbitMQ в тестах отключены через spring.autoconfigure.exclude,
 * чтобы тесты не требовали запущенных контейнеров Redis/RabbitMQ.
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
public class BaseIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("shop")
            .withUsername("shop")
            .withPassword("shop");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        // на всякий случай жёстко укажем драйвер
        registry.add("spring.datasource.driver-class-name",
                () -> "org.postgresql.Driver");
    }

    @Test
    void contextLoads() {
        // простой smoke-тест, что контекст вообще поднимается
    }
}
