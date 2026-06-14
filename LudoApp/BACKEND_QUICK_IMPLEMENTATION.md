# ⚡ LUDO BACKEND - QUICK IMPLEMENTATION GUIDE

## 🎯 Goal
Implementasi Backend Spring Boot dengan WebSocket real-time untuk Ludo Elite

## ✅ Checklist Implementation

### Phase 1: Setup (10 min)
- [ ] Copy dependencies dari `backend-pom.xml` ke `pom.xml`
- [ ] Create `application.properties`
- [ ] Test run: `mvn spring-boot:run`

### Phase 2: Core Files (30 min)
- [ ] Entity: User, Game, MoveHistory
- [ ] Repository: UserRepository, GameRepository
- [ ] Configuration: WebSocket, Security

### Phase 3: Game Logic OOP (45 min)
- [ ] GameAction (Abstract)
- [ ] RollDiceAction, MoveAction (Concrete)
- [ ] LudoGameService (Orchestrator)

### Phase 4: Controllers (30 min)
- [ ] AuthController (REST)
- [ ] GameRestController (REST)
- [ ] GameWebSocketController (WebSocket)

### Phase 5: Security (20 min)
- [ ] JwtTokenProvider
- [ ] JwtAuthenticationFilter
- [ ] UserDetailsServiceImpl

### Phase 6: Testing (30 min)
- [ ] Test REST API (Postman)
- [ ] Test WebSocket (wscat/Postman)
- [ ] Integration test

---

## 📁 FILES TO CREATE (Priority Order)

### HIGH PRIORITY (Must Have)
1. ✅ `LudoBackendApplication.java` (Created)
2. ✅ `User.java` (Created)
3. `Game.java`
4. `WebSocketConfig.java`
5. `SecurityConfig.java`
6. `GameAction.java` (Abstract - OOP)
7. `LudoGameService.java`
8. `GameWebSocketController.java`

### MEDIUM PRIORITY
9. `JwtTokenProvider.java`
10. `AuthController.java`
11. `UserRepository.java`
12. `GameRepository.java`

### LOW PRIORITY (Nice to Have)
13. DTOs (Request/Response)
14. Exception Handlers
15. Additional services

---

## 🚀 FASTEST PATH TO WORKING BACKEND

### Option A: Minimal Implementation (2 hours)
Focus on core WebSocket functionality:
1. WebSocketConfig ✅
2. Game entity + repository
3. Simple GameController with STOMP
4. No JWT (for testing)

### Option B: Full Implementation (4-6 hours)
Complete system with authentication:
1. All entities + repositories
2. Full JWT security
3. Game logic with OOP
4. Complete REST + WebSocket APIs

### Option C: Use Given Code (1 hour)
1. Follow `BACKEND_CODE_COMPLETE.md`
2. Copy-paste all code
3. Fix imports
4. Test

---

## 📝 KEY CODE SNIPPETS

### 1. application.properties (REQUIRED)

```properties
server.port=8080
spring.datasource.url=jdbc:h2:mem:ludodb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.h2.console.enabled=true
spring.jpa.hibernate.ddl-auto=update
jwt.secret=yourSecretKey
jwt.expiration=86400000
```

### 2. Game Entity (Core)

```java
@Entity
@Data
public class Game {
    @Id @GeneratedValue
    private Long id;
    private String gameName;
    private String status; // WAITING, IN_PROGRESS, FINISHED
    private Integer maxPlayers = 4;
    private Integer currentPlayers = 0;
    @Column(columnDefinition = "TEXT")
    private String gameStateJson; // Store game state as JSON
    @ManyToOne
    private User host;
}
```

### 3. WebSocket Controller (Core)

```java
@Controller
public class GameWebSocketController {
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @MessageMapping("/game/{gameId}/move")
    public void handleMove(@DestinationVariable Long gameId, 
                          MoveRequest move) {
        // Process move
        // Broadcast to /topic/game/{gameId}
        messagingTemplate.convertAndSend(
            "/topic/game/" + gameId, 
            gameState
        );
    }
}
```

---

## 🔗 INTEGRATION WITH FRONTEND

### Frontend Update Required:

**File:** `LudoBoardController.java`

