FROM gradle:8-jdk17 AS build
WORKDIR /workspace
COPY gradle gradle
COPY gradlew settings.gradle build.gradle gradle.properties ./
COPY common common
COPY contract-service contract-service
COPY report-service report-service
COPY gateway gateway
USER root
RUN chmod +x gradlew && ./gradlew :gateway:bootJar -x test --no-daemon

FROM eclipse-temurin:17-jre
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY --from=build /workspace/gateway/build/libs/gateway.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
