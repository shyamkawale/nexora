<img alt="Java" src="https://img.shields.io/badge/-Java%2021-ED8B00?style=flat-square&logo=java&logoColor=white"/> <img alt="Spring Boot" src="https://img.shields.io/badge/-Spring%20Boot%204.0.5-6DB33F?style=flat-square&logo=spring-boot&logoColor=white"/> <img alt="PostgreSQL" src="https://img.shields.io/badge/-PostgreSQL-316192?style=flat-square&logo=postgresql&logoColor=white"/> <img alt="Redis" src="https://img.shields.io/badge/-Redis-FF4438?style=flat-square&logo=redis&logoColor=white"/> <img alt="WebSocket" src="https://img.shields.io/badge/-WebSocket-4A89F3?style=flat-square&logo=websocket&logoColor=white"/> <img alt="AWS S3" src="https://img.shields.io/badge/-AWS_S3-569A31?style=flat-square&logo=amazon-aws&logoColor=white"/>

# Nexora Backend - Spring Boot REST API

A powerful, production-ready backend REST API for the Nexora enterprise collaboration platform. Built with Spring Boot 4.0.5, Java 21, and featuring real-time WebSocket communication, secure JWT authentication, and comprehensive social features.

## 🎯 Key Features

### Authentication & Security
- **JWT Authentication** - Stateless authentication with JSON Web Tokens
- **Password Security** - BCrypt password hashing with Spring Security
- **Role-Based Access Control** - Admin and regular user roles
- **Token Management** - Automatic token generation and validation
- **Secure API Endpoints** - Protected endpoints with proper authorization

### Real-Time Communication
- **WebSocket/STOMP** - Real-time messaging with STOMP protocol
- **Direct Messaging** - 1-to-1 conversations between users
- **Group Chat** - Multi-user group conversations
- **Presence Tracking** - Real-time online/offline status updates
- **Message History** - Persistent message storage in PostgreSQL

### User Management
- **User Registration & Login** - Secure user onboarding
- **User Profiles** - Comprehensive user information
- **User Search** - Find users by name or email
- **Admin Dashboard** - User management and administration
- **User Activation/Deactivation** - Control user access

### Social Features
- **Posts & Feeds** - Create and share posts
- **Comments** - Comment on posts for discussions
- **Likes/Reactions** - Like posts and comments
- **Social Graph** - Follow relationships (extensible)
- **Feed Generation** - Paginated social feed

### File Management
- **AWS S3 Integration** - Secure file storage in cloud
- **Presigned URLs** - Secure temporary access links
- **File Uploads** - Direct uploads via presigned URLs
- **File Downloads** - Secure file access with temporary URLs
- **Media Support** - Store files with metadata

### Infrastructure
- **PostgreSQL** - Relational database for persistence
- **Redis** - Caching and presence data (optional)
- **REST API** - RESTful endpoints with proper HTTP methods
- **OpenAPI/Swagger** - Auto-generated API documentation
- **Error Handling** - Comprehensive exception handling

---

## 🛠 Tech Stack

### Core Framework
- **Spring Boot**: 4.0.5 (latest)
- **Java**: Version 21
- **Maven**: Build tool and dependency management
- **Spring Data JPA**: ORM and database access layer

### Database & Caching
- **PostgreSQL**: 12+ (relational database)
- **Hibernate**: ORM for database operations
- **Redis**: Optional caching and presence storage
- **HikariCP**: Connection pooling

### Security & Authentication
- **Spring Security**: Authentication and authorization
- **JJWT**: JWT token generation and validation (0.12.6)
- **BCrypt**: Password hashing

### Real-Time Communication
- **Spring WebSocket**: WebSocket support
- **STOMP Protocol**: Simple Text Oriented Messaging Protocol
- **Spring Messaging**: Asynchronous messaging

### External Integrations
- **AWS S3 SDK**: Amazon S3 file storage (2.24.11)
- **OpenAI API**: AI chatbot capabilities (optional)
- **Spring WebClient**: Non-blocking HTTP client

