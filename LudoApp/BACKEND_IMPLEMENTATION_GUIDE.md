# 🚀 Ludo Elite Backend - Complete Implementation Guide

## 📋 Table of Contents
1. [Project Setup](#project-setup)
2. [Configuration Files](#configuration-files)
3. [Entity Layer](#entity-layer)
4. [Repository Layer](#repository-layer)
5. [Service Layer with OOP](#service-layer)
6. [Controller Layer](#controller-layer)
7. [Security & JWT](#security--jwt)
8. [WebSocket Configuration](#websocket-configuration)
9. [Testing](#testing)
10. [Deployment](#deployment)

---

## 1. Project Setup

### Dependencies (backend-pom.xml)
All dependencies sudah defined di `backend-pom.xml`:
- Spring Boot Web
- Spring Boot WebSocket
- Spring Boot Data JPA
- Spring Boot Security
- H2 Database
- JWT (jjwt)
- Lombok

### application.properties
Create file: `src/main/resources/application.properties`

```properties
# Server Configuration
server.port=8080
server.servlet.context-path=/

# H2 Database Configuration
spring.datasource.url=jdbc:h2:mem:ludodb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# JPA/Hibernate Configuration
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# JWT Configuration
jwt.secret=ludoEliteSecretKeyForJWTTokenGeneration2024VeryLongSecret
jwt.expiration=86400000

# Logging
logging.level.com.ludoelite=DEBUG
logging.level.org.springframework.security=DEBUG
logging.level.org.springframework.messaging=DEBUG

# CORS
cors.allowed-origins=http://localhost:*,http://127.0.0.1:*
```

---

## 2. Configuration Files

Semua configuration files di package: `com.ludoelite.backend.config`

### Files yang perlu dibuat:
1. `WebSocketConfig.java` - WebSocket + STOMP configuration
2. `SecurityConfig.java` - Spring Security + JWT
3. `CorsConfig.java` - CORS configuration

---

## 3. Entity Layer (JPA)

### Package: `com.ludoelite.backend.entity`

#### Files:
1. **User.java** ✅ (Already created)
2. **Game.java** (Next)
3. **MoveHistory.java**
4. **GameState.java** (Embeddable)

---

## 4. Repository Layer

### Package: `com.ludoelite.backend.repository`

#### Files:
1. **UserRepository.java**
2. **GameRepository.java**
3. **MoveHistoryRepository.java**

---

## 5. Service Layer with OOP

### Package: `com.ludoelite.backend.service`

#### Game Logic with OOP:
```
service/
├── game/
│   ├── LudoGameService.java      # Main game service
│   ├── GameAction.java            # Abstract base (OOP)
│   ├── RollDiceAction.java        # Concrete (Inheritance)
│   ├── MoveAction.java            # Concrete (Inheritance)
│   ├── JoinGameAction.java        # Concrete (Inheritance)
│   └── GameStateManager.java      # State management
├── UserService.java
└── AuthService.java
```

**OOP Demonstration:**
- `GameAction` = Abstract parent class
- `RollDiceAction`, `MoveAction`, `JoinGameAction` = Concrete children
- Polymorphic dispatch in `LudoGameService`

---


## 6. Complete Backend File Structure

```
src/main/java/com/ludoelite/backend/
│
├── LudoBackendApplication.java          ✅ Created
│
├── config/
│   ├── WebSocketConfig.java
│   ├── SecurityConfig.java
│   ├── CorsConfig.java
│   └── JwtProperties.java
│
├── entity/
│   ├── User.java                         ✅ Created
│   ├── Game.java
│   ├── MoveHistory.java
│   └── PlayerColor.java (Enum)
│
├── repository/
│   ├── UserRepository.java
│   ├── GameRepository.java
│   └── MoveHistoryRepository.java
│
├── service/
│   ├── game/
│   │   ├── LudoGameService.java          ⭐ Main game engine
│   │   ├── GameAction.java                ⭐ Abstract (OOP)
│   │   ├── RollDiceAction.java            ⭐ Concrete
│   │   ├── MoveAction.java                ⭐ Concrete
│   │   └── GameStateManager.java
│   ├── UserService.java
│   └── AuthService.java
│
├── controller/
│   ├── GameWebSocketController.java       ⭐ WebSocket
│   ├── GameRestController.java
│   └── AuthController.java
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
│   ├── JwtTokenProvider.java
│   ├── JwtAuthenticationFilter.java
│   └── UserDetailsServiceImpl.java
│
└── exception/
    ├── GameException.java
    ├── InvalidMoveException.java
    └── GlobalExceptionHandler.java
```

---

## 7. Key Implementation Code Samples

### A. WebSocket Configuration

**File:** `src/main/java/com/ludoelite/backend/config/WebSocketConfig.java`

```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Enable simple broker for broadcasting
        config.enableSimpleBroker("/topic", "/queue");
        // Prefix for client-to-server messages
        config.setApplicationDestinationPrefixes("/app");
        // Prefix for user-specific messages
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-ludo")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}
```

### B. Game Action (Abstract - OOP)

**File:** `src/main/java/com/ludoelite/backend/service/game/GameAction.java`

```java
public abstract class GameAction {
    
    /**
     * Execute the game action.
     * Template Method Pattern + Polymorphism
     */
    public final GameStateResponse execute(
            Game game, 
            User player, 
            Map<String, Object> params) {
        
        // Validate action (may differ per action type)
        if (!validate(game, player, params)) {
            throw new InvalidMoveException("Invalid action");
        }
        
        // Execute specific action (polymorphic)
        performAction(game, player, params);
        
        // Update game state
        game.updateTimestamp();
        
        return createResponse(game);
    }
    
    // Abstract methods (must be implemented by children)
    protected abstract boolean validate(
        Game game, User player, Map<String, Object> params);
    
    protected abstract void performAction(
        Game game, User player, Map<String, Object> params);
    
    // Concrete method (shared by all)
    protected GameStateResponse createResponse(Game game) {
        return new GameStateResponse(game);
    }
}
```

---

