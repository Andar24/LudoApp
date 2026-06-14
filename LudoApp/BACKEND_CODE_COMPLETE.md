# 🎮 LUDO ELITE BACKEND - COMPLETE CODE IMPLEMENTATION

**Status:** Ready for Copy-Paste Implementation  
**Tech Stack:** Spring Boot 3.1.5 + WebSocket + JWT + H2 + JPA

---

## 📦 QUICK START

### 1. Update pom.xml
Copy dependencies from `backend-pom.xml` ke `pom.xml` utama

### 2. Create application.properties
Location: `src/main/resources/application.properties`

```properties
server.port=8080
spring.datasource.url=jdbc:h2:mem:ludodb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
jwt.secret=ludoEliteSecretKeyForJWTTokenGeneration2024VeryLongSecret
jwt.expiration=86400000
logging.level.com.ludoelite=DEBUG
```

### 3. Run
```bash
mvn spring-boot:run
```

---

## 🏗️ PACKAGE STRUCTURE

```
com.ludoelite.backend/
├── config/          # WebSocket, Security, CORS
├── entity/          # JPA Entities (User, Game, MoveHistory)
├── repository/      # Spring Data JPA
├── service/         # Business Logic + OOP Game Engine
├── controller/      # REST + WebSocket Controllers
├── dto/             # Request/Response DTOs
├── security/        # JWT Provider, Filters
└── exception/       # Exception Handling
```

---


## 🎯 CORE FILES - COPY & PASTE READY

---

### 📄 1. MAIN APPLICATION

**File:** `src/main/java/com/ludoelite/backend/LudoBackendApplication.java`

✅ **Already Created** - See existing file

---

### 📄 2. WEBSOCKET CONFIGURATION

**File:** `src/main/java/com/ludoelite/backend/config/WebSocketConfig.java`

```java
package com.ludoelite.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
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

---

### 📄 3. SECURITY CONFIGURATION

**File:** `src/main/java/com/ludoelite/backend/config/SecurityConfig.java`

```java
package com.ludoelite.backend.config;

import com.ludoelite.backend.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .cors().and()
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**", "/ws-ludo/**", "/h2-console/**").permitAll()
                .anyRequest().authenticated()
            )
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        // Allow H2 Console
        http.headers().frameOptions().sameOrigin();
        
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
```

---