### Development & Documentation
- **SpringDoc OpenAPI**: Swagger UI and API documentation (3.0.3)
- **Lombok**: Reduce boilerplate code
- **Dotenv**: Environment variable management

### Testing
- **JUnit**: Unit testing framework
- **Spring Test**: Spring Boot test support
- **Spring Security Test**: Security testing utilities

---

## 📁 Project Structure

```
nexora-be/
├── src/main/java/com/svk/nexora_be/
│   ├── config/                          # Spring configurations
│   │   ├── SecurityConfig.java          # Spring Security setup
│   │   ├── WebSocketConfig.java         # WebSocket/STOMP configuration
│   │   ├── S3Config.java                # AWS S3 configuration
│   │   ├── S3Properties.java            # S3 configuration properties
│   │   ├── RedisConfig.java             # Redis configuration
│   │   └── OpenApiConfig.java           # Swagger/OpenAPI configuration
│   │
│   ├── controller/                      # REST API endpoints
│   │   ├── AuthController.java          # Authentication endpoints
│   │   ├── UserController.java          # User management endpoints
│   │   ├── DirectMessageController.java # Direct message API
│   │   ├── GroupController.java         # Group management API
│   │   ├── GroupChatController.java     # Group chat API
│   │   ├── PostController.java          # Posts/feeds API
│   │   ├── CommentController.java       # Comments API
│   │   ├── LikeController.java          # Likes API
│   │   ├── PresenceController.java      # Presence tracking API
│   │   ├── FileUploadController.java    # File upload API
│   │   ├── UserManagementController.java# Admin user management
│   │   └── ChatController.java          # AI chatbot API
│   │
│   ├── service/                         # Business logic (interfaces)
│   │   ├── impl/                        # Service implementations
│   │   │   ├── AuthServiceImpl.java      # Authentication logic
│   │   │   ├── UserServiceImpl.java      # User management logic
│   │   │   ├── DirectMessageServiceImpl.java
│   │   │   ├── GroupServiceImpl.java
│   │   │   ├── GroupChatServiceImpl.java
│   │   │   ├── GroupMessageServiceImpl.java
│   │   │   ├── PostServiceImpl.java
│   │   │   ├── CommentServiceImpl.java
│   │   │   ├── LikeServiceImpl.java
│   │   │   ├── PresenceServiceImpl.java
│   │   │   ├── S3UploadService.java
│   │   │   └── OpenAIServiceImpl.java
│   │   │
│   │   └── [Service interfaces]
│   │
│   ├── repository/                      # Data access layer
│   │   ├── UserRepository.java
│   │   ├── DirectMessageRepository.java
│   │   ├── DirectMessageChatRepository.java
│   │   ├── GroupRepository.java
│   │   ├── GroupChatRepository.java
│   │   ├── GroupChatMemberRepository.java
│   │   ├── GroupMessageRepository.java
│   │   ├── PostRepository.java
│   │   ├── CommentRepository.java
│   │   ├── PostLikeRepository.java
│   │   ├── MediaFileRepository.java
│   │   └── more...
│   │
│   ├── entity/                          # JPA entities (database models)
│   │   ├── User.java
│   │   ├── DirectMessage.java
│   │   ├── DirectMessageChat.java
│   │   ├── Group.java
│   │   ├── GroupChat.java
│   │   ├── GroupChatMember.java
│   │   ├── GroupChatMessage.java
│   │   ├── GroupMessage.java
│   │   ├── Post.java
│   │   ├── Comment.java
│   │   ├── PostLike.java
│   │   ├── MediaFile.java
│   │   └── more...
│   │
│   ├── dto/                             # Data Transfer Objects
│   │   ├── request/                     # Request DTOs
│   │   │   ├── LoginRequest.java
│   │   │   ├── SignupRequest.java
│   │   │   ├── CreatePostRequest.java
│   │   │   ├── ChatRequest.java
│   │   │   └── more...
│   │   │
│   │   └── response/                    # Response DTOs
│   │       ├── UserResponse.java
│   │       ├── PostResponse.java
│   │       ├── AuthResponse.java
│   │       ├── PresignedUrlResponse.java
│   │       └── more...
│   │
│   ├── security/                        # Security utilities
│   │   ├── JwtUtil.java                 # JWT token utilities
│   │   ├── UserPrincipal.java           # Custom user principal
│   │   └── JwtAuthenticationFilter.java # JWT filter
│   │
│   ├── exception/                       # Custom exceptions
│   │   ├── GlobalExceptionHandler.java  # Global exception handler
│   │   ├── ResourceNotFoundException.java
│   │   ├── UnauthorizedException.java
│   │   └── more...
│   │
│   ├── enums/                           # Enumerations
│   │   ├── UserRole.java
│   │   ├── MessageType.java
│   │   └── more...
│   │
│   ├── model/                           # Domain models
│   │   ├── ApiResponse.java             # Standardized API response
│   │   ├── PageResponse.java
│   │   └── more...
│   │
│   ├── WebSocketHandler.java            # WebSocket message handler
│   └── NexoraBeApplication.java         # Spring Boot main class
│
├── src/main/resources/
│   ├── application.yaml                 # Application configuration
│   ├── application-dev.yaml             # Development profile
│   ├── application-prod.yaml            # Production profile
│   └── more...
│
├── src/test/java/                       # Test files
├── pom.xml                              # Maven configuration
├── mvnw                                 # Maven wrapper (Unix)
├── mvnw.cmd                             # Maven wrapper (Windows)
└── README.md                            # This file
```

