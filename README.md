# Shop – Интернет-магазин (Spring Boot)

Учебный проект интернет-магазина на стеке:

- Java 21
- Spring Boot 3
    - spring-boot-starter-web
    - spring-boot-starter-validation
    - spring-boot-starter-data-jpa
    - spring-boot-starter-security
- PostgreSQL 16 + Flyway
- Swagger / OpenAPI (springdoc-openapi)
- Безопасность: Spring Security + JWT (com.auth0:java-jwt)
- Сборка: Maven
- Контейнеризация: Docker + Docker Compose
- Тесты: JUnit 5 + Testcontainers (Postgres)
- Redis (@Cacheable)
- MapStruct (маппинг DTO)
- Micrometer + Prometheus + Grafana
- RabbitMQ (очередь / асинхронка)

## Структура

Основные пакеты:

- `config` – конфигурация безопасности, OpenAPI, Redis, Cache, RabbitMQ, Metrics
- `entity` – сущности JPA (User, Product, Category, Order, OrderItem, enums)
- `repository` – Spring Data JPA репозитории
- `dto` – DTO для auth/product/order
- `mapper` – MapStruct-мапперы
- `service` – сервисный слой + реализации
- `security` – JWT, UserDetails, фильтр
- `controller` – REST-контроллеры (auth, products, orders)
- `exception` – кастомные исключения и глобальный обработчик
- `util` – утилиты (пагинация)

## Локальный запуск (dev, без Docker для БД)

1. Убедиться, что локально поднят PostgreSQL 16, Redis, RabbitMQ **или** использовать Docker Compose (см. ниже).
2. Собрать проект:

```bash
mvn clean verify
