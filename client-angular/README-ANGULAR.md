# Nexora Angular Frontend

A modern, production-ready Angular 17+ client application for the Nexora enterprise collaboration platform. This frontend provides a comprehensive user interface for real-time communication, social features, and team collaboration.

Built with **Angular 17.3**, **TypeScript 5.4**, and featuring **Server-Side Rendering (SSR)** support with Angular Universal for improved performance and SEO.

## 🎯 Key Features

### User Management & Authentication
- **Secure Authentication** - JWT-based login and registration
- **User Profiles** - View and manage user information
- **People Discovery** - Search and find other users in the platform
- **Admin Dashboard** - User management and administration (admin only)
- **Role-based Access** - Different interfaces for different user roles

### Real-Time Communication
- **Direct Messaging** - 1-to-1 real-time conversations between users
- **Group Chat** - Multi-user group conversations with management
- **Presence Tracking** - Real-time online/offline status indicators
- **WebSocket Integration** - Powered by STOMP protocol for instant updates
- **Message History** - Full conversation history persistence

### Social Features
- **Social Feed** - Browse posts from all users in the platform
- **Create Posts** - Share content with the community
- **Comments** - Engage in discussions on posts
- **Likes/Reactions** - React to posts and comments
- **User Engagement** - See likes and comments on your content

### File Management
- **File Uploads** - Upload files to AWS S3 via presigned URLs
- **Media Support** - Support for images and other media types
- **File Sharing** - Share files within chat and posts
- **Secure Access** - Generate presigned URLs for secure file downloads

### User Settings & Personalization
- **Profile Settings** - Customize user profile information
- **Preferences** - Configure user preferences
- **Settings Management** - Manage account settings

### UI/UX Features
- **Responsive Design** - Works on desktop, tablet, and mobile
- **Real-time Updates** - Instant notifications and updates
- **Loading States** - Visual feedback during data loading
- **Error Handling** - User-friendly error messages
- **Modal Dialogs** - Popups for confirmations and inputs

## 📁 Project Structure

