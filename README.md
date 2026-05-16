<img alt="Java" src="https://img.shields.io/badge/-Java%2021-ED8B00?style=flat-square&logo=java&logoColor=white"/> <img alt="Spring Boot" src="https://img.shields.io/badge/-Spring%20Boot%204.0.5-6DB33F?style=flat-square&logo=spring-boot&logoColor=white"/> <img alt="Angular" src="https://img.shields.io/badge/-Angular%2017-DD0031?style=flat-square&logo=angular&logoColor=white"/> <img alt="PostgreSQL" src="https://img.shields.io/badge/-PostgreSQL-316192?style=flat-square&logo=postgresql&logoColor=white"/> <img alt="Redis" src="https://img.shields.io/badge/-Redis-FF4438?style=flat-square&logo=redis&logoColor=white"/> <img alt="WebSocket" src="https://img.shields.io/badge/-WebSocket-4A89F3?style=flat-square&logo=websocket&logoColor=white"/> <img alt="AWS S3" src="https://img.shields.io/badge/-AWS%20S3-569A31?style=flat-square&logo=amazon-aws&logoColor=white"/> <img alt="OpenAI" src="https://img.shields.io/badge/-OpenAI-412991?style=flat-square&logo=openai&logoColor=white"/>

# 🚀 Nexora - Enterprise Collaboration Platform

A modern, full-stack enterprise collaboration platform built with **Spring Boot**, **Angular 17+**, and **real-time WebSocket communication**. Think Slack meets Twitter with an AI-powered chatbot — designed to showcase production-ready architecture and best practices.

## ✨ Features

### 💬 Real-Time Communication
- **1-to-1 Direct Messaging** — Instant chat between users
- **Group Chat** — Multi-user conversations with member management
- **Presence Tracking** — See who's online in real-time
- **Message History** — Full conversation persistence

### 📱 Social Features
- **Social Feed** — Create, share, and discover posts
- **Comments & Likes** — Engage with content
- **User Profiles** — Public profiles with activity history
- **User Discovery** — Search and connect with others

### 🤖 AI & Smart Features
- **AI Chatbot** — OpenAI-powered chat assistant
- **Smart Notifications** — Real-time event updates
- **File Management** — Upload & share files via AWS S3

### 👥 User Management
- **JWT Authentication** — Secure, stateless auth
- **Admin Dashboard** — User management & analytics
- **Role-Based Access** — Admin and regular user roles
- **User Activation/Deactivation** — Full user lifecycle management

### 🏗️ Enterprise Ready
- **PostgreSQL** — Relational database persistence
- **Redis** — Caching & presence optimization
- **SSR Support** — Angular Universal for SEO & performance
- **API Documentation** — Auto-generated Swagger UI

---

## 🛠 Tech Stack

| Layer | Technologies |
|-------|--------------|
| **Backend** | Spring Boot 4.0.5, Java 21, PostgreSQL, Redis |
| **Frontend** | Angular 17.3, TypeScript 5.4, RxJS, Angular Material |
| **Real-Time** | WebSocket, STOMP Protocol, SockJS |
| **Security** | JWT (JJWT), BCrypt, Spring Security |
| **Storage** | AWS S3 (presigned URLs) |
| **AI** | OpenAI API (chatbot integration) |
| **DevOps** | Maven, npm, Docker-ready |

---

## 🎯 Quick Start

### Prerequisites
- **Backend**: Java 21+, Maven, PostgreSQL 12+
- **Frontend**: Node.js 18+, npm 9+
- **Optional**: Redis, AWS S3 account, OpenAI API key

### Backend (Spring Boot)

```bash
cd nexora-be

# Create .env file to set environment variables (see .env.example file)

# Run
mvn spring-boot:run
```

Backend ready at **http://localhost:8080**  
API Docs: **http://localhost:8080/swagger-ui/index.html**

### Frontend (Angular)

```bash
cd client-angular

# Install dependencies
npm install

# Update environment config
# Edit src/environments/environment.ts:
# - serverUrl: http://localhost:8080
# - websocketUrl: ws://localhost:8080/ws

# Start dev server
npm start
```

Frontend ready at **http://localhost:4200**

---

## 📚 Documentation

- **[Backend README](./nexora-be/README.md)** — API endpoints, architecture, deployment
- **[Frontend README](./client-angular/README-ANGULAR.md)** — Components, services, styling guide
- **[API Documentation](http://localhost:8080/swagger-ui/index.html)** — Interactive Swagger UI (when backend is running)

---

## 🏛️ Architecture Highlights

```
Frontend (Angular 17)          Backend (Spring Boot)        Database
├─ Standalone Components       ├─ REST API                 └─ PostgreSQL
├─ RxJS State Management       ├─ WebSocket/STOMP          └─ Redis (Cache)
├─ Material Design UI          ├─ JWT Auth
├─ SSR (Angular Universal)     ├─ JPA/Hibernate
└─ Real-Time WebSocket         └─ Service Layer
```

**Key Architecture Decisions:**
- ✅ Microservices-ready layered architecture
- ✅ Type-safe full-stack (TypeScript + Java)
- ✅ Real-time communication over WebSocket
- ✅ Scalable with Redis caching
- ✅ Cloud-ready (AWS S3, Docker support)

---

## 🎓 What This Project Demonstrates

- ✅ Full-stack development (Spring Boot + Angular)
- ✅ Real-time communication (WebSocket/STOMP)
- ✅ Security best practices (JWT, Spring Security, CORS)
- ✅ Modern frontend architecture (standalone components, RxJS)
- ✅ Database design (PostgreSQL, relationships, migrations)
- ✅ RESTful API design with proper HTTP methods
- ✅ Cloud integration (AWS S3)
- ✅ Production-ready code (error handling, logging, validation)
- ✅ DevOps practices (Docker, environment configuration)
- ✅ AI integration (OpenAI API)

---

## 📖 Project Structure

```
nexora/
├── client-angular/          # Angular 17+ frontend (SSR-ready)
├── nexora-be/              # Spring Boot backend (Java 21)
└── README.md               # This file
```

For detailed structure, see [Backend README](./nexora-be/README.md) and [Frontend README](./client-angular/README-ANGULAR.md).

---

## 🔒 Security Features

- **JWT-based authentication** with automatic token refresh
- **Password hashing** with BCrypt
- **CORS properly configured** for frontend origin only
- **Role-based access control** (RBAC) for admin features
- **HTTPS ready** for production
- **Input validation** across all endpoints

---

## 🛣️ Roadmap

- [x] Core messaging (1-to-1 & groups)
- [x] Social feed with posts & comments
- [x] User authentication & profiles
- [x] Admin dashboard
- [x] File uploads (S3)
- [x] AI chatbot integration
- [ ] Message encryption
- [ ] Advanced search with Elasticsearch
- [ ] Microservices refactor (Optional)

---

## 🤝 Contributing

This is a portfolio project, but contributions are welcome! Feel free to:
- Fork and run locally
- Report issues or suggest features
- Submit pull requests

Follow the existing code style and include tests for new features.

---

## 👨‍💻 Author

**Shyam Kawale**

---

## 📝 License

Open source — feel free to use, modify, and learn!

---

## 💡 Getting Help

- Check the detailed [Backend README](./nexora-be/README.md) for API documentation
- Review the [Frontend README](./client-angular/README-ANGULAR.md) for component architecture
- Open an issue for bugs or questions

---

**Made with ❤️ for modern web development**
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
