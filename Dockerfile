# Build stage
FROM maven:3.8.5-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests


FROM eclipse-temurin:17-jre-alpine
COPY --from=build /target/backend-1.0.0.jar backend.jar
EXPOSE 8081
ENTRYPOINT ["java", "-Dserver.port=8080", "-Dserver.address=0.0.0.0", "-jar", "backend.jar"]