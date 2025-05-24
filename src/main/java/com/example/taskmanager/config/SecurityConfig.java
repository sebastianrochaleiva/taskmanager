package com.example.taskmanager.config;

import com.example.taskmanager.security.JwtReactiveAuthenticationManager;
import com.example.taskmanager.security.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final JwtReactiveAuthenticationManager authManager;

    public SecurityConfig(JwtUtil jwtUtil, JwtReactiveAuthenticationManager authManager) {
        this.jwtUtil = jwtUtil;
        this.authManager = authManager;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Habilita CORS
            .csrf(ServerHttpSecurity.CsrfSpec::disable) // Deshabilita CSRF
            .securityContextRepository(NoOpServerSecurityContextRepository.getInstance()) // Sesión sin estado
            .authorizeExchange(exchanges -> exchanges
                .pathMatchers("/auth/**").permitAll() // Acceso público a /auth/**
                .anyExchange().authenticated() // Requiere autenticación para el resto
            )
            .addFilterAt(jwtAuthenticationFilter(), org.springframework.security.config.web.server.SecurityWebFiltersOrder.AUTHENTICATION) // Filtro JWT
            .build();
    }

    private AuthenticationWebFilter jwtAuthenticationFilter() {
        AuthenticationWebFilter filter = new AuthenticationWebFilter(authManager);
        
        // Convierte el encabezado Authorization en un objeto Authentication
        filter.setServerAuthenticationConverter(exchange -> {
            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String authToken = authHeader.substring(7);
                return Mono.just(new UsernamePasswordAuthenticationToken(authToken, authToken));
            }
            return Mono.empty();
        });

        // Configura el filtro para que sea sin estado
        filter.setSecurityContextRepository(NoOpServerSecurityContextRepository.getInstance());
        // Aplica el filtro a todas las rutas
        filter.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers("/**"));

        return filter;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200")); // URL de Angular
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true); // Permite cookies o credenciales si es necesario
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}