---

## 🚀 Getting Started

### Prerequisites

Ensure you have installed:

- **Java 21** or higher
  ```bash
  java -version
  ```
  
- **Maven 3.6.0** or higher
  ```bash
  mvn -version
  ```
  
- **PostgreSQL 12** or higher
  ```bash
  psql --version
  ```

- **Git** for version control
  ```bash
  git --version
  ```

Optional:
- **Redis 6.0** or higher (for caching)
- **Docker** (for containerization)

### Installation & Setup

1. **Clone the repository:**
```bash
git clone <repository-url>
cd nexora-be
```

2. **Create PostgreSQL database:**
```bash
# Connect to PostgreSQL
psql -U postgres

# Create database
CREATE DATABASE nexora;
```

3. **Configure environment variables:**

Create a `.env` file in the project root:

```env
# Database Configuration
DB_URL=jdbc:postgresql://localhost:5432/nexora
DB_USERNAME=postgres
DB_PASSWORD=your_password
DB_HIKARI_MAXIMUM_POOL_SIZE=10
DB_HIKARI_MINIMUM_IDLE=2
DB_HIKARI_CONNECTION_TIMEOUT=20000
DB_HIKARI_IDLE_TIMEOUT=300000
DB_HIKARI_MAX_LIFETIME=1200000

# Redis Configuration (optional)
REDIS_HOST=localhost
REDIS_PORT=6379

# Security - JWT Configuration
JWT_SECRET=your-secret-key-minimum-256-bits-recommended-use-strong-random-string
JWT_EXPIRATION=86400000

# AWS S3 Configuration
AWS_ACCESS_KEY=your-aws-access-key-id
AWS_SECRET_KEY=your-aws-secret-access-key
AWS_REGION=ap-south-1
AWS_BUCKET_NAME=your-s3-bucket-name

# OpenAI Configuration (optional)
OPENAI_API_KEY=your-openai-api-key

# Server Configuration
SERVER_PORT=8080
```

4. **Build the project:**

```bash
# Build with Maven
mvn clean build

# Or skip tests
mvn clean build -DskipTests
```

5. **Run the application:**

```bash
# Using Maven
mvn spring-boot:run

# Or using built JAR
mvn clean package
java -jar target/nexora-be-0.0.1-SNAPSHOT.jar
```

The backend will start on `http://localhost:8080`

6. **Access API Documentation:**

Swagger UI: `http://localhost:8080/swagger-ui/index.html`

API Docs: `http://localhost:8080/v3/api-docs`

---

## 📚 API Endpoints

### Authentication

**Login**
```
POST /api/v1/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}

Response:
{
  "status": "success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": { ... }
  }
}
```

**Register**
```
POST /api/v1/auth/signup
Content-Type: application/json

{
  "email": "newuser@example.com",
  "password": "password123",
  "username": "newuser"
}
```

