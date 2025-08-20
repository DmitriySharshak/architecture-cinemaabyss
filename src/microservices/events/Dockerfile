FROM openjdk:17-jdk-slim
VOLUME /tmp
ARG JAR_FILE=build/libs/events-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} events.jar
ENTRYPOINT ["java", "-jar", "/events.jar"]