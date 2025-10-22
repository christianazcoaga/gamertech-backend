package com.api.e_commerce.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Deshabilitar CSRF para APIs REST
            .authorizeHttpRequests(auth -> auth
                // Endpoints públicos
                .requestMatchers("/api/auth/**").permitAll() // Registro y login públicos
                .requestMatchers(HttpMethod.GET, "/api/productos/**").permitAll() // Consulta de productos pública
                .requestMatchers("/h2-console/**").permitAll() // Consola H2
                
                // Endpoints de productos que requieren autenticación
                .requestMatchers(HttpMethod.POST, "/api/productos/**").authenticated() // Crear productos requiere autenticación
                .requestMatchers(HttpMethod.PUT, "/api/productos/**").authenticated() // Actualizar productos requiere autenticación
                .requestMatchers(HttpMethod.DELETE, "/api/productos/**").authenticated() // Eliminar productos requiere autenticación
                
                // Endpoints de pedidos
                .requestMatchers(HttpMethod.POST, "/api/pedidos").authenticated() // Crear pedido requiere autenticación
                .requestMatchers(HttpMethod.GET, "/api/pedidos/mis-pedidos").authenticated() // Ver mis pedidos
                .requestMatchers(HttpMethod.PUT, "/api/pedidos/*/cancelar").authenticated() // Cancelar pedido
                .requestMatchers(HttpMethod.GET, "/api/pedidos/**").hasRole("ADMIN") // Ver todos los pedidos (ADMIN)
                .requestMatchers(HttpMethod.PUT, "/api/pedidos/*/estado").hasRole("ADMIN") // Actualizar estado (ADMIN)
                .requestMatchers(HttpMethod.DELETE, "/api/pedidos/**").hasRole("ADMIN") // Eliminar pedidos (ADMIN)
                
                // Endpoints que requieren rol ADMIN
                .requestMatchers("/api/admin/**").hasRole("ADMIN") // Solo administradores
                
                // Cualquier otro endpoint requiere autenticación
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Sin sesiones, ideal para JWT
            )
            .headers(headers -> headers
                .frameOptions(frame -> frame.disable()) // Permitir frames para H2 console
            );
        
        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
