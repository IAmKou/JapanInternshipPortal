# Stage 1: Build
FROM ubuntu:latest AS Build

# Update and install necessary packages
RUN apt-get update && apt-get install -y openjdk-17-jdk wget unzip curl

# Set the working directory
WORKDIR /app

# Copy project files into the container
COPY . .

# Make Gradle wrapper executable
RUN chmod +x ./gradlew

# Run Gradle build to create the jar
RUN ./gradlew bootJar --no-daemon

# Stage 2: Run
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Expose application port
EXPOSE 8080

# Copy the built jar from the Build stage
COPY --from=Build /app/build/libs/demo-0.0.1-SNAPSHOT.jar app.jar

# Set entry point to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
