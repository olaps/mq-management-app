# Étape de build
FROM maven:3.8-amazoncorretto-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Étape d'exécution
FROM amazoncorretto:17-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
RUN ls -la /app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]