# Stage 1: Build the application
FROM eclipse-temurin:21 AS builder

# Set working directory
WORKDIR /app

# Copy the Maven wrapper files
COPY .mvn/ .mvn/
COPY mvnw mvnw.cmd pom.xml ./

# Download dependencies and cache them (will be cached by Docker if pom.xml doesn't change)
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw dependency:go-offline -B

# Copy source code after dependencies to leverage build caching
COPY src/ ./src/

# Build the application
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw package -DskipTests -B

# Stage 2: Create runtime image
FROM eclipse-temurin:21-alpine

# Add a non-root user to run the app
RUN addgroup -S javauser && \
    adduser -S -G javauser -u 1001 javauser

# Set working directory
WORKDIR /app

# Copy the built JAR from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Set ownership of the application files to non-root user
RUN chown -R javauser:javauser /app

# Switch to non-root user
USER javauser

# Expose application port
EXPOSE 8000

# Configure JVM options (optimized for containers)
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8000/health || exit 1

# Run the application
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar app.jar" ]
