# CollabGen Angular Client

A modern, user-centric collaboration application built with Angular 17+. This is a fresh, simplified Angular UI that mirrors the user-focused features of the React client, integrated with the Spring Boot backend APIs.

## 🎯 Features

- **User Authentication** - Secure signup/signin with JWT tokens
- **Direct Messaging** - 1-to-1 real-time messaging between users
- **Group Chat** - Create and manage group conversations
- **User Profiles** - View and manage user profiles
- **People Discovery** - Find and connect with other users
- **WebSocket Support** - Real-time communication using WebSocket
- **Presence Tracking** - Track user online status

## 📁 Project Structure

```
client-angular/
├── src/
│   ├── app/
│   │   ├── core/                          # Core application layer
│   │   │   ├── services/                  # API services (HTTP, WebSocket, Auth, Chat, User, People)
│   │   │   ├── interceptors/              # HTTP interceptors (Auth interceptor)
│   │   │   └── guards/                    # Route guards (Auth guard)
│   │   │
│   │   ├── shared/                        # Shared resources
│   │   │   ├── components/                # Reusable UI components
│   │   │   │   ├── loader.component.ts    # Loading spinner
│   │   │   │   ├── popup.component.ts     # Modal/popup dialog
│   │   │   │   ├── search-user.component.ts   # User search
│   │   │   │   ├── error-message.component.ts # Error display
│   │   │   │   ├── vertical-loader.component.ts
│   │   │   │   └── full-screen-image-view.component.ts
│   │   │   ├── directives/                # Custom directives
│   │   │   ├── pipes/                     # Custom pipes
│   │   │   └── utils/                     # Utility functions and services
│   │   │
│   │   ├── features/                      # Feature modules (lazy-loaded)
│   │   │   ├── auth/                      # Authentication
│   │   │   │   ├── auth.component.ts
│   │   │   │   ├── signin.component.ts
│   │   │   │   ├── signup.component.ts
│   │   │   │   └── auth.routes.ts
│   │   │   │
│   │   │   ├── home/                      # Dashboard
│   │   │   │   ├── home.component.ts
│   │   │   │   └── home.routes.ts
│   │   │   │
│   │   │   ├── chat/                      # Chat module
│   │   │   │   ├── chat.component.ts
│   │   │   │   └── chat.routes.ts
│   │   │   │
│   │   │   ├── people/                    # People discovery
│   │   │   │   ├── people.component.ts
│   │   │   │   └── people.routes.ts
│   │   │   │
│   │   │   ├── user/                      # User profile
│   │   │   │   ├── user.component.ts
│   │   │   │   └── user.routes.ts
│   │   │   │
│   │   │   ├── feeds/                     # Feeds/posts
│   │   │   │   ├── feeds.component.ts
│   │   │   │   └── feeds.routes.ts
│   │   │   │
│   │   │   └── settings/                  # User settings
│   │   │       ├── settings.component.ts
│   │   │       └── settings.routes.ts
│   │   │
│   │   ├── app.component.ts               # Root component with navbar
│   │   ├── app.routes.ts                  # Application routing
│   │   └── app.config.ts                  # Application configuration
│   │
│   ├── environments/
│   │   ├── environment.ts                 # Development environment
│   │   └── environment.prod.ts            # Production environment
│   │
│   ├── styles.scss                        # Global styles with utilities
│   ├── main.ts                            # Application entry point
│   └── index.html                         # HTML root
│
├── angular.json                           # Angular build configuration
├── tsconfig.json                          # TypeScript configuration
├── package.json                           # Dependencies
└── README.md                              # This file
```

## 🚀 Getting Started

### Prerequisites
- Node.js 18+
- Angular CLI 17+
- npm or yarn

### Installation

1. **Install dependencies**:
```bash
cd client-angular
npm install
```

2. **Configure environment**:
Update `src/environments/environment.ts` with your backend URLs:
```typescript
export const environment = {
  production: false,
  serverUrl: 'http://localhost:8080',
  websocketUrl: 'ws://localhost:8080/ws'
};
```

### Development

Start the development server:
```bash
ng serve
```

Navigate to `http://localhost:4200/`. The application will automatically reload if you change any source files.

### Build

Build for production:
```bash
ng build --configuration production
```

The build artifacts will be stored in the `dist/` directory.

## 🔗 API Integration