```
client-angular/
├── src/
│   ├── app/
│   │   ├── core/                                    # Core application services and logic
│   │   │   ├── services/                            # Core services
│   │   │   │   ├── index.ts                         # Services barrel export
│   │   │   │   ├── http.service.ts                  # HTTP client wrapper with interceptor support
│   │   │   │   ├── websocket.service.ts             # WebSocket/STOMP connection manager
│   │   │   │   ├── state.service.ts                 # Global state management (BehaviorSubjects)
│   │   │   │   ├── auth.service.ts                  # Authentication logic (login, signup, logout)
│   │   │   │   ├── user.service.ts                  # User profile and info endpoints
│   │   │   │   ├── chat.service.ts                  # Direct message operations
│   │   │   │   ├── people.service.ts                # User discovery and search
│   │   │   │   ├── presence.service.ts              # Online status tracking
│   │   │   │   ├── feed.service.ts                  # Posts, comments, likes operations
│   │   │   │   ├── file-upload.service.ts           # File upload to S3
│   │   │   │   └── toast.service.ts                 # Toast notifications
│   │   │   │
│   │   │   ├── interceptors/                        # HTTP interceptors
│   │   │   │   └── auth.interceptor.ts              # Adds JWT token to requests
│   │   │   │
│   │   │   ├── guards/                              # Route guards
│   │   │   │   ├── auth.guard.ts                    # Protects authenticated routes
│   │   │   │   └── role.guard.ts                    # Role-based access control
│   │   │   │
│   │   │   └── enums/                               # Enumerations
│   │   │       └── user-role.enum.ts                # User role definitions
│   │   │
│   │   ├── shared/                                  # Shared resources and utilities
│   │   │   ├── components/                          # Reusable UI components
│   │   │   │   ├── loader.component.ts              # Loading spinner component
│   │   │   │   ├── loader.component.html
│   │   │   │   ├── loader.component.scss
│   │   │   │   ├── popup.component.ts               # Modal/popup dialog component
│   │   │   │   ├── popup.component.html
│   │   │   │   ├── popup.component.scss
│   │   │   │   ├── search-user.component.ts         # User search component
│   │   │   │   ├── search-user.component.html
│   │   │   │   ├── search-user.component.scss
│   │   │   │   ├── error-message.component.ts       # Error display component
│   │   │   │   ├── error-message.component.html
│   │   │   │   ├── error-message.component.scss
│   │   │   │   ├── vertical-loader.component.ts     # Vertical loading indicator
│   │   │   │   ├── vertical-loader.component.html
│   │   │   │   ├── vertical-loader.component.scss
│   │   │   │   ├── full-screen-image-view.component.ts  # Image preview component
│   │   │   │   ├── full-screen-image-view.component.html
│   │   │   │   ├── full-screen-image-view.component.scss
│   │   │   │   ├── chatbot/                         # AI Chatbot components
│   │   │   │   │   ├── chatbot.component.ts
│   │   │   │   │   ├── chatbot.component.html
│   │   │   │   │   ├── chatbot.component.scss
│   │   │   │   │   ├── chatbot.service.ts           # Chatbot API service
│   │   │   │   │   └── chatbot.models.ts            # Chatbot data models
│   │   │   │   └── index.ts                         # Component barrel export
│   │   │   │
│   │   │   ├── directives/                          # Custom directives
│   │   │   │   └── [custom directives here]
│   │   │   │
│   │   │   ├── pipes/                               # Custom pipes
│   │   │   │   └── [custom pipes here]
│   │   │   │
│   │   │   └── utils/                               # Utility functions and helpers
│   │   │       └── [utility files here]
│   │   │
│   │   ├── features/                                # Feature modules (lazy-loaded)
│   │   │   ├── auth/                                # Authentication module
│   │   │   │   ├── auth.component.ts                # Auth container component
│   │   │   │   ├── auth.component.html
│   │   │   │   ├── auth.component.scss
│   │   │   │   ├── signin.component.ts              # Sign in form component
│   │   │   │   ├── signin.component.html
│   │   │   │   ├── signin.component.scss
│   │   │   │   ├── signup.component.ts              # Sign up form component
│   │   │   │   ├── signup.component.html
│   │   │   │   ├── signup.component.scss
│   │   │   │   └── auth.routes.ts                   # Auth module routes
│   │   │   │
│   │   │   ├── home/                                # Dashboard module
│   │   │   │   ├── home.component.ts
│   │   │   │   ├── home.component.html
│   │   │   │   ├── home.component.scss
│   │   │   │   └── home.routes.ts
│   │   │   │
│   │   │   ├── chat/                                # Chat module
│   │   │   │   ├── chat.component.ts                # Main chat component
│   │   │   │   ├── chat.component.html
│   │   │   │   ├── chat.component.scss
│   │   │   │   └── chat.routes.ts
│   │   │   │
│   │   │   ├── people/                              # People discovery module
│   │   │   │   ├── people.component.ts
│   │   │   │   ├── people.component.html
│   │   │   │   ├── people.component.scss
│   │   │   │   └── people.routes.ts
│   │   │   │
│   │   │   ├── user/                                # User profile module
│   │   │   │   ├── user.component.ts
│   │   │   │   ├── user.component.html
│   │   │   │   ├── user.component.scss
│   │   │   │   └── user.routes.ts
│   │   │   │
│   │   │   ├── feeds/                               # Social feed module
│   │   │   │   ├── feeds.component.ts               # Main feeds component
│   │   │   │   ├── feeds.component.html
│   │   │   │   ├── feeds.component.scss
│   │   │   │   └── feeds.routes.ts
│   │   │   │
│   │   │   ├── settings/                            # Settings module
│   │   │   │   ├── settings.component.ts
│   │   │   │   ├── settings.component.html
│   │   │   │   ├── settings.component.scss
│   │   │   │   └── settings.routes.ts
│   │   │   │
│   │   │   ├── admin/                               # Admin module (admin only)
│   │   │   │   ├── admin-dashboard/
│   │   │   │   │   ├── admin-dashboard.component.ts
│   │   │   │   │   ├── admin-dashboard.component.html
│   │   │   │   │   ├── admin-dashboard.component.scss
│   │   │   │   │   └── admin-dashboard.component.spec.ts
│   │   │   │   └── unauthorized/
│   │   │   │       └── unauthorized.component.ts     # Unauthorized access page
│   │   │   │
│   │   │   ├── department/                          # Department management module
│   │   │   │   ├── department.component.ts
│   │   │   │   ├── department.component.html
│   │   │   │   ├── department.component.scss
│   │   │   │   └── department.routes.ts
│   │   │   │
│   │   │   └── feed/                                # Individual feed module
│   │   │       └── [feed-specific components]
│   │   │
│   │   ├── app.component.ts                         # Root component with navbar
│   │   ├── app.component.html
│   │   ├── app.component.scss
│   │   ├── app.component.spec.ts
│   │   ├── app.routes.ts                            # Application routing configuration
│   │   ├── app.config.ts                            # Application configuration (providers)
│   │   └── app.config.server.ts                     # Server-side rendering configuration
│   │
│   ├── environments/                                # Environment configurations
│   │   ├── environment.ts                           # Development environment
│   │   └── environment.prod.ts                      # Production environment
│   │
│   ├── styles.scss                                  # Global SCSS styles
│   ├── main.ts                                      # Application entry point (CSR)
│   ├── main.server.ts                               # Application entry point (SSR)
│   ├── polyfills-global.ts                          # Global polyfills
│   ├── index.html                                   # HTML root file
│   └── assets/                                      # Static assets
│       ├── images/
│       ├── icons/
│       └── fonts/
│
├── server.ts                                         # Express server for SSR
├── angular.json                                      # Angular CLI configuration
├── tsconfig.json                                     # TypeScript configuration (base)
├── tsconfig.app.json                                # TypeScript configuration (app)
├── tsconfig.spec.json                               # TypeScript configuration (tests)
├── package.json                                      # npm dependencies
├── package-lock.json                                # npm lock file
├── README-ANGULAR.md                                # This file
└── README.md                                        # Alternative readme
```

