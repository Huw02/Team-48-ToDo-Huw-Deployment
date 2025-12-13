#STAGE 1 - Building Stage
FROM maven:3.9.9-eclipse-temurin-21 AS build

#Target
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn package -DskipTests -B

#STAGE 2  - Deploy Stage
FROM eclipse-temurin:21-jdk-alpine AS deploy

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]