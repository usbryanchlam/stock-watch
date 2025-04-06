# Use OpenJDK as base image
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/stockwatch-1.0.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