### Users

**Get Current User Info**
```
GET /api/v1/user/info
Authorization: Bearer <token>
```

**Get User Profile**
```
GET /api/v1/user/profile/{userId}
Authorization: Bearer <token>
```

**Search Users**
```
GET /api/v1/user/search?query=john&page=0&size=10
Authorization: Bearer <token>
```

**List All Users**
```
GET /api/v1/user/list?page=0&size=10
Authorization: Bearer <token>
```

### Direct Messages

**Send Message**
```
POST /api/v1/direct-messages
Authorization: Bearer <token>
Content-Type: application/json

{
  "chatId": "chat-id",
  "content": "Hello!",
  "attachmentId": null
}
```

**Get Chat Messages**
```
GET /api/v1/direct-messages/{chatId}
Authorization: Bearer <token>
```

**List User Chats**
```
GET /api/v1/direct-messages/user/chats
Authorization: Bearer <token>
```

### Groups

**Create Group**
```
POST /api/v1/groups
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "Team A",
  "description": "Our awesome team"
}
```

**Get Groups**
```
GET /api/v1/groups
Authorization: Bearer <token>
```

**Add Member to Group**
```
POST /api/v1/groups/{groupId}/members/{userId}
Authorization: Bearer <token>
```

### Posts & Feeds

**Create Post**
```
POST /api/v1/posts
Authorization: Bearer <token>
Content-Type: application/json

{
  "content": "This is my first post!",
  "attachmentId": null
}
```

**Get Feed**
```
GET /api/v1/posts?page=0&size=20
Authorization: Bearer <token>
```

**Get Post**
```
GET /api/v1/posts/{postId}
Authorization: Bearer <token>
```

**Delete Post**
```
DELETE /api/v1/posts/{postId}
Authorization: Bearer <token>
```

### Comments

**Create Comment**
```
POST /api/v1/comments
Authorization: Bearer <token>
Content-Type: application/json

{
  "postId": "post-id",
  "content": "Great post!"
}
```

**Get Comments**
```
GET /api/v1/comments/{postId}
Authorization: Bearer <token>
```

### Likes

**Like Post**
```
POST /api/v1/likes
Authorization: Bearer <token>
Content-Type: application/json

{
  "postId": "post-id"
}
```

**Unlike Post**
```
DELETE /api/v1/likes/{likeId}
Authorization: Bearer <token>
```

### Files

**Get Presigned Upload URL**
```
POST /api/v1/files/presigned-url
Authorization: Bearer <token>
Content-Type: application/json

{
  "fileName": "image.jpg",
  "fileType": "image/jpeg",
  "fileSize": 1024000
}
```

**Get Download URL**
```
GET /api/v1/files/download-url?fileKey=uploads/file.jpg&download=false
Authorization: Bearer <token>
```

### AI Chatbot

