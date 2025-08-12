# Step 1: Build Spring Boot app using Gradle + JDK 21
FROM gradle:8.9.0-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle clean build -x test

# Step 2: Use a smaller JDK 21 image to run the jar
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
