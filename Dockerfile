# Step 1: Use Maven with JDK 17 to build the app
FROM maven:3.9.4-eclipse-temurin-17 AS builder
WORKDIR /app

# Copy Maven configuration files first for caching
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the rest of the source code
COPY src ./src

# Build the Spring Boot JAR
RUN mvn clean package -DskipTests

# Step 2: Run the app using JDK 17 only (smaller final image)
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copy built JAR from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose port 8080
EXPOSE 8080

# Run the Spring Boot app
ENTRYPOINT ["java", "-jar", "app.jar"]
