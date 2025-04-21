FROM gradle:8.5-jdk17 AS build

WORKDIR /app
COPY . /app/
RUN gradle shadowJar --no-daemon

FROM eclipse-temurin:17-jre

WORKDIR /app
COPY --from=build /app/build/libs/*-all.jar /app/urlshortener.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/urlshortener.jar"]
