# Application de Gestion des Messages MQ

Cette application permet de gérer les messages IBM MQ Series reçus par le département de paiement de la banque. Elle offre la possibilité de stocker, consulter et gérer les messages et les partenaires.

## Fonctionnalités

- Lecture et stockage des messages déposés sur une file IBM MQ Series
- Affichage de la liste des messages dans l'interface utilisateur
- Consultation détaillée des messages
- Gestion des partenaires (ajout, modification, suppression)
- API REST pour l'accès aux messages et aux partenaires

## Architecture technique

- **Backend** : Spring Boot 2.7.8 / Java 17
- **Frontend** : Angular 17 avec Angular Material (Approche Standalone)
- **Base de données** : PostgreSQL
- **Messagerie** : IBM MQ
- **Tests** : JUnit, Mockito
- **Déploiement** : Docker, Docker Compose

## Structure du projet

```
mq-management-app/
├── backend/                  # Code source du backend Spring Boot
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   └── resources/
│   │   └── test/
│   ├── pom.xml
│   └── Dockerfile
├── frontend/                 # Code source du frontend Angular
│   ├── src/
│   │   ├── app/
│   │   │   ├── core/
│   │   │   ├── features/
│   │   │   ├── shared/
│   │   │   ├── app.component.ts
│   │   │   └── app.routes.ts
│   │   ├── assets/
│   │   └── environments/
│   ├── package.json
│   ├── angular.json
│   ├── Dockerfile
│   └── nginx.conf
├── docker-compose.yml        # Configuration Docker Compose
└── README.md                 # Cette documentation
```

## Prérequis

- Java 17 ou supérieur
- Node.js 18 ou supérieur
- Docker et Docker Compose
- Maven 3.6 ou supérieur

## Installation et démarrage

### Option 1 : Avec Docker Compose (recommandé)

1. Clonez le dépôt :
   ```bash
   git clone https://github.com/votre-organisation/mq-management-app.git
   cd mq-management-app
   ```

2. Démarrez l'application avec Docker Compose :
   ```bash
   docker-compose up -d
   ```

3. L'application sera accessible à :
   - Frontend : http://localhost:4200
   - API Backend : http://localhost:8080/api
   - Documentation API : http://localhost:8080/api/swagger-ui.html
   - Console IBM MQ : https://localhost:9443/ibmmq/console (utilisateur : admin, mot de passe : passw0rd)

### Option 2 : Démarrage manuel (développement)

#### Backend

1. Naviguez vers le répertoire du backend :
   ```bash
   cd backend
   ```

2. Compilez et exécutez l'application avec Maven :
   ```bash
   mvn clean install
   mvn spring-boot:run -Dspring-boot.run.profiles=dev
   ```

Le profil "dev" active le service de mock des messages MQ, ce qui permet de tester l'application sans avoir besoin d'un serveur IBM MQ réel.

#### Frontend

1. Naviguez vers le répertoire du frontend :
   ```bash
   cd frontend
   ```

2. Installez les dépendances et démarrez l'application :
   ```bash
   npm install
   ng serve
   ```

Le frontend sera accessible à l'adresse http://localhost:4200

## Utilisation de l'API

L'API REST expose plusieurs endpoints :

### Messages

- **GET /api/messages** : Récupérer tous les messages avec pagination
- **GET /api/messages/{id}** : Récupérer un message par ID
- **GET /api/messages/messageId/{messageId}** : Récupérer un message par messageId
- **GET /api/messages/processed/{status}** : Récupérer les messages par statut de traitement
- **GET /api/messages/dateRange** : Récupérer les messages par plage de dates
- **GET /api/messages/queue/{queueName}** : Récupérer les messages par nom de file
- **GET /api/messages/search** : Rechercher des messages

### Partenaires

- **GET /api/partners** : Récupérer tous les partenaires avec pagination
- **GET /api/partners/{id}** : Récupérer un partenaire par ID
- **GET /api/partners/alias/{alias}** : Récupérer un partenaire par alias
- **POST /api/partners** : Créer un nouveau partenaire
- **PUT /api/partners/{id}** : Mettre à jour un partenaire existant
- **DELETE /api/partners/{id}** : Supprimer un partenaire
- **GET /api/partners/search** : Rechercher des partenaires

## Documentation des composants principaux

### Backend