**Send Message to Chatbot (with Knowledge Base)**
```
POST /api/v1/chat
Authorization: Bearer <token>
Content-Type: application/json

{
  "message": "How does presence tracking work?"
}

Response:
{
  "response": "Presence tracking shows whether users are online or offline in real-time. When a user logs in, they connect via WebSocket, and their online status is broadcast to all connected clients...",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

**How it works:**
- Backend searches knowledge base for relevant documents
- Matches question against 7 knowledge documents
- Sends matching docs + question to OpenAI
- Returns AI-generated response based only on knowledge base

### Admin

**List All Users (Admin)**
```
GET /api/v1/admin/users
Authorization: Bearer <admin-token>
```

**Activate User (Admin)**
```
PUT /api/v1/admin/users/{userId}/activate
Authorization: Bearer <admin-token>
```

**Deactivate User (Admin)**
```
PUT /api/v1/admin/users/{userId}/deactivate
Authorization: Bearer <admin-token>
```

**Delete User (Admin)**
```
DELETE /api/v1/admin/users/{userId}
Authorization: Bearer <admin-token>
```

---

## 🔌 WebSocket/STOMP Configuration

### Connect to WebSocket

```
ws://localhost:8080/ws
```

### STOMP Endpoints

**Subscribe to Personal Queue**
```
SUBSCRIBE /user/queue/direct-messages
```

**Subscribe to Presence Updates**
```
SUBSCRIBE /topic/presence/{userId}
```

**Subscribe to Group Messages**
```
SUBSCRIBE /topic/group/{groupId}
```

**Send Direct Message**
```
SEND /app/direct-message
```

**Send Group Message**
```
SEND /app/group-message
```

**Update Presence**
```
SEND /app/presence-update
```

---

## 🗄 Database Schema

### Key Tables

**users**
- id, userId (UUID), email, password, username, first_name, last_name, profile_picture, bio, role, is_active, created_at, updated_at

**direct_message_chats**
- id, participant1_id, participant2_id, created_at, updated_at

**direct_messages**
- id, chat_id, sender_id, content, attachment_id, timestamp

**groups**
- id, groupId (UUID), name, description, created_by, created_at, updated_at

**group_chat_members**
- id, group_id, user_id, joined_at, role

**group_chats/group_messages**
- id, group_id, sender_id, content, attachment_id, timestamp

**posts**
- id, postId (UUID), author_id, content, attachment_id, likes_count, comments_count, created_at, updated_at

**comments**
- id, commentId (UUID), post_id, author_id, content, created_at, updated_at

**post_likes**
- id, post_id, user_id, created_at

**media_files**
- id, fileId (UUID), uploader_id, file_key, file_type, file_size, created_at

---

## 🔐 Security

### JWT Token Structure

```
Header: { "alg": "HS256", "typ": "JWT" }
Payload: { "userId": "...", "email": "...", "roles": [...], "exp": ... }
Signature: HMACSHA256(header + payload, secret)
```

### Token Generation

Tokens are generated on successful login/registration and must be included in the `Authorization` header:

```
Authorization: Bearer <token>
```

### Password Security

Passwords are hashed using BCrypt:
- Salt rounds: 10 (default)
- Never stored in plain text
- Compared during login

### CORS Configuration

Configure CORS in `SecurityConfig.java`:
```java
.cors(cors -> cors
  .configurationSource(request -> {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
    config.setAllowedMethods(Arrays.asList("*"));
    config.setAllowedHeaders(Arrays.asList("*"));
    config.setAllowCredentials(true);
    return config;
  })
)
```

---

## 🧪 Testing

### Run Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=UserServiceTest

# Run with coverage
mvn test jacoco:report
```

### Test Structure

Tests are located in `src/test/java/`:
- Unit tests for services
- Integration tests for controllers
- Repository tests with TestContainers

---

## 📊 Database Migrations

Using JPA/Hibernate with `ddl-auto: update`:

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: update
```

**Options:**
- `validate`: Only validate schema
- `update`: Update schema automatically
- `create`: Create new schema
- `create-drop`: Create on startup, drop on shutdown

For production, use `validate` and manage migrations manually.

---

## 🚀 Deployment

### Building JAR

```bash
mvn clean package -DskipTests
```

Output: `target/nexora-be-0.0.1-SNAPSHOT.jar`

### Docker Deployment

Create `Dockerfile`:

```dockerfile
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/nexora-be-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

Build and run:

```bash
docker build -t nexora-backend .
docker run -p 8080:8080 \
  -e DB_URL=jdbc:postgresql://host.docker.internal:5432/nexora \
  -e DB_USERNAME=postgres \
  -e DB_PASSWORD=password \
  nexora-backend
```

### Production Configuration

Create `application-prod.yaml`:

```yaml
spring:
  datasource:
    url: ${DB_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
  data:
    redis:
      host: ${REDIS_HOST}
      port: ${REDIS_PORT}

server:
  port: 8080
  servlet:
    context-path: /
  ssl:
    key-store: /path/to/keystore.jks
    key-store-password: ${KEYSTORE_PASSWORD}
    key-store-type: JKS
```

Run with production profile:

```bash
java -jar target/*.jar --spring.profiles.active=prod
```

---

## 🤖 AI Chatbot with Knowledge Base (RAG)

### Overview

