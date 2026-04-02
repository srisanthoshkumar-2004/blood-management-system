# Build Stage
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Build the application
RUN mvn clean package -DskipTests

# Run Stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# 🔐 Security Hardening: Create a non-root user to run the container
# This prevents potential container breakout vulnerabilities.
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Expose the default Spring Boot port
EXPOSE 8080

# 🚀 Run the application with optimized JVM flags for containers
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
