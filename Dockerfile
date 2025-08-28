# 1. Стадия сборки
FROM maven:3.9.2-eclipse-temurin-17 AS build

WORKDIR /app

# Копируем pom.xml и скачиваем зависимости
COPY pom.xml .
RUN mvn dependency:go-offline

# Копируем весь проект
COPY src ./src

# Собираем jar
RUN mvn clean package -DskipTests

# 2. Стадия запуска
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Копируем jar из стадии сборки
COPY --from=build /app/target/bankcards-0.0.1-SNAPSHOT.jar app.jar

# Запуск приложения
ENTRYPOINT ["java", "-jar", "app.jar"]
