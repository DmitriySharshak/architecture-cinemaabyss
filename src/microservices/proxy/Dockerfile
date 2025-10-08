# Build stage
FROM openjdk:17-jdk-slim AS builder
WORKDIR /app
# Копируем только файлы, влияющие на зависимости, для кеширования слоёв
COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle settings.gradle ./
# Копируем исходники
COPY src ./src
# Сборка без тестов
RUN chmod +x ./gradlew && ./gradlew bootJar -x test


FROM openjdk:17-jdk-slim
VOLUME /tmp
WORKDIR /
# Копируем собранный fat-jar
COPY --from=builder /app/build/libs/*.jar /proxy-service.jar
ENTRYPOINT ["java", "-jar", "/proxy-service.jar"]