## 🚀 Getting Started

### Prerequisites

Before you begin, ensure you have the following installed:

- **Node.js**: 18.0.0 or higher
- **npm**: 9.0.0 or higher (comes with Node.js)
- **Angular CLI**: 17.0.0 or higher (install with: `npm install -g @angular/cli@17`)
- **Backend API**: Nexora backend running on `http://localhost:8080`

You can check your versions with:
```bash
node --version
npm --version
ng version
```

### Installation & Setup

1. **Clone or navigate to the project directory:**
```bash
cd client-angular
```

2. **Install dependencies:**
```bash
npm install
```

3. **Configure the environment:**

Edit `src/environments/environment.ts` for development:
```typescript
export const environment = {
  production: false,
  serverUrl: 'http://localhost:8080',
  websocketUrl: 'ws://localhost:8080/ws'
};
```

Edit `src/environments/environment.prod.ts` for production:
```typescript
export const environment = {
  production: true,
  serverUrl: 'https://api.yourdomain.com',
  websocketUrl: 'wss://api.yourdomain.com/ws'
};
```

### Development Server

Start the development server:

```bash
npm start
```

or

```bash
ng serve
```

The application will be available at `http://localhost:4200/`. The app will automatically reload if you change any source files.

### Building for Production

Build the application for production:

```bash
npm run build
```

or

```bash
ng build --configuration production
```

The build artifacts will be stored in the `dist/` directory.

### Server-Side Rendering (SSR)

Build and run with Server-Side Rendering:

```bash
# Build with SSR
npm run build:ssr

# Serve with SSR
npm run serve:ssr:client-angular
```

The SSR server will run on `http://localhost:4200` and provide better performance and SEO.

### Running Tests

Run unit tests:
```bash
ng test
```

Run unit tests with code coverage:
```bash
ng test --code-coverage
```

Run end-to-end (e2e) tests:
```bash
ng e2e
```

## 🔗 API Integration

The Angular frontend integrates with the Spring Boot backend using RESTful APIs and WebSocket connections. All API calls are made through the `HttpService` which handles authentication, error handling, and token management.

### Authentication Endpoints

- `POST /api/v1/auth/login` - User login (returns JWT token)
- `POST /api/v1/auth/signup` - User registration (creates account)

**Example usage:**
```typescript
this.authService.login(email, password).subscribe({
  next: (response) => {
    // Token stored, user logged in
  },
  error: (error) => {
    // Show error message
  }
});
```

### User Endpoints

- `GET /api/v1/user/info` - Get current authenticated user information
- `GET /api/v1/user/profile/{userId}` - Get specific user's profile
- `GET /api/v1/user/search?query=name` - Search users by name or email
- `GET /api/v1/user/list?page=0&size=10` - List all users with pagination
- `PUT /api/v1/user/profile` - Update current user's profile

### Direct Message Endpoints

- `POST /api/v1/direct-messages` - Send direct message
- `GET /api/v1/direct-messages/chats/{otherUserId}` - Get or create 1-to-1 chat
- `GET /api/v1/direct-messages/{chatId}` - Get messages from specific chat
- `GET /api/v1/direct-messages/user/chats` - List all user conversations

**Example usage:**
```typescript
this.chatService.sendDirectMessage(chatId, message).subscribe({
  next: (response) => {
    // Message sent
  }
});
```

### Group Endpoints

- `POST /api/v1/groups` - Create new group
- `GET /api/v1/groups` - List user's groups
- `GET /api/v1/groups/{groupId}` - Get group details
- `PUT /api/v1/groups/{groupId}` - Update group (name, description, etc.)
- `DELETE /api/v1/groups/{groupId}` - Delete group
- `POST /api/v1/groups/{groupId}/members/{userId}` - Add member to group
- `DELETE /api/v1/groups/{groupId}/members/{userId}` - Remove member from group