```java
// Add SockJS + STOMP client
private StompSession stompSession;

public void connectWebSocket() {
    String token = SessionManager.getInstance().getToken();
    WebSocketClient client = new StandardWebSocketClient();
    WebSocketStompClient stompClient = new WebSocketStompClient(client);
    
    StompHeaders headers = new StompHeaders();
    headers.add("Authorization", "Bearer " + token);
    
    stompClient.connect("http://localhost:8080/ws-ludo", headers, 
        new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(StompSession session, StompHeaders headers) {
                stompSession = session;
                subscribeToGame(gameId);
            }
        });
}

private void subscribeToGame(Long gameId) {
    stompSession.subscribe("/topic/game/" + gameId, new StompFrameHandler() {
        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            GameStateResponse state = (GameStateResponse) payload;
            Platform.runLater(() -> updateUI(state));
        }
    });
}
```

---

## 🧪 TESTING ENDPOINTS

### 1. Register User
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"player1","password":"pass123"}'
```

### 2. Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"player1","password":"pass123"}'
```

### 3. Create Game
```bash
curl -X POST http://localhost:8080/api/games \
  -H "Authorization: Bearer {TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{"gameName":"Test Game","maxPlayers":4}'
```

### 4. WebSocket Test (JavaScript)
```javascript
const socket = new SockJS('http://localhost:8080/ws-ludo');
const stompClient = Stomp.over(socket);

stompClient.connect(
    {'Authorization': `Bearer ${token}`},
    frame => {
        stompClient.subscribe(`/topic/game/${gameId}`, message => {
            console.log('Game state:', JSON.parse(message.body));
        });
    }
);
```

---

## 📚 DOCUMENTATION FILES

1. **BACKEND_README.md** - Overview & architecture
2. **BACKEND_IMPLEMENTATION_GUIDE.md** - Detailed guide
3. **BACKEND_CODE_COMPLETE.md** - Full code samples
4. **This file** - Quick reference

---

## ⏱️ TIME ESTIMATES

| Task | Time |
|------|------|
| Setup & Config | 30 min |
| Entities & Repos | 45 min |
| Services (incl OOP) | 1.5 hours |
| Controllers | 1 hour |
| Security & JWT | 1 hour |
| Testing | 1 hour |
| **TOTAL** | **5-6 hours** |

---

## 🎓 OOP DEMONSTRATION

Backend demonstrates OOP through:

**1. Abstraction:**
- `GameAction` abstract class
- Abstract methods: `validate()`, `performAction()`

**2. Inheritance:**
- `RollDiceAction extends GameAction`
- `MoveAction extends GameAction`
- Inherit template method `execute()`

**3. Polymorphism:**
```java
Map<String, GameAction> actions = new HashMap<>();
actions.put("ROLL", new RollDiceAction());
actions.put("MOVE", new MoveAction());

GameAction action = actions.get(type); // Polymorphic
return action.execute(game, player, params);
```

**4. Encapsulation:**
- All entity fields private
- JPA annotations hide DB details
- Service layer encapsulates business logic

---

## ✅ SUCCESS CRITERIA

Backend is complete when:
- [ ] Server starts without errors
- [ ] H2 console accessible
- [ ] Can register/login users
- [ ] Can create games via REST
- [ ] WebSocket accepts connections
- [ ] Can send/receive moves via WebSocket
- [ ] Game state persists to H2
- [ ] Multiple clients can connect to same game

---

## 🚨 COMMON ISSUES

### Issue: Can't connect to WebSocket
**Solution:** Check CORS config, ensure token in headers

### Issue: JWT validation fails
**Solution:** Verify jwt.secret matches, check token format

### Issue: H2 database empty
**Solution:** Check ddl-auto=update, verify @Entity annotations

### Issue: Move validation fails
**Solution:** Debug LudoGameService, check game state

---

## 📞 NEXT STEPS

1. ✅ Read this guide
2. Choose implementation path (A, B, or C)
3. Follow step-by-step or copy-paste code
4. Test with Postman/curl
5. Integrate with JavaFX frontend
6. Deploy to production

---

**Ready to implement? Start with `application.properties` and `WebSocketConfig.java`!**
