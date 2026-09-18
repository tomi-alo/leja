# Stage 1: build the Spring Boot app with Maven and Java 21
FROM maven:3.9.16-eclipse-temurin-21 AS builder

WORKDIR /app

# Copy Maven configuration first so Docker can cache dependencies
COPY pom.xml mvnw ./
COPY .mvn .mvn

# Allow Linux to run the Maven wrapper
RUN chmod +x mvnw

# Download dependencies
RUN ./mvnw -B dependency:go-offline

# Copy your Java source code
COPY src ./src

# Build a runnable JAR file
RUN ./mvnw -B -DskipTests package

# Stage 2: run only the completed app with Java 21
FROM eclipse-temurin:21-jre AS runtime

WORKDIR /app

# Copy the built JAR from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# The Spring app listens on this port
EXPOSE 8080

# Start the Spring Boot app
ENTRYPOINT ["java", "-jar", "app.jar"]