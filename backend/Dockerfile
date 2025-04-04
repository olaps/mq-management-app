FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copier le fichier JAR construit avec Maven dans le conteneur
COPY target/*.jar app.jar

# Exposer le port utilisé par l'application
EXPOSE 8080

# Point d'entrée pour démarrer l'application
ENTRYPOINT ["java", "-jar", "app.jar"]