### Group Chat Endpoints

- `GET /api/v1/group-chats/{groupId}` - Get all messages in group
- `POST /api/v1/group-chats` - Send message to group
- `PUT /api/v1/group-chats/{messageId}` - Edit group message
- `DELETE /api/v1/group-chats/{messageId}` - Delete group message

### Post/Feed Endpoints

- `POST /api/v1/posts` - Create new post
- `GET /api/v1/posts` - Get feed (paginated)
- `GET /api/v1/posts/{postId}` - Get specific post
- `GET /api/v1/posts/user/{userPublicId}` - Get user's posts
- `PUT /api/v1/posts/{postId}` - Update post
- `DELETE /api/v1/posts/{postId}` - Delete post

### Comment Endpoints

- `POST /api/v1/comments` - Create comment on post
- `GET /api/v1/comments/{postId}` - Get all comments for post
- `PUT /api/v1/comments/{commentId}` - Update comment
- `DELETE /api/v1/comments/{commentId}` - Delete comment

### Like Endpoints

- `POST /api/v1/likes` - Like a post
- `DELETE /api/v1/likes/{likeId}` - Unlike a post
- `GET /api/v1/likes/{postId}` - Get all likes for post

### File Upload Endpoints

- `POST /api/v1/files/presigned-url` - Generate S3 presigned URL for upload
- `GET /api/v1/files/download-url` - Generate S3 presigned URL for download

**Example usage:**
```typescript
this.fileUploadService.getPresignedUrl(fileName).subscribe({
  next: (url) => {
    // Upload file directly to S3 using the presigned URL
  }
});
```

### Presence Endpoints

- `GET /api/v1/presence/status/{userId}` - Get user's current status
- `GET /api/v1/presence/all-status` - Get all users' presence status

### Admin Endpoints

- `GET /api/v1/admin/users` - List all users (admin only)
- `PUT /api/v1/admin/users/{userId}/activate` - Activate user (admin only)
- `PUT /api/v1/admin/users/{userId}/deactivate` - Deactivate user (admin only)
- `DELETE /api/v1/admin/users/{userId}` - Delete user (admin only)

### AI Chat Endpoint

- `POST /api/v1/chat` - Send message to AI chatbot

## 🔐 Authentication & Security

### JWT Token Management

The application uses JSON Web Tokens (JWT) for stateless authentication:

1. **Token Generation**: When user logs in successfully, backend generates a JWT token
2. **Token Storage**: Token is stored in browser's `localStorage` under key `authToken`
3. **Token Transmission**: Token is automatically included in the `Authorization` header of all API requests
4. **Token Validation**: Backend validates token on each request
5. **Token Expiration**: Token expires after 24 hours (configurable on backend)

### Authentication Flow

```
User Login
  ↓
POST /api/v1/auth/login (email, password)
  ↓
Backend validates credentials
  ↓
JWT Token generated
  ↓
Token sent to frontend
  ↓
Frontend stores in localStorage
  ↓
AuthInterceptor adds token to all requests
  ↓
Backend validates token
  ↓
Access granted/denied
```

### AuthInterceptor

The `AuthInterceptor` automatically:
- Adds JWT token to all outgoing HTTP requests
- Handles token refresh/renewal logic
- Manages authentication errors
- Redirects to login on unauthorized (401) responses

### Route Guards

**AuthGuard** protects routes that require authentication:
- Checks if user is logged in
- Redirects to login page if not authenticated
- Allows navigation only to authenticated users

**RoleGuard** enforces role-based access control:
- Checks user's role
- Restricts access to admin-only routes
- Redirects to unauthorized page if user lacks permissions

### Security Best Practices

- **HTTPS Only**: Always use HTTPS in production
- **Secure Storage**: Store sensitive data in secure, HTTP-only cookies in production
- **CSRF Protection**: All state-changing requests (POST, PUT, DELETE) are protected
- **XSS Prevention**: All user input is sanitized and escaped
- **Token Expiration**: Short-lived tokens reduce exposure
- **Secure Headers**: CORS and security headers configured on backend

### Logout

When user logs out:
1. Token removed from localStorage
2. User redirected to login page
3. State cleared
4. WebSocket disconnected

## 🎨 Styling & Theming

### Global Styles

Global styles are defined in `src/styles.scss` and include:

**Flexbox Utility Classes:**
```scss
// Flexbox utilities for common layouts
.FRCC  { display: flex; flex-direction: row; justify-content: center; align-items: center; }
.FRCB  { display: flex; flex-direction: row; justify-content: space-between; align-items: center; }
.FRCS  { display: flex; flex-direction: row; justify-content: flex-start; align-items: center; }
.FRCE  { display: flex; flex-direction: row; justify-content: flex-end; align-items: center; }
.FCCC  { display: flex; flex-direction: column; justify-content: center; align-items: center; }
.FCSB  { display: flex; flex-direction: column; justify-content: space-between; align-items: center; }
```

**Spacing Utilities:**
```scss
// Margin utilities
.mt-1, .mt-2, .mt-3, .mt-4     // margin-top
.mb-1, .mb-2, .mb-3, .mb-4     // margin-bottom
.ml-1, .ml-2, .ml-3, .ml-4     // margin-left
.mr-1, .mr-2, .mr-3, .mr-4     // margin-right

// Padding utilities
.p-1, .p-2, .p-3, .p-4         // padding
.pt-1, .pt-2, .pt-3, .pt-4     // padding-top
.pb-1, .pb-2, .pb-3, .pb-4     // padding-bottom
.pl-1, .pl-2, .pl-3, .pl-4     // padding-left
.pr-1, .pr-2, .pr-3, .pr-4     // padding-right
```

**Color Utilities:**
```scss
// Text colors
.text-primary     // Primary brand color
.text-success     // Success/green color
.text-danger      // Danger/red color
.text-warning     // Warning/orange color
.text-muted       // Muted/gray color

// Background colors
.bg-light         // Light background
.bg-white         // White background
.bg-primary       // Primary background
.bg-dark          // Dark background
```

**Shadow & Border Utilities:**
```scss
// Shadows
.shadow-sm        // Small shadow
.shadow           // Medium shadow
.shadow-lg        // Large shadow

// Borders
.border           // 1px border
.border-radius    // border-radius: 4px
.border-radius-lg // border-radius: 8px
```

### Component Styling

All components use SCSS for styling with:
- **BEM (Block Element Modifier)** naming convention
- **Scoped styles** (styles are component-specific)
- **Responsive design** with media queries
- **Flexbox layouts** for responsive grids
- **Custom scrollbars** for webkit browsers

### Theming

To customize the application theme:

1. Modify color variables in `src/styles.scss`
2. Update Angular Material theme in `app.config.ts`
3. Customize component-specific colors in component SCSS files

### Material Design Components

The app uses Angular Material (17.3.10) which provides:
- Pre-built UI components
- Consistent Material Design
- Accessibility features
- Responsive layouts

Import Material components as needed in your components.

## 🔌 Core Services

The application uses several core services for backend communication and state management:

### HttpService
**Location:** `src/app/core/services/http.service.ts`

Wrapper around Angular's `HttpClient` providing:
- Automatic error handling and logging
- Request/response interceptors
- JWT token management
- Base URL handling
- Custom headers

**Methods:**
- `get<T>(url: string): Observable<T>`
- `post<T>(url: string, body: any): Observable<T>`
- `put<T>(url: string, body: any): Observable<T>`
- `delete<T>(url: string): Observable<T>`

**Example:**
```typescript
constructor(private httpService: HttpService) {}

getUser(userId: string) {
  return this.httpService.get(`/api/v1/user/profile/${userId}`);
}
```

### WebSocketService
**Location:** `src/app/core/services/websocket.service.ts`

Manages WebSocket connections for real-time communication:
- STOMP protocol over WebSocket
- Connection management (connect, disconnect)
- Message publishing and subscription
- Automatic reconnection
- Connection status tracking

**Methods:**
- `connect(): Observable<Frame>`
- `disconnect(): void`
- `subscribe(destination: string): Observable<Message>`
- `send(destination: string, body: any): void`

**Example:**
```typescript
constructor(private wsService: WebSocketService) {}

subscribeToMessages() {
  return this.wsService.subscribe('/user/queue/direct-messages');
}
```

### AuthService
**Location:** `src/app/core/services/auth.service.ts`

Handles authentication and user sessions:
- User login and registration
- Token management
- Session persistence
- Logout functionality
- Authentication state tracking

**Methods:**
- `login(email: string, password: string): Observable<AuthResponse>`
- `signup(user: SignupData): Observable<AuthResponse>`
- `logout(): void`
- `isLoggedIn(): boolean`
- `getToken(): string | null`
- `getCurrentUser(): Observable<User>`

**Example:**
```typescript
constructor(private authService: AuthService) {}

login(email: string, password: string) {
  this.authService.login(email, password).subscribe({
    next: (response) => {
      // User logged in, token stored
      this.router.navigate(['/home']);
    },
    error: (error) => {
      // Show error
    }
  });
}
```