The Nexora AI Chatbot uses **RAG (Retrieval Augmented Generation)** to provide accurate, context-aware answers about the platform. Instead of relying solely on general internet knowledge, it searches the knowledge base first and then provides answers based only on Nexora's documentation.

### How It Works

**Simple RAG Without Vector Database:**

1. **User sends question** via `/api/v1/chat` endpoint
2. **Backend searches** knowledge base for relevant documents (keyword-based)
3. **Relevant documents** are included in the OpenAI prompt
4. **OpenAI answers** ONLY from the provided documentation
5. **Response sent** back to user with accurate, platform-specific information

### Knowledge Base Structure

Knowledge documents are stored in `src/main/resources/knowledge/` as markdown files:

```
knowledge/
├── overview.md                    # Nexora platform overview
├── presence-tracking.md           # How presence tracking works
├── messaging.md                   # Direct messages & group chat
├── social-features.md             # Posts, comments, likes
├── file-management.md             # File upload & AWS S3
├── authentication-security.md     # JWT, passwords, RBAC, CORS
└── ai-chatbot.md                  # AI features & RAG documentation
```

### API Usage

**Send Message to AI Chatbot**
```
POST /api/v1/chat
Authorization: Bearer <token>
Content-Type: application/json

{
  "message": "How does presence tracking work?"
}

Response:
{
  "response": "Presence tracking shows whether users are online or offline in real-time...",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### Example Conversations

**Q1: Presence Tracking**
```
User: "How does presence tracking work?"

AI: "Presence tracking shows whether users are online or offline in real-time. 
When a user logs in, they connect via WebSocket, and their online status is 
broadcast to all connected clients. When disconnected, the status changes to 
offline with a last-seen timestamp."
```

**Q2: File Upload**
```
User: "What file types are supported?"

AI: "Nexora supports: documents (PDF, Word, Excel), images (JPG, PNG, GIF), 
videos (MP4, AVI), and archives (ZIP, RAR). Maximum file size is 50MB."
```

**Q3: Security**
```
User: "How is my password protected?"

AI: "Your password is hashed using BCrypt with 10 iterations of salt. Only the hash 
is stored in the database. When you log in, your password is compared to the stored 
hash to verify identity."
```

### Services Architecture

**KnowledgeBaseService** (Interface & Implementation)
- Loads all knowledge documents at startup
- Provides keyword-based search functionality
- Ranks documents by relevance
- Builds augmented context for AI prompts

**OpenAIService** (Enhanced)
- `ask(message)` - Simple question without context
- `askWithContext(message, context)` - RAG query with knowledge base

**ChatController**
- Uses KnowledgeBaseService to find relevant docs
- Calls OpenAIService with augmented context
- Returns AI response to client

### Implementation Details

```java
// Service method that builds augmented context
@Override
public String buildAugmentedContext(String query) {
    List<String> relevantDocs = search(query, 3);
    
    if (relevantDocs.isEmpty()) {
        return "";
    }
    
    StringBuilder context = new StringBuilder();
    context.append("Knowledge Base:\n");
    for (String doc : relevantDocs) {
        context.append(doc).append("\n---\n");
    }
    return context.toString();
}