#### Services principaux

- **MessageService** : Gestion et récupération des messages
- **PartnerService** : Gestion des partenaires (CRUD)
- **MQListenerService** : Écoute et traitement des messages MQ
- **MockMQService** : Génère des messages factices pour le développement

#### Configuration

- Le fichier `application.yml` contient la configuration principale
- Le profil "dev" active les fonctionnalités de développement comme le mock MQ
- Flyway gère les migrations de base de données

### Frontend

#### Architecture Standalone (Angular 17)

L'application utilise l'approche standalone d'Angular 17 :
- Chaque composant est autonome et déclare ses propres dépendances
- Pas de modules NgModule traditionnels
- Système de routing simplifié

#### Fonctionnalités principales

- **Message List** : Affichage tabulaire des messages avec pagination et filtrage
- **Message Detail** : Popin affichant les détails d'un message
- **Partner List** : Gestion des partenaires avec actions CRUD
- **Partner Form** : Formulaire pour ajouter/modifier des partenaires

## Mode développement

En mode développement, le backend utilise un service de mock pour simuler la réception de messages MQ :
- Génère 10 messages au démarrage de l'application
- Ajoute un nouveau message toutes les 5 secondes
- Ces messages sont stockés en base de données et accessibles via l'API REST

Pour activer ce mode, lancez le backend avec le profil "dev" :
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## Résolution des problèmes courants

### IBM MQ sur macOS M1/M2

L'image Docker IBM MQ n'est pas compatible avec l'architecture ARM64 des Mac M1/M2. Solutions :
1. Utiliser le mode développement avec le mock MQ
2. Activer l'émulation x86_64 dans Docker Desktop
3. Utiliser une VM ou un service distant pour IBM MQ

### Erreurs CORS

Si le frontend ne peut pas communiquer avec le backend à cause d'erreurs CORS :
1. Vérifiez la configuration CORS dans `WebConfig.java`
2. Assurez-vous que l'URL du backend est correctement configurée dans les environnements Angular

### Accès à la base de données

- En mode développement local : utilisez `localhost` pour la connexion à PostgreSQL
- En mode Docker : utilisez `postgres` (nom du service dans docker-compose.yml)

## Performances et résilience

L'application a été conçue pour traiter une volumétrie importante de messages avec les mécanismes suivants :

- **Traitement asynchrone** : Consommation asynchrone des messages
- **Traitement par lots** : Pour optimiser les performances
- **Pagination** : Pour gérer de grandes quantités de données
- **Indexation optimisée** : Sur les colonnes fréquemment utilisées dans les requêtes

## Capture d'écran de l'application
- **List des messages**
![Capture d’écran 2025-04-09 à 00.53.20.png](..%2F..%2F..%2F..%2F..%2FDesktop%2FCapture%20d%E2%80%99%C3%A9cran%202025-04-09%20%C3%A0%2000.53.20.png)
- **Détails du message**
![Capture d’écran 2025-04-09 à 00.51.09.png](..%2F..%2F..%2F..%2F..%2FDesktop%2FCapture%20d%E2%80%99%C3%A9cran%202025-04-09%20%C3%A0%2000.51.09.png)
- **List des partenaires**
![Capture d’écran 2025-04-09 à 00.52.20.png](..%2F..%2F..%2F..%2F..%2FDesktop%2FCapture%20d%E2%80%99%C3%A9cran%202025-04-09%20%C3%A0%2000.52.20.png)
- **Supprimer un partenaire**
![Capture d’écran 2025-04-09 à 00.52.33.png](..%2F..%2F..%2F..%2F..%2FDesktop%2FCapture%20d%E2%80%99%C3%A9cran%202025-04-09%20%C3%A0%2000.52.33.png)
- **Ajouter un partenaire**
![Capture d’écran 2025-04-09 à 00.51.36.png](..%2F..%2F..%2F..%2F..%2FDesktop%2FCapture%20d%E2%80%99%C3%A9cran%202025-04-09%20%C3%A0%2000.51.36.png)
- **Loading d'affichage des partenaires**
![Capture d’écran 2025-04-09 à 00.51.23.png](..%2F..%2F..%2F..%2F..%2FDesktop%2FCapture%20d%E2%80%99%C3%A9cran%202025-04-09%20%C3%A0%2000.51.23.png)