### StateService
**Location:** `src/app/core/services/state.service.ts`

Global state management using RxJS BehaviorSubjects:
- Centralized application state
- State subscriptions for reactive updates
- State mutations and updates
- Replaces need for complex state management libraries

**Methods:**
- `getState(): Observable<AppState>`
- `setState(state: Partial<AppState>): void`
- `updateUser(user: User): void`
- `clearState(): void`

**Example:**
```typescript
constructor(private stateService: StateService) {}

ngOnInit() {
  this.stateService.getState().subscribe(state => {
    this.currentUser = state.user;
  });
}
```

### ChatService
**Location:** `src/app/core/services/chat.service.ts`

Handles direct messaging operations:
- Get or create direct message chat
- Fetch messages from chat
- List all user chats
- Send direct messages

**Methods:**
- `getOrCreateChat(userId: string): Observable<Chat>`
- `getChatMessages(chatId: string): Observable<Message[]>`
- `getUserChats(): Observable<Chat[]>`
- `sendMessage(chatId: string, message: string): Observable<Message>`

### UserService
**Location:** `src/app/core/services/user.service.ts`

User profile and information endpoints:
- Get current user info
- Fetch user profiles
- Search users
- List all users
- Update user profile

**Methods:**
- `getCurrentUser(): Observable<User>`
- `getUserProfile(userId: string): Observable<User>`
- `searchUsers(query: string): Observable<User[]>`
- `listUsers(page: number, size: number): Observable<Page<User>>`

### PeopleService
**Location:** `src/app/core/services/people.service.ts`

User discovery and people management:
- Search for users
- Get user suggestions
- Filter and discover users

**Methods:**
- `searchUsers(query: string): Observable<User[]>`
- `getSuggestions(): Observable<User[]>`

### PresenceService
**Location:** `src/app/core/services/presence.service.ts`

Presence tracking and status management:
- Broadcast user online status every 15 seconds
- Get user presence status
- Listen for presence updates

**Methods:**
- `broadcastPresence(): void`
- `getUserPresence(userId: string): Observable<PresenceStatus>`
- `subscribeToPresenceUpdates(): Observable<PresenceStatus>`

### FeedService
**Location:** `src/app/core/services/feed.service.ts`

Social feed operations:
- Create, read, update, delete posts
- Comment on posts
- Like/unlike posts
- Get user posts

**Methods:**
- `createPost(content: string): Observable<Post>`
- `getPosts(page: number, size: number): Observable<Page<Post>>`
- `getPost(postId: string): Observable<Post>`
- `deletePost(postId: string): Observable<void>`
- `createComment(postId: string, content: string): Observable<Comment>`
- `likePost(postId: string): Observable<Like>`

### ToastService
**Location:** `src/app/core/services/toast.service.ts`

User notifications using Material Snackbar:
- Show toast messages
- Different notification types (success, error, info, warning)
- Auto-dismiss with timeout

**Methods:**
- `success(message: string, duration?: number): void`
- `error(message: string, duration?: number): void`
- `info(message: string, duration?: number): void`
- `warning(message: string, duration?: number): void`

### FileUploadService
**Location:** `src/app/core/services/file-upload.service.ts`

File upload to AWS S3:
- Generate presigned upload URLs
- Generate presigned download URLs
- Handle file uploads

**Methods:**
- `getPresignedUrl(fileName: string): Observable<PresignedUrl>`
- `getDownloadUrl(fileKey: string): Observable<DownloadUrl>`

## 📦 Dependencies & Versions

### Core Angular Packages
```json
{
  "@angular/animations": "^17.3.0",
  "@angular/cdk": "^17.3.10",
  "@angular/common": "^17.3.0",
  "@angular/compiler": "^17.3.0",
  "@angular/core": "^17.3.0",
  "@angular/forms": "^17.3.0",
  "@angular/material": "^17.3.10",
  "@angular/platform-browser": "^17.3.0",
  "@angular/platform-browser-dynamic": "^17.3.0",
  "@angular/platform-server": "^17.3.0",
  "@angular/router": "^17.3.0",
  "@angular/ssr": "^17.3.11"
}
```

### Real-Time Communication
```json
{
  "@stomp/stompjs": "^7.3.0",
  "sockjs-client": "^1.6.1"
}
```

### Server-Side Rendering
```json
{
  "express": "^4.18.2"
}
```

### Utilities
```json
{
  "rxjs": "~7.8.0",
  "tslib": "^2.3.0",
  "zone.js": "~0.14.3"
}
```

