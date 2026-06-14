# 🎮 Ludo Elite Backend - Spring Boot WebSocket Implementation

## 📋 Overview

Backend Spring Boot lengkap untuk Ludo Elite dengan:
- ✅ **WebSocket real-time** (STOMP + SockJS)
- ✅ **JWT Authentication**
- ✅ **H2 Database** (JPA/Hibernate)
- ✅ **Game Logic Engine** dengan OOP (Inheritance & Polymorphism)
- ✅ **MVC Architecture** (Controller → Service → Repository)
- ✅ **Server sebagai source of truth**

---

## 🏗️ Arsitektur Backend

```
┌──────────────────────────────────────┐
│    WebSocket Layer                   │
│  (STOMP + SockJS)                    │
│  /ws-ludo endpoint                   │
└────────────┬─────────────────────────┘
             │
┌────────────▼─────────────────────────┐
│    Controller Layer                  │
│  - GameWebSocketController           │
│  - GameRestController                │
│  - AuthController                    │
└────────────┬─────────────────────────┘
             │
┌────────────▼─────────────────────────┐
│    Service Layer (Business Logic)   │
│  - LudoGameService (OOP Engine)      │
│  - UserService                       │
│  - AuthService                       │
└────────────┬─────────────────────────┘
             │
┌────────────▼─────────────────────────┐
│    Repository Layer (JPA)            │
│  - GameRepository                    │
│  - UserRepository                    │
│  - MoveHistoryRepository             │
└────────────┬─────────────────────────┘
             │
┌────────────▼─────────────────────────┐
│    H2 Database                       │
│  - users                             │
│  - games                             │
│  - move_history                      │
└──────────────────────────────────────┘
```

---

## 📦 Struktur File Backend

Semua file backend berada di: `src/main/java/com/ludoelite/backend/`

```
backend/
├── config/
│   ├── WebSocketConfig.java          # WebSocket configuration
│   ├── SecurityConfig.java           # Spring Security + JWT
│   └── CorsConfig.java                # CORS configuration
│
├── entity/
│   ├── User.java                      # User entity (JPA)
│   ├── Game.java                      # Game entity (JPA)
│   ├── MoveHistory.java               # Move history entity (JPA)
│   └── GameState.java                 # Game state embeddable
│
├── repository/
│   ├── UserRepository.java
│   ├── GameRepository.java
│   └── MoveHistoryRepository.java
│
├── service/
│   ├── game/
│   │   ├── LudoGameService.java       # Main game logic engine
│   │   ├── GameAction.java            # Abstract class (OOP)
│   │   ├── MoveAction.java            # Concrete action
│   │   ├── RollDiceAction.java        # Concrete action
│   │   └── GameStateManager.java      # State management
│   ├── UserService.java
│   └── AuthService.java
│
├── controller/
│   ├── GameWebSocketController.java   # WebSocket STOMP controller
│   ├── GameRestController.java        # REST API
│   └── AuthController.java            # Authentication
│
├── dto/
│   ├── request/
│   │   ├── LoginRequest.java
│   │   ├── RegisterRequest.java
│   │   ├── CreateGameRequest.java
│   │   └── MoveRequest.java
│   └── response/
│       ├── AuthResponse.java
│       ├── GameResponse.java
│       └── GameStateResponse.java
│
├── security/
│   ├── JwtTokenProvider.java          # JWT generation/validation
│   ├── JwtAuthenticationFilter.java   # JWT filter
│   └── UserDetailsServiceImpl.java    # Spring Security integration
│
└── LudoBackendApplication.java        # Main application class
```

---

## 🔌 WebSocket Endpoints

### Conectión
```
ws://localhost:8080/ws-ludo
```

### Subscribe (Client)
```
/topic/game/{gameId}        # Receive game state updates
/user/queue/errors          # Receive personal error messages
```

### Send (Client to Server)
```
/app/game/{gameId}/roll     # Roll dice
/app/game/{gameId}/move     # Move piece
/app/game/{gameId}/join     # Join game
```

---

## 🔐 JWT Authentication Flow

### 1. Register/Login
```http
POST /api/auth/register
POST /api/auth/login
Response: { "token": "eyJ...", "username": "player1" }
```

### 2. WebSocket Connection with JWT
```javascript
const token = localStorage.getItem('token');
const stompClient = Stomp.over(new SockJS('/ws-ludo'));
stompClient.connect(
    {'Authorization': `Bearer ${token}`},
    onConnected,
    onError
);
```

### 3. All WebSocket messages validated with JWT

---

## 🎮 Game Flow

### 1. Create Game (REST)
```http
POST /api/games
Authorization: Bearer {token}
Body: {
    "gameName": "My Game",
    "maxPlayers": 4
}
```

### 2. Join Game (WebSocket)
```javascript
stompClient.send(
    `/app/game/${gameId}/join`,
    {},
    JSON.stringify({username: 'player1'})
);
```

### 3. Roll Dice (WebSocket)
```javascript
stompClient.send(
    `/app/game/${gameId}/roll`,
    {},
    JSON.stringify({playerId: 1})
);
```

### 4. Move Piece (WebSocket)
```javascript
stompClient.send(
    `/app/game/${gameId}/move`,
    {},
    JSON.stringify({
        playerId: 1,
        pieceNumber: 0,
        diceValue: 6
    })
);
```

