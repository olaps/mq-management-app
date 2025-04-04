# Application de Gestion des Messages MQ

Cette application permet de gérer les messages IBM MQ Series reçus par le département de paiement de la banque. Elle offre la possibilité de stocker, consulter et gérer les messages et les partenaires.

## Fonctionnalités

- Lecture et stockage des messages déposés sur une file IBM MQ Series
- Affichage de la liste des messages dans l'interface utilisateur
- Consultation détaillée des messages
- Gestion des partenaires (ajout, modification, suppression)
- API REST pour l'accès aux messages et aux partenaires

## Architecture technique

- **Backend** : Spring Boot 2+ / Java 11+
- **Frontend** : Angular 17 avec Angular Material
- **Base de données** : PostgreSQL
- **Tests** : JUnit, Mockito
- **Déploiement** : Docker, Docker Compose

## Structure du projet

```
mq-management-app/
├── backend/                  # Code source du backend Spring Boot
├── frontend/                 # Code source du frontend Angular
├── docker-compose.yml        # Configuration Docker Compose
└── README.md                 # Documentation
```

## Prérequis

- Java 17
- Node.js 14 ou supérieur
- Docker et Docker Compose
- Maven 3.6 ou supérieur

## Installation

### Avec Docker Compose (recommandé)

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

### Démarrage manuel (développement)

#### Backend

1. Naviguez vers le répertoire du backend :
   ```bash
   cd backend
   ```

2. Compilez et exécutez l'application avec Maven :
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

#### Frontend

1. Naviguez vers le répertoire du frontend :
   ```bash
   cd frontend
   ```

2. Installez les dépendances et démarrez l'application :
   ```bash
   npm install
   npm start
   ```

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

## Structure de la base de données

Le schéma de la base de données comprend deux tables principales :

### Table `messages`

- `id` : ID unique du message