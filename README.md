<img alt="Java" src="https://img.shields.io/badge/-Java-ED8B00?style=flat-square&logo=java&logoColor=white"/> <img alt="Spring Boot" src="https://img.shields.io/badge/-Spring_Boot-6DB33F?style=flat-square&logo=spring-boot&logoColor=white"/> <img alt="Angular" src="https://img.shields.io/badge/-Angular-DD0031?style=flat-square&logo=angular&logoColor=white"/> <img alt="PostgreSQL" src="https://img.shields.io/badge/-PostgreSQL-316192?style=flat-square&logo=postgresql&logoColor=white"/> <img alt="Redis" src="https://img.shields.io/badge/-Redis-FF4438?style=flat-square&logo=redis&logoColor=white"/> <img alt="WebSocket" src="https://img.shields.io/badge/-WebSocket-4A89F3?style=flat-square&logo=websocket&logoColor=white"/> <img alt="AWS S3" src="https://img.shields.io/badge/-AWS_S3-569A31?style=flat-square&logo=amazon-aws&logoColor=white"/>

# Nexora

Nexora is a modern collaboration platform providing real-time chat, feeds, presence tracking and team features. This repository contains the Angular frontend and Spring Boot backend used for development and deployment.

## Key Features

- Real-time 1:1 and group chat (STOMP over WebSocket)
- Presence (online/offline) tracking
- User authentication with JWT
- Feeds (posts, comments, likes)
- Groups and group chat management
- File uploads via S3 presigned URLs
- PostgreSQL persistence + optional Redis for caching/presence
- Server-Side Rendering (Angular Universal) support for the client

---

## Repository layout

```
nexora/
├── client-angular/        # Angular 17+ frontend (SSR-ready)
├── nexora-be/             # Spring Boot backend (Java 21)
├── docs/                  # Auto-generated docs and diagrams
└── README.md              # This file
```

## Tech stack

- Backend: Java 21, Spring Boot, Spring Data JPA, Spring Security, Spring WebSocket
- Frontend: Angular 17, RxJS, @stomp/stompjs, SockJS
- Database: PostgreSQL
- Cache/presence: Redis (optional)
- Storage: AWS S3 (presigned uploads)
- Build: Maven (backend), npm / Angular CLI (frontend)

---

## Getting started — quick

Prerequisites:

- Java 21 + Maven
- Node.js 18+ and npm
- PostgreSQL (local or remote)
- (Optional) Redis for presence caching

### Backend (development)

1. Change to backend folder:

```bash
cd nexora-be
```

2. Create a `.env` (optional) or export environment variables. Example `.env`:

```env
DB_URL=jdbc:postgresql://localhost:5432/nexora
DB_USERNAME=dbuser
DB_PASSWORD=dbpass
REDIS_HOST=localhost
REDIS_PORT=6379
JWT_SECRET=your-256-bit-secret
AWS_ACCESS_KEY=your-access-key
AWS_SECRET_KEY=your-secret-key
AWS_REGION=ap-south-1
AWS_BUCKET_NAME=your-bucket-name
```

3. Run the backend:

```bash
# run via Maven
mvn spring-boot:run

# or build and run the jar
mvn clean package
java -jar target/*.jar
```

The backend listens on port `8080` by default (see `nexora-be/src/main/resources/application.yaml`).

### Frontend (development)

1. Change to client folder and install dependencies:

```bash
cd client-angular
npm install
```

2. Update client environment if needed: `src/environments/environment.ts` — set `serverUrl` and `websocketUrl` (e.g. `http://localhost:8080`, `ws://localhost:8080/ws`).

3. Run the dev server:

```bash
ng serve
```

Open the app at `http://localhost:4200`.

To run the SSR build and server (production-like):

```bash
npm run build
npm run serve:ssr:client-angular
```

---

## Database & schema

- The backend uses JPA/Hibernate. You can find the schema design reference at [nexora-be/dbdiagram_nexora.md](nexora-be/dbdiagram_nexora.md).
- For local development, create the `nexora` database and ensure the DB credentials match your environment variables or `application.yaml` defaults.

## API & WebSocket

- A compact API list is available in [docs/API.md](docs/API.md).
- STOMP endpoint: `/ws` (SockJS)
- Typical STOMP topics:
  - Presence updates: `/topic/presence/updates`
  - Direct messages: `/topic/messages/{chatId}`
  - Group messages: `/topic/group-messages/{chatId}`

---

## Testing

- Backend unit/integration tests:

```bash
cd nexora-be
mvn test
```

- Frontend unit tests:

```bash
cd client-angular
ng test
```

---

## Deployment notes

- Build backend: `mvn clean package` and deploy the produced jar.
- Build frontend: `ng build --configuration production` and serve the static `dist/` assets (or use SSR server for server-side rendering).
- Recommended production stack: PostgreSQL (managed), Redis (optional), S3 for media, and a process manager (systemd / container orchestrator).
- Consider adding a `docker-compose.yml` with Postgres + Redis + backend + frontend for local full-stack testing.

---

## Contributing

We welcome contributions — please follow the standard GitHub flow:

1. Fork the repository
2. Create a descriptive branch: `git checkout -b feature/your-feature`
3. Commit your changes with clear messages
4. Push and open a Pull Request

Please include unit tests for new features and follow existing code style.

---

## Contact & support

If you have questions or want to report issues, please open an issue in this repository or contact the maintainer.

Maintainer: Shyam Kawale

---

## Acknowledgements

Thanks to all contributors and the open-source libraries that make Nexora possible.
