package com.ludoelite.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for Ludo Elite Backend.
 * 
 * This Spring Boot application provides:
 * - RESTful API for game management
 * - WebSocket support for real-time multiplayer
 * - JWT authentication
 * - H2 database with JPA/Hibernate
 * 
 * @author Ludo Elite Team
 * @version 1.0
 */
@SpringBootApplication
public class LudoBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(LudoBackendApplication.class, args);
        System.out.println("\n" +
            "═══════════════════════════════════════════════════════════════\n" +
            "  🎲 Ludo Elite Backend Started Successfully! 🎲\n" +
            "═══════════════════════════════════════════════════════════════\n" +
            "  REST API:        http://localhost:8080/api\n" +
            "  WebSocket:       ws://localhost:8080/ws-ludo\n" +
            "  H2 Console:      http://localhost:8080/h2-console\n" +
            "  Swagger UI:      http://localhost:8080/swagger-ui.html\n" +
            "═══════════════════════════════════════════════════════════════\n"
        );
    }
}
