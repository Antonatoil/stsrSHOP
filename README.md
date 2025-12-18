
---

# Shop Project - README

## Описание

Проект интернет-магазина, написанный на **Java 21** с использованием **Spring Boot 3**, использующий следующие технологии:

* **PostgreSQL 16** + миграции **Flyway** для работы с БД
* **Redis** для кэширования
* **RabbitMQ** для очередей сообщений
* **Prometheus** + **Grafana** для мониторинга
* **Swagger/OpenAPI** для API документации
* **JWT** + **Spring Security** для авторизации
* **JUnit 5** + **Testcontainers** для тестирования

Проект позволяет развернуть полноценный интернет-магазин с возможностью кэширования, очередями и мониторингом.

---

## Стек технологий

* **Java 21**
* **Spring Boot 3**: spring-boot-starter-web, spring-boot-starter-validation, spring-boot-starter-data-jpa
* **PostgreSQL 16** + **Flyway**
* **Redis** для кэширования
* **RabbitMQ** для очередей
* **Swagger/OpenAPI** для API
* **Spring Security + JWT** для авторизации
* **Maven** для сборки
* **Docker** и **Docker Compose** для контейнеризации
* **JUnit 5** + **Testcontainers** для тестирования
* **Micrometer** + **Prometheus** + **Grafana** для мониторинга

---

## Развертывание

### 1. Подготовка

* Убедитесь, что у вас установлены:

  * **Docker Desktop** с **Docker Compose**
  * **Java Development Kit (JDK) 21**
  * **Maven** для сборки

### 2. Получение исходного кода

1. Клонируйте репозиторий:

   ```bash
   git clone https://github.com/Antonatoil/stsrSHOP.git shop
   ```
2. Перейдите в директорию проекта:

   ```bash
   cd shop
   ```

---

## Запуск проекта

### 1. Первый запуск:

Для запуска всех сервисов:

```bash
docker-compose up -d
docker ps
```

### 2. После изменения кода:

```bash
docker-compose down
mvn clean test
mvn clean package -DskipTests
docker-compose up -d --build
docker ps
```

### 3. Доступные сервисы:

* **Swagger UI**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
* **Grafana**: [http://localhost:3000](http://localhost:3000)
* **Prometheus**: [http://localhost:9090](http://localhost:9090)
* **Actuator**:

  * **Prometheus метрики**: [http://localhost:8080/actuator/prometheus](http://localhost:8080/actuator/prometheus)
  * **Health статус**: [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)

---

## API

### 1. **AuthController** (Доступно всем)

* **POST /api/auth/register** — регистрация
* **POST /api/auth/login** — логин, получение JWT

### 2. **ProductController** (Товары)

* **GET /api/products** — список товаров (для всех)
* **GET /api/products/{id}** — товар по ID (для всех)
* **POST /api/products** — создание товара (только ADMIN)
* **PUT /api/products/{id}** — обновление товара (только ADMIN)
* **DELETE /api/products/{id}** — удаление товара (только ADMIN)

### 3. **CategoryController** (Категории)

* **GET /api/categories** — список категорий (для всех)
* **GET /api/categories/{id}** — категория по ID (для всех)
* **POST /api/categories** — создание категории (только ADMIN)
* **PUT /api/categories/{id}** — обновление категории (только ADMIN)
* **DELETE /api/categories/{id}** — удаление категории (только ADMIN)

### 4. **OrderController** (Заказы)

* **POST /api/orders** — создание заказа
* **GET /api/orders/my** — мои заказы (для авторизованных пользователей)
* **GET /api/orders** — все заказы (только ADMIN)
* **PATCH /api/orders/{id}/status** — изменение статуса заказа (только ADMIN)

---

## Метрики и мониторинг

1. **Prometheus**:

  * Контролирует метрики с эндпоинта `/actuator/prometheus`.
  * На **Grafana** отображаются такие метрики, как:

    * Количество HTTP запросов
    * Среднее время ответа
    * Использование памяти JVM
    * Активные потоки JVM
    * Подключения RabbitMQ

2. **Grafana**:

  * Подключен к **Prometheus**.
  * Для мониторинга производительности настраиваются панели:

    * HTTP Requests by Status
    * Average Response Time
    * JVM Heap Memory Used
    * HTTP Error Requests (4xx, 5xx)
    * и другие.

---

## Нагрузочное тестирование (PowerShell)

### 1. Простая нагрузка на публичный эндпоинт (GET /api/products)

```powershell
$uri = "http://localhost:8080/api/products"
$iterations = 200

for ($i = 1; $i -le $iterations; $i++) {
    Write-Host "Request #$i"
    Invoke-WebRequest -Uri $uri -Method GET -UseBasicParsing | Out-Null
    Start-Sleep -Milliseconds 200
}
```

### 2. Нагрузка с несколькими потоками

```powershell
$threads = 5
$iterationsPerThread = 100

1..$threads | ForEach-Object {
    Start-Job -ScriptBlock {
        param($uri, $iterations)
        for ($i = 1; $i -le $iterations; $i++) {
            Invoke-WebRequest -Uri $uri -Method GET -UseBasicParsing | Out-Null
        }
    } -ArgumentList $uri, $iterationsPerThread
}
```

---

## Завершение работы

```bash
cd D:\бгуир\стср\shop
docker-compose down
```

---