The app integrates with the Spring Boot backend using REST APIs:

### Authentication
- `POST /api/v1/auth/login` - User login
- `POST /api/v1/auth/signup` - User registration

### Users
- `GET /api/v1/user/info` - Get current user info
- `GET /api/v1/user/profile/{userId}` - Get user profile

### Direct Messages
- `GET /api/v1/direct-messages/chats/{otherUserId}` - Get or create 1-to-1 chat
- `GET /api/v1/direct-messages/{chatId}` - Get messages from chat
- `GET /api/v1/direct-messages/user/chats` - List all chats

### Groups
- `POST /api/v1/groups` - Create group
- `GET /api/v1/groups` - List groups
- `GET /api/v1/groups/{groupId}` - Get group details
- `POST /api/v1/groups/{groupId}/members/{userId}` - Add member
- `DELETE /api/v1/groups/{groupId}/members/{userId}` - Remove member

### Group Chat
- `GET /api/v1/group-chats/{groupId}/messages` - Get group messages
- `DELETE /api/v1/group-chats/messages/{messageId}` - Delete message

## 🔐 Authentication & Security

- **JWT Tokens**: Stored in localStorage with `authToken` key
- **Auth Guard**: Protects routes requiring authentication
- **HTTP Interceptor**: Automatically adds Bearer token to requests
- **Token Expiration**: 24 hours (configured on backend)

## 🎨 Styling

Global styles include:
- **CSS Reset**: Normalized default styles
- **Flexbox Utilities**: `FRCC`, `FRCB`, `FCCC`, etc. (matching React app)
- **Spacing Utilities**: Margin and padding helpers
- **Color System**: Primary, success, danger, warning colors
- **Shadows & Borders**: Common shadow and border-radius styles
- **Custom Scrollbar**: Styled webkit scrollbar

### Global Classes

```scss
// Flexbox utilities
.FRCC { display: flex; flex-direction: row; justify-content: center; align-items: center; }
.FRCB { display: flex; flex-direction: row; justify-content: space-between; align-items: center; }
.FCCC { display: flex; flex-direction: column; justify-content: center; align-items: center; }

// Spacing
.mt-1, .mt-2, .mt-3, .mt-4
.mb-1, .mb-2, .mb-3, .mb-4
.p-1, .p-2, .p-3, .p-4

// Colors
.text-primary, .text-success, .text-danger, .text-muted
.bg-light, .bg-white, .bg-primary

// Shadows
.shadow-sm, .shadow, .shadow-lg
```

## 🔌 Services

### HttpService
Wrapper around HttpClient with automatic error handling and token management.

### WebSocketService
Manages WebSocket connections for real-time communication.

### StateService
Global state management using BehaviorSubjects (similar to Redux in React).

### AuthService
Handles authentication (signin, signup, logout) and token management.

### ChatService
API calls for direct messages and group chat operations.

### UserService
User profile and information API calls.

### PeopleService
User search and discovery API calls.

### PresenceService
Broadcasts user online status every 15 seconds via WebSocket.

## 📦 Key Dependencies

- **Angular**: 17.3.0
- **RxJS**: 7.8.0
- **TypeScript**: 5.4.2
- **Express**: 4.18.2 (for SSR)
- **Angular Universal**: 17.3.11 (for server-side rendering)

## 🧪 Testing

Run unit tests:
```bash
ng test
```

Run e2e tests:
```bash
ng e2e
```

## 📝 Development Notes

- **Standalone Components**: All components use Angular's standalone API (no NgModules)
- **Route Guards**: Implemented with AuthGuard to protect authenticated routes
- **HTTP Interceptors**: AuthInterceptor adds JWT token to all requests
- **Reactive Programming**: Extensive use of RxJS Observables and Operators
- **Type Safety**: Full TypeScript typing for API responses

## 🚧 Future Enhancements

- [ ] Add Feeds/Posts module
- [ ] Implement message search
- [ ] Add message reactions
- [ ] Implement message editing/deletion UI
- [ ] Add user profile editing
- [ ] Implement group management UI
- [ ] Add file upload for attachments
- [ ] Implement typing indicators
- [ ] Add read receipts display
- [ ] Message pagination improvements
- [ ] User presence visualization
- [ ] Add notifications

## 📞 Support

For issues or questions, refer to the main project README or contact the development team.

---

**Built with ❤️ using Angular 17+**
