# Build stage
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

COPY gradlew .
COPY gradle/ gradle/
COPY build.gradle* settings.gradle* ./
COPY src/ src/

RUN chmod +x gradlew

RUN ./gradlew clean bootJar -x test

# Run stage
FROM eclipse-temurin:17-jre

WORKDIR /app

RUN useradd -ms /bin/bash appuser

COPY app.jar /app/app.jar
RUN chown -R appuser:appuser /app

USER appuser
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