// OpenAI prompt with knowledge base
String prompt = String.format(
    "You are the Nexora AI Assistant. Answer ONLY from the knowledge base:\n\n%s\n\nQuestion: %s",
    knowledgeContext,
    userMessage
);
```

### Adding New Knowledge Documents

To add new documentation to the knowledge base:

1. **Create markdown file** in `src/main/resources/knowledge/`
   ```
   Example: user-management.md
   ```

2. **Follow standard format:**
   - Clear headings (H2/H3)
   - Practical examples
   - API endpoint documentation
   - Related links

3. **Update KnowledgeBaseServiceImpl**
   - Add document name to `KNOWLEDGE_DOCS` array
   ```java
   private static final String[] KNOWLEDGE_DOCS = {
       "overview",
       "presence-tracking",
       "messaging",
       "social-features",
       "file-management",
       "authentication-security",
       "ai-chatbot",
       "user-management"    // New document
   };
   ```

4. **Restart backend**
   - Knowledge base reloads from classpath
   - New document immediately searchable

### Performance & Scalability

- **Load Time**: Documents loaded once at startup (~100ms)
- **Search Time**: Keyword matching is O(n*m) where n=docs, m=keywords (~50ms)
- **Total Latency**: ~500-1000ms including OpenAI API call
- **Caching**: Knowledge documents cached in memory
- **Concurrency**: Thread-safe map for concurrent access

### Future Enhancements (Phase 3)

- **Vector Database**: Use embeddings for semantic search
- **Similarity Scoring**: Semantic similarity instead of keyword matching
- **Multi-turn Context**: Remember conversation history
- **User Feedback**: Learn from user corrections
- **Analytics**: Track common questions and answer accuracy

### Troubleshooting AI Chatbot

**Problem**: AI returns "I don't know"
- **Cause**: Question not in knowledge base
- **Solution**: Add relevant documentation

**Problem**: Irrelevant answers
- **Cause**: Poor document relevance ranking
- **Solution**: Update document keywords or add targeted doc

**Problem**: Slow response
- **Cause**: OpenAI API latency
- **Solution**: Enable response caching or use faster model

---

## 🐛 Troubleshooting

### Database Connection Issues

**Error:** `Connection refused`

**Solution:**
- Ensure PostgreSQL is running
- Check database credentials in `.env`
- Verify database exists: `psql -U postgres -l`

### JWT Token Errors

**Error:** `Invalid JWT token`

**Solution:**
- Verify JWT_SECRET is set and consistent
- Check token hasn't expired
- Ensure token format is correct: `Bearer <token>`

### WebSocket Connection Issues

**Error:** `WebSocket connection failed`

**Solution:**
- Verify WebSocket endpoint is accessible
- Check firewall allows WebSocket connections
- Ensure `WebSocketConfig.java` is properly configured

### S3 Upload Errors

**Error:** `Access Denied` or `Invalid credentials`

**Solution:**
- Verify AWS credentials are correct
- Check IAM permissions for S3 bucket
- Ensure bucket exists and is accessible
- Verify region matches bucket region

---

## 📖 Architecture & Design Patterns

### Layered Architecture

```
Controller Layer (REST endpoints)
    ↓
Service Layer (Business logic)
    ↓
Repository Layer (Data access)
    ↓
Database (PostgreSQL)
```

### Design Patterns Used

- **Repository Pattern**: Data access abstraction
- **Service Pattern**: Business logic encapsulation
- **DTO Pattern**: Request/response data transfer
- **Dependency Injection**: Spring IoC container
- **Exception Handling**: Global exception handler
- **Configuration Management**: Externalized configuration

### Best Practices

- **Single Responsibility**: Each class has one responsibility
- **Dependency Injection**: Inject dependencies, don't create them
- **Error Handling**: Comprehensive exception handling
- **Logging**: Proper logging at all layers
- **Testing**: Unit tests for critical business logic
- **Documentation**: Clear code comments and API docs

---

## 📚 Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security Guide](https://spring.io/projects/spring-security)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [JWT Introduction](https://jwt.io/introduction)
- [WebSocket Protocol](https://tools.ietf.org/html/rfc6455)
- [REST API Best Practices](https://restfulapi.net/)

---

## 👨‍💻 Development Team

**Lead Developer:** Shyam Kawale

---

## 📄 License

This project is provided as-is for educational and commercial purposes.

---

## 🤝 Contributing

Contributions are welcome! Follow these guidelines:

1. Create a feature branch
2. Follow Java/Spring conventions
3. Write unit tests
4. Update API documentation
5. Create a pull request

---

## 📞 Support

For issues or questions:
- Check existing GitHub issues
- Review troubleshooting section
- Contact development team
- Check main project README

---

## 🔗 Related Resources

- [Main Project README](../README.md)
- [Frontend (Angular) README](../client-angular/README-ANGULAR.md)
- [API Documentation](http://localhost:8080/swagger-ui/index.html)

---

**Built with Spring Boot 4.0.5, Java 21, and PostgreSQL**

Last Updated: May 2026
Version: 1.0.0
