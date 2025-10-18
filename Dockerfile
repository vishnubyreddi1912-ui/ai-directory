# Step 1: Build with Maven + JDK 21
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app

# Copy only pom.xml first for dependency caching
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the Spring Boot JAR
RUN mvn clean package -DskipTests

# Step 2: Run the JAR using lightweight JDK 21 image
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

# Copy JAR from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose port 8080
EXPOSE 8080

# Run Spring Boot app
ENTRYPOINT ["java", "-jar", "app.jar"]