### Development Dependencies
```json
{
  "@angular-devkit/build-angular": "^17.3.11",
  "@angular/cli": "^17.3.11",
  "@angular/compiler-cli": "^17.3.0",
  "typescript": "~5.4.2",
  "karma": "~6.4.0",
  "jasmine-core": "~5.1.0"
}
```

## 🧪 Testing

### Unit Tests

Run unit tests with Karma:
```bash
ng test
```

Run tests in headless mode (CI/CD):
```bash
ng test --watch=false --browsers=ChromeHeadless
```

Generate code coverage report:
```bash
ng test --code-coverage
```

Coverage reports are available in `coverage/` directory.

### End-to-End Tests

Run e2e tests:
```bash
ng e2e
```

### Writing Tests

Example unit test:
```typescript
describe('UserService', () => {
  let service: UserService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(UserService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  it('should fetch user profile', () => {
    service.getUserProfile('userId').subscribe(user => {
      expect(user.id).toBe('userId');
    });

    const req = httpMock.expectOne('/api/v1/user/profile/userId');
    expect(req.request.method).toBe('GET');
    req.flush({ id: 'userId', name: 'John' });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
```

## 📝 Development Workflow

### Creating a New Feature

1. **Create feature folder structure:**
```bash
mkdir src/app/features/my-feature
```

2. **Generate component:**
```bash
ng generate component features/my-feature/my-feature
```

3. **Create routing:**
```typescript
// my-feature.routes.ts
export const myFeatureRoutes: Routes = [
  {
    path: '',
    component: MyFeatureComponent
  }
];
```

4. **Add service if needed:**
```bash
ng generate service core/services/my-feature
```

5. **Update main routing:**
```typescript
// app.routes.ts
export const routes: Routes = [
  {
    path: 'my-feature',
    loadChildren: () => import('./features/my-feature/my-feature.routes')
      .then(m => m.myFeatureRoutes)
  }
];
```

### Code Style Guidelines

- **Naming**: Use camelCase for variables and methods, PascalCase for classes
- **Imports**: Organize imports (Angular, third-party, local)
- **Comments**: Use JSDoc comments for public methods and complex logic
- **Types**: Always use TypeScript interfaces and types (avoid `any`)
- **Observables**: Always unsubscribe to prevent memory leaks

### Component Best Practices

```typescript
@Component({
  selector: 'app-my-component',
  standalone: true,  // Use standalone components
  imports: [CommonModule, ...],
  templateUrl: './my-component.html',
  styleUrls: ['./my-component.scss']
})
export class MyComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();

  constructor(
    private service: MyService,
    private cdr: ChangeDetectionStrategy
  ) {}

  ngOnInit(): void {
    this.service.getData()
      .pipe(takeUntil(this.destroy$))
      .subscribe(data => {
        // Use data
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
```

## 🐛 Troubleshooting

### Common Issues

**WebSocket connection failed:**
- Check backend is running on correct port
- Verify WebSocket URL in `environment.ts`
- Ensure firewall allows WebSocket connections
- Check browser console for detailed error

**CORS errors:**
- Verify backend CORS configuration
- Check Origin header matches backend configuration
- Ensure credentials are being sent with requests

**Build errors:**
```bash
# Clear Angular cache
ng cache clean

# Remove node_modules and reinstall
rm -rf node_modules package-lock.json
npm install

# Clean build
ng build --configuration production --aot=true
```

**Token expiration issues:**
- Token expires after 24 hours
- Implement automatic token refresh
- Force re-login when token expires
- Regenerate token on each login

**Memory leaks:**
- Always unsubscribe from Observables
- Use `takeUntil` operator with destroy$ subject
- Avoid subscribing in templates (use `async` pipe)

### Debug Mode

Enable debug logging:
```typescript
// In main.ts or app.config.ts
import { isDevMode } from '@angular/core';

if (isDevMode()) {
  enableDebugTools(componentRef);
}
```

### Performance Optimization

- Use **OnPush** change detection strategy
- Lazy load feature modules
- Implement **virtual scrolling** for long lists
- Use **trackBy** in *ngFor loops
- Optimize bundle size with tree-shaking

## 🚀 Deployment

### Production Build

```bash
npm run build -- --configuration production
```

### Server-Side Rendering Build

```bash
npm run build:ssr
```

### Docker Deployment

Create `Dockerfile`:
```dockerfile
FROM node:18-alpine
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build:ssr
EXPOSE 4200
CMD ["npm", "run", "serve:ssr:client-angular"]
```

Build and run:
```bash
docker build -t nexora-frontend .
docker run -p 4200:4200 nexora-frontend
```

### Environment-Specific Configuration

**Production (.prod)**
```typescript
export const environment = {
  production: true,
  serverUrl: 'https://api.yourdomain.com',
  websocketUrl: 'wss://api.yourdomain.com/ws'
};
```

