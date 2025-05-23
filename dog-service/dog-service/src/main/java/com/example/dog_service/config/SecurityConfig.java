package com.example.dog_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.http.HttpMethod;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) // ✅ Permite @PreAuthorize
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // ✅ CORS personalizado
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // ✅ API sin estado
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // ✅ Permitir solicitudes OPTIONS
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll() // ✅ Swagger público
                        .requestMatchers(HttpMethod.GET, "/api/dogs/public/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/dogs/**").authenticated() // ✅ Requiere autenticación para GET
                        .requestMatchers(HttpMethod.POST, "/api/dogs/register").hasRole("OWNER") // ⚠️ Requiere ajuste
                        .requestMatchers(HttpMethod.PATCH, "/api/dogs/**").hasAnyRole("OWNER", "ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Permitir orígenes específicos
        config.setAllowedOrigins(List.of("http://127.0.0.1:5500", "http://localhost:3000")); // Agrega todos los orígenes necesarios
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")); // Incluye PATCH y OPTIONS
        config.setAllowedHeaders(List.of("Authorization", "Content-Type")); // Permite encabezados personalizados
        config.setExposedHeaders(List.of("Authorization")); // Exponer encabezados si es necesario
        config.setAllowCredentials(true); // Permitir credenciales

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}