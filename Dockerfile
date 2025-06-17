FROM openjdk:17-jdk-slim AS build

WORKDIR /app

# Copy the Maven wrapper and project definition file to cache dependencies.
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline -B

# Copy the rest of the application's source code and build it.
COPY src ./src
RUN ./mvnw package -DskipTests -B


# --- Stage 2: Create the final, secure, and optimized production image ---
# Using a smaller, more secure base image for the final product.
FROM eclipse-temurin:17-alpine

# Create a dedicated, non-root user for enhanced security.
RUN addgroup -g 1000 appgroup && \
    adduser -u 1000 -G appgroup -s /bin/sh -D appuser

WORKDIR /app

# Copy the built JAR from the 'build' stage.
# --- IMPORTANT ---
# Check your 'target' directory to ensure 'user-service-*.jar' matches the actual JAR file name.
COPY --from=build /app/target/user-service-*.jar user-service.jar

# Assign ownership of the application file to our new non-root user.
RUN chown appuser:appgroup user-service.jar

# Switch the container to run as the non-root user.
USER appuser

# Expose the port defined in your application.yml.
EXPOSE 8082

# Set JVM options for better performance inside a container.
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

# Set the command to run the application.
# The environment variables for datasource, kafka, and eureka will be set
# in your docker-compose.yml file, which is the best practice.
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar user-service.jar"]