**Staging (.staging)**
```typescript
export const environment = {
  production: false,
  serverUrl: 'https://staging-api.yourdomain.com',
  websocketUrl: 'wss://staging-api.yourdomain.com/ws'
};
```

## 📚 Architecture & Design Patterns

### Component Architecture

```
App (Root)
├── Core Services (Singleton)
├── Features (Lazy-loaded routes)
│   ├── Auth
│   ├── Home
│   ├── Chat
│   ├── Feeds
│   └── ...
├── Shared Components
└── Shared Services
```

### State Management Pattern

Uses RxJS BehaviorSubjects instead of complex state management:
- Simpler to understand and maintain
- Less boilerplate code
- Direct access to state
- Perfect for medium-sized applications

### Service Injection Pattern

All services are provided at root level:
```typescript
@Injectable({
  providedIn: 'root'
})
export class MyService {
  // Service implementation
}
```

This ensures single instance across application.

## 📖 Additional Resources

- [Angular Documentation](https://angular.io/docs)
- [TypeScript Handbook](https://www.typescriptlang.org/docs/)
- [RxJS Documentation](https://rxjs.dev/)
- [Material Design](https://material.io/)
- [WebSocket Protocol](https://tools.ietf.org/html/rfc6455)
- [JWT Introduction](https://jwt.io/introduction)

## 📋 Feature Checklist & Future Enhancements

### Completed Features
- ✅ User Authentication (JWT)
- ✅ Direct Messaging
- ✅ Group Chat
- ✅ User Profiles
- ✅ People Discovery
- ✅ Presence Tracking
- ✅ Social Feed (Posts, Comments, Likes)
- ✅ File Uploads (S3)
- ✅ Admin Dashboard
- ✅ Server-Side Rendering (SSR)
- ✅ Real-time WebSocket Communication

### Planned Enhancements
- [ ] Message Search functionality
- [ ] Message Reactions (emojis)
- [ ] Message Editing UI
- [ ] Message Deletion UI
- [ ] Advanced User Profile Editing
- [ ] Group Management UI
- [ ] File Upload Progress Indicators
- [ ] Typing Indicators
- [ ] Message Read Receipts
- [ ] Better Message Pagination
- [ ] User Presence Visualization
- [ ] Push Notifications
- [ ] Dark Mode Theme
- [ ] Accessibility Improvements (WCAG 2.1)
- [ ] Performance Optimization (Web Workers)
- [ ] Offline Support (Service Workers)

## 🤝 Contributing

Contributions are welcome! To contribute:

1. **Fork the repository**
2. **Create a feature branch:**
```bash
git checkout -b feature/AmazingFeature
```

3. **Follow coding standards:**
   - Use TypeScript with strict mode
   - Follow Angular style guide
   - Use meaningful variable and function names
   - Add comments for complex logic
   - Write unit tests for new features

4. **Test your changes:**
```bash
ng test
ng lint
```

5. **Commit your changes:**
```bash
git commit -m "Add AmazingFeature"
```

6. **Push to the branch:**
```bash
git push origin feature/AmazingFeature
```

7. **Open a Pull Request** with detailed description

## 📄 License

This project is provided as-is for educational and commercial purposes.

## 👨‍💻 Development Team

**Lead Developer:** Shyam Kawale

## 📞 Support & Contact

For issues, questions, or suggestions:
- Check existing GitHub issues
- Review the troubleshooting section in this README
- Contact the development team
- Check the main project README for additional resources

## 🔗 Related Resources

- [Main Project README](../README.md)
- [Backend Repository](../nexora-be/)
- [Angular Official Site](https://angular.io/)
- [Spring Boot Official Site](https://spring.io/projects/spring-boot)

---

## Summary

Nexora Angular Frontend is a comprehensive, production-ready collaboration platform frontend built with modern Angular technologies. It provides:

- **Real-time Communication**: WebSocket-based STOMP for instant messaging
- **Scalable Architecture**: Lazy-loaded feature modules, standalone components
- **Type Safety**: Full TypeScript support with strict mode
- **Performance**: Server-side rendering, lazy loading, change detection optimization
- **Security**: JWT authentication, role-based access control, secure interceptors
- **User Experience**: Material Design, responsive layouts, real-time presence
- **Developer Experience**: Clear project structure, comprehensive documentation, testing setup

Whether you're building an enterprise collaboration platform or learning modern Angular development, this frontend serves as an excellent reference implementation.

---

**Built with ❤️ using Angular 17+, TypeScript 5.4, and powered by Spring Boot backend**

Last Updated: May 2026
Version: 1.0.0
