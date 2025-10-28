package com.api.e_commerce.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
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
public class SecurityConfig {
    
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;
    
    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter, UserDetailsService userDetailsService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Aplica la configuración de CORS definida
            .csrf(csrf -> csrf.disable()) // Deshabilitar CSRF para APIs REST
            .authorizeHttpRequests(auth -> auth
                // Endpoints públicos
                .requestMatchers("/api/auth/**").permitAll() // Registro y login públicos
                .requestMatchers(HttpMethod.GET, "/api/productos/**").permitAll() // Consulta de productos pública
                .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll() // Consulta de categorías pública
                .requestMatchers("/h2-console/**").permitAll() // Consola H2
                
                // Endpoints de productos que requieren autenticación
                .requestMatchers(HttpMethod.POST, "/api/productos/**").authenticated() // Crear productos requiere autenticación
                .requestMatchers(HttpMethod.PUT, "/api/productos/**").authenticated() // Actualizar productos requiere autenticación
                .requestMatchers(HttpMethod.DELETE, "/api/productos/**").authenticated() // Eliminar productos requiere autenticación
                
                // Endpoints de categorías que requieren autenticación
                .requestMatchers(HttpMethod.POST, "/api/categories/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/categories/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/categories/**").authenticated()
                
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
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .headers(headers -> headers
                .frameOptions(frame -> frame.disable()) // Permitir frames para H2 console
            );
        
        return http.build();
    }
    
    @Bean
    public AuthenticationProvider authenticationProvider() {
        @SuppressWarnings("deprecation")
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(passwordEncoder());
        authProvider.setUserDetailsService(userDetailsService);
        return authProvider;
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    /**
     * Define la configuración global de CORS.
     * Permite solicitudes desde tu frontend React.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 1. Define los orígenes permitidos (Añade tu React Front-end)
        // Sustituye con el origen exacto de tu frontend (ej: http://localhost:5173, http://127.0.0.1:5173)
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173", "http://localhost:5137", "http://127.0.0.1:5173"));
        
        // 2. Define los métodos HTTP permitidos
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        // 3. Define las cabeceras (headers) permitidas (importante para Content-Type, Authorization, etc.)
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        // 4. Permite el envío de credenciales (cookies, tokens de autorización, si aplica)
        configuration.setAllowCredentials(true);
        
        // Define a qué rutas se aplica la configuración (/* = a todas las rutas)
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}
