FROM openjdk:17-jdk-slim
VOLUME /tmp
ARG JAR_FILE=build/libs/proxy-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} proxy-service.jar
ENTRYPOINT ["java", "-jar", "/proxy-service.jar"]