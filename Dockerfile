FROM docker.io/library/eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /src/eshop-advpro
COPY . .

RUN chmod +x gradlew
RUN ./gradlew clean bootJar

FROM docker.io/library/eclipse-temurin:21-jre-alpine AS runner

WORKDIR /app
COPY --from=builder /src/eshop-advpro/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]