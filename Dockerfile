# Use official OpenJDK 17 image
FROM openjdk:17-jdk-slim

# Set working directory inside the container
WORKDIR /app

# Copy everything from your local project to the container
COPY . .

# Build the Spring Boot app (using Maven Wrapper if present)
RUN ./mvnw clean package -DskipTests || mvn clean package -DskipTests

# Expose port 8080 for the app
EXPOSE 8080

# Run the built Spring Boot jar
CMD ["java", "-jar", "target/*.jar"]
