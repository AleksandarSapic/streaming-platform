package com.aleksandar.streaming_platform.backend.security;

import com.aleksandar.streaming_platform.backend.model.UserRoleType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomJwtAuthenticationConverter jwtAuthenticationConverter;

    public SecurityConfig(CustomJwtAuthenticationConverter jwtAuthenticationConverter) {
        this.jwtAuthenticationConverter = jwtAuthenticationConverter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // OAuth2 Resource Server configuration
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(jwtAuthenticationConverter)
                        )
                )

                .authorizeHttpRequests(authz -> authz
                        // Public endpoints
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/content-types/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/genres/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/content/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/episodes/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll()

                        // Admin only endpoints
                        .requestMatchers(HttpMethod.POST, "/api/v1/content-types/**").hasRole(UserRoleType.ADMIN.getRoleName())
                        .requestMatchers(HttpMethod.PUT, "/api/v1/content-types/**").hasRole(UserRoleType.ADMIN.getRoleName())
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/content-types/**").hasRole(UserRoleType.ADMIN.getRoleName())
                        .requestMatchers(HttpMethod.POST, "/api/v1/genres/**").hasRole(UserRoleType.ADMIN.getRoleName())
                        .requestMatchers(HttpMethod.PUT, "/api/v1/genres/**").hasRole(UserRoleType.ADMIN.getRoleName())
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/genres/**").hasRole(UserRoleType.ADMIN.getRoleName())
                        .requestMatchers(HttpMethod.POST, "/api/v1/content/**").hasRole(UserRoleType.ADMIN.getRoleName())
                        .requestMatchers(HttpMethod.PUT, "/api/v1/content/**").hasRole(UserRoleType.ADMIN.getRoleName())
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/content/**").hasRole(UserRoleType.ADMIN.getRoleName())
                        .requestMatchers(HttpMethod.POST, "/api/v1/episodes/**").hasRole(UserRoleType.ADMIN.getRoleName())
                        .requestMatchers(HttpMethod.PUT, "/api/v1/episodes/**").hasRole(UserRoleType.ADMIN.getRoleName())
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/episodes/**").hasRole(UserRoleType.ADMIN.getRoleName())
                        .requestMatchers("/api/v1/user-roles/**").hasRole(UserRoleType.ADMIN.getRoleName())
                        .requestMatchers(HttpMethod.GET, "api/v1/users").hasRole(UserRoleType.ADMIN.getRoleName())
                        .requestMatchers(HttpMethod.POST, "/api/v1/users").hasRole(UserRoleType.ADMIN.getRoleName())
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/users/**").hasRole(UserRoleType.ADMIN.getRoleName())

                        // Authenticated user endpoints
                        .requestMatchers("/api/v1/watchlists/**").authenticated()
                        .requestMatchers("/api/v1/users/**").authenticated()

                        // All other requests need authentication
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}