# 1. Сборка JAR с помощью Maven
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

# сначала pom.xml, чтобы кэшировались зависимости
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

# теперь весь src
COPY src ./src

# собираем приложение
RUN mvn -q -DskipTests package

# 2. Лёгкий рантайм-образ для запуска
FROM eclipse-temurin:21-jre

WORKDIR /app

# копируем jar из стадии сборки
COPY --from=build /app/target/shop-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/app.jar"]