### 5. Receive Game State (WebSocket Subscribe)
```javascript
stompClient.subscribe(`/topic/game/${gameId}`, (message) => {
    const gameState = JSON.parse(message.body);
    updateUI(gameState);
});
```

---

## 🏛️ OOP Implementation dalam Game Service

### Abstraction & Inheritance

```java
// Abstract base class
public abstract class GameAction {
    public abstract GameStateResponse execute(
        Game game, 
        Player player, 
        Map<String, Object> params
    );
    
    protected abstract boolean validate(
        Game game, 
        Player player
    );
}

// Concrete implementations (Polymorphism)
public class RollDiceAction extends GameAction {
    @Override
    public GameStateResponse execute(...) {
        // Roll dice logic
    }
}

public class MoveAction extends GameAction {
    @Override
    public GameStateResponse execute(...) {
        // Move piece logic
    }
}
```

### Polymorphism in Service

```java
@Service
public class LudoGameService {
    
    private final Map<String, GameAction> actions = new HashMap<>();
    
    public LudoGameService() {
        actions.put("ROLL", new RollDiceAction());
        actions.put("MOVE", new MoveAction());
        // Polymorphic dispatch
    }
    
    public GameStateResponse processAction(
        String actionType, 
        Game game, 
        Player player, 
        Map<String, Object> params
    ) {
        GameAction action = actions.get(actionType);
        return action.execute(game, player, params);
    }
}
```

---

## 🗄️ Database Schema (H2)

### users table
```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### games table
```sql
CREATE TABLE games (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    game_name VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL,
    max_players INT NOT NULL,
    current_players INT DEFAULT 0,
    current_turn INT DEFAULT 0,
    host_user_id BIGINT,
    game_state_json TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (host_user_id) REFERENCES users(id)
);
```

### move_history table
```sql
CREATE TABLE move_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    game_id BIGINT NOT NULL,
    player_id BIGINT NOT NULL,
    action_type VARCHAR(20) NOT NULL,
    dice_value INT,
    piece_number INT,
    from_position INT,
    to_position INT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (game_id) REFERENCES games(id),
    FOREIGN KEY (player_id) REFERENCES users(id)
);
```

---

## ⚙️ Configuration Files

### application.properties
```properties
# Server
server.port=8080

# H2 Database
spring.datasource.url=jdbc:h2:mem:ludodb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# H2 Console
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# JPA
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# JWT
jwt.secret=ludoEliteSecretKeyForJWTTokenGeneration2024
jwt.expiration=86400000

# WebSocket
spring.websocket.allowed-origins=http://localhost:*
```

---

## 🚀 How to Run

### 1. Setup
```bash
cd LudoApp
# Use backend-pom.xml atau copy dependencies ke pom.xml utama
```

### 2. Run Application
```bash
mvn spring-boot:run
```

### 3. Test H2 Console
```
http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:ludodb
Username: sa
Password: (empty)
```

### 4. Test REST API
```bash
# Register
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"player1","password":"pass123"}'

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"player1","password":"pass123"}'

# Create Game
curl -X POST http://localhost:8080/api/games \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{"gameName":"Test Game","maxPlayers":4}'
```

---

## 🔒 Security Features

1. **JWT Authentication**
   - Token-based auth untuk REST API
   - Token validation untuk WebSocket handshake

2. **CORS Configuration**
   - Allow frontend origin
   - Credentials support

3. **Password Encryption**
   - BCrypt hashing
   - No plaintext passwords

4. **WebSocket Security**
   - JWT validation on connect
   - User-specific queues
   - Authorization checks per action

---

## 🧪 Testing

### Unit Tests
```bash
mvn test
```

### Integration Tests
```bash
mvn verify
```

### Manual WebSocket Testing
Use tool seperti:
- Postman (WebSocket support)
- wscat
- Browser console dengan SockJS client

---

## 📊 API Endpoints Summary

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/auth/register` | Register user | No |
| POST | `/api/auth/login` | Login user | No |
| GET | `/api/games` | Get all games | Yes |
| POST | `/api/games` | Create game | Yes |
| GET | `/api/games/{id}` | Get game details | Yes |
| POST | `/api/games/{id}/join` | Join game | Yes |
| WS | `/ws-ludo` | WebSocket connection | Yes (JWT) |
| STOMP | `/app/game/{id}/roll` | Roll dice | Yes |
| STOMP | `/app/game/{id}/move` | Move piece | Yes |
| SUB | `/topic/game/{id}` | Game state updates | Yes |

---

## 🎯 Next Steps

1. **Integrate with Frontend**
   - Update `LudoBoardController.java` WebSocket hooks
   - Connect SockJS client
   - Handle game state updates

2. **Add Features**
   - Chat system
   - Player rankings
   - Game replay
   - Tournament mode

3. **Deploy**
   - Configure production database
   - Setup SSL/TLS
   - Deploy to cloud (Heroku, AWS, etc.)

---

## 📝 Notes

- Backend adalah **source of truth**
- All game rules validated di server
- Client hanya send actions, receive states
- No client-side game logic (anti-cheat)

---

**Status: READY FOR IMPLEMENTATION** ✅

Next: Implement all Java classes...
