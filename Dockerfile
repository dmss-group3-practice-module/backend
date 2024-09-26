# Use an official OpenJDK runtime as a parent image
FROM eclipse-temurin:22-jdk-jammy AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the Gradle wrapper and build scripts into the container
COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle settings.gradle /app/

# Copy the application source code into the container
COPY src /app/src

# Set permissions to execute the Gradle wrapper
RUN chmod +x gradlew

# Build the application
RUN ./gradlew bootJar

# Use a smaller base image for the final stage to run the application
FROM eclipse-temurin:22-jre-jammy

# Set the working directory inside the container
WORKDIR /app

# Copy the jar file from the build stage to the current stage
COPY --from=build /app/build/libs/*.jar app.jar

# Set the entry point to run the Spring Boot application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
