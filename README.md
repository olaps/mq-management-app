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
   ng serve
   ```

Le frontend sera accessible à l'adresse http://localhost:4200
## Mode développement
### Gestion des messages MQ

L'application offre deux options pour traiter les messages MQ :

### Option 1 : Mode Mock (pour développement)

Pour utiliser la simulation de messages MQ sans serveur IBM MQ :

1. Modifiez `application.yml` :
```yaml
app:
  mq:
    mock:
      enabled: true  # Active le mock MQ
    listener:
      enabled: false # Désactive le listener MQ réel
```

2. Démarrez l'application :
```bash
mvn spring-boot:run
```

Le service de mock générera automatiquement des messages de test.

### Option 2 : IBM MQ réel

Pour utiliser un véritable serveur IBM MQ :

1. Démarrez IBM MQ via Docker :
```bash
docker-compose up -d ibmmq
```

2. Accédez à la console IBM MQ : `https://localhost:9443/ibmmq/console/`
   - Login : admin / passw0rd

3. Pour créer un message de test :
   - Sélectionnez la queue "DEV.QUEUE.1"
   - Cliquez sur "Create message" ou "Put message"
   - Saisissez un exemple de message XML :

<img width="1446" alt="Capture d’écran 2025-04-10 à 03 39 26" src="https://github.com/user-attachments/assets/6e79a0ac-1867-440c-a931-1eb0fbefe47e" />

4. Assurez-vous que le mock est désactivé dans `application.yml` :
```yaml
app:
  mq:
    mock:
      enabled: false  # Désactive le mock MQ
    listener:
      enabled: true   # Active le listener MQ réel
```

Vous verrez les messages apparaître dans l'interface de l'application.
r ce mode, lancez le backend :
```bash
mvn spring-boot:run
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

## Résolution des problèmes courants

### IBM MQ sur macOS M1/M2

L'image Docker IBM MQ n'est pas compatible avec l'architecture ARM64 des Mac M1/M2. Solutions :
1. Utiliser le mode développement avec le mock MQ
2. Activer l'émulation x86_64 dans Docker Desktop
3. Utiliser une VM ou un service distant pour IBM MQ

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
  <img width="1582" alt="Capture d’écran 2025-04-09 à 00 53 20" src="https://github.com/user-attachments/assets/9000dcb7-4ccc-47ae-88e2-b2ba99bfba6d" />
- **Détails du message**
<img width="1582" alt="Capture d’écran 2025-04-09 à 00 51 09" src="https://github.com/user-attachments/assets/d376d1e1-5fd6-4fa4-81f7-344085f44821" />
- **List des partenaires**
<img width="1582" alt="Capture d’écran 2025-04-09 à 00 52 20" src="https://github.com/user-attachments/assets/f3c1d783-2e8d-4808-888b-aed29e40df2e" />
- **Supprimer un partenaire**
<img width="1582" alt="Capture d’écran 2025-04-09 à 00 52 33" src="https://github.com/user-attachments/assets/d77974df-5c13-421b-a1ea-c4c41c34e150" />
- **Ajouter un partenaire**
<img width="1582" alt="Capture d’écran 2025-04-09 à 00 51 36" src="https://github.com/user-attachments/assets/b49abf7f-a1ae-41a2-bb97-588c3ae7ac22" />
- **Loading d'affichage des partenaires**
<img width="1582" alt="Capture d’écran 2025-04-09 à 00 51 23" src="https://github.com/user-attachments/assets/b125195b-34bb-4b9a-8223-815f2dbc3e7e" />

## Authentification et Autorisations

L'application MQ Management intègre un système complet d'authentification et d'autorisations basé sur JSON Web Tokens (JWT).

### Fonctionnalités d'authentification

- **Connexion utilisateur** - Les utilisateurs peuvent se connecter avec leur nom d'utilisateur et mot de passe
- **Inscription** - Création de nouveaux comptes utilisateur
- **Gestion de sessions** - Stockage sécurisé des tokens JWT
- **Contrôle d'accès basé sur les rôles** - Différents niveaux d'autorisation (USER, SUPERVISOR, ADMIN)
- **Protection des routes** - Les routes non autorisées sont automatiquement protégées

### Niveaux d'autorisation

L'application définit trois niveaux d'accès :

1. **ROLE_USER** : Accès en lecture seule aux messages
   - Peut consulter la liste des messages et leurs détails

2. **ROLE_SUPERVISOR** : Accès aux messages et aux partenaires
   - Toutes les permissions du ROLE_USER
   - Peut gérer (créer, modifier, supprimer) les partenaires

3. **ROLE_ADMIN** : Accès complet à toutes les fonctionnalités
   - Toutes les permissions du ROLE_SUPERVISOR
   - Accès aux fonctionnalités d'administration système

### Utilisation

#### Connexion

1. Accédez à la page de connexion via le bouton "Connexion" dans le header
2. Saisissez vos identifiants (nom d'utilisateur et mot de passe)
3. Cliquez sur "Se connecter"

Une fois connecté, votre token JWT est stocké localement et utilisé pour authentifier les requêtes.

#### Inscription

1. Accédez à la page d'inscription via le bouton "Inscription" dans le header
2. Remplissez le formulaire avec vos informations
3. Cliquez sur "S'inscrire"

Par défaut, les nouveaux comptes reçoivent le rôle USER.

#### Déconnexion

Cliquez sur l'icône de profil dans le header puis sur "Déconnexion".

### Configuration technique

#### Backend (Spring Security)

- Utilisation de Spring Security 5 avec JWT
- Stockage sécurisé des mots de passe avec BCrypt
- Durée de validité du token configurable via `app.jwtExpirationMs`
- Configuration REST pour l'authentification sans état (stateless)

#### Frontend (Angular)

- Service d'authentification pour gérer login/logout
- Intercepteur HTTP pour ajouter automatiquement le token JWT aux requêtes
- Guards pour protéger les routes selon les rôles
- Stockage du token et des informations utilisateur dans le localStorage

*Note: Pour des raisons de sécurité, ce compte par défaut devrait être supprimé ou son mot de passe modifié dans un environnement de production.*
## Ecran pour Login.
- **Inscription avec Role User Par défaut**
  ![image](https://github.com/user-attachments/assets/143e6577-2299-4241-beb8-f1d7d13f8d6d)
- **Authentification**
![image](https://github.com/user-attachments/assets/5259ad56-bb64-4cff-8795-0aaaf0ba4161)
- **Déconnxion**
![image](https://github.com/user-attachments/assets/3ae1e6b5-1336-4ec8-8ec2-dd935b8693cd)

