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
USER appuser
COPY --from=build /app/build/libs/*.jar /app/app.jar

EXPOSE 8080

ENV JAVA_OPTS=""
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]