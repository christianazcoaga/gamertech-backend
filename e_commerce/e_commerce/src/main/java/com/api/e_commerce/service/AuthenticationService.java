package com.api.e_commerce.service;

import com.api.e_commerce.dto.AuthenticationResponse;
import com.api.e_commerce.dto.LoginRequest;
import com.api.e_commerce.dto.RegisterRequest;
import com.api.e_commerce.dto.UserDTO;
import com.api.e_commerce.model.Role;
import com.api.e_commerce.model.User;
import com.api.e_commerce.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthenticationService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    
    public AuthenticationService(UserRepository userRepository, 
                                PasswordEncoder passwordEncoder,
                                AuthenticationManager authenticationManager,
                                JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }
    
    public AuthenticationResponse register(RegisterRequest request) {
        // Validar que el email no exista
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("El correo ya está registrado: " + request.getEmail());
        }
        
        // Validar que el username no exista
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("El nombre de usuario ya existe: " + request.getUsername());
        }
        
        // Crear nuevo usuario
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // Encriptar contraseña
        user.setName(request.getName());
        user.setApellido(request.getApellido());
        user.setRole(Role.USER); // Asignar rol USER por defecto a nuevos usuarios
        
        // Guardar usuario
        User savedUser = userRepository.save(user);
        
        // Generar token JWT
        String jwtToken = jwtService.generateToken(savedUser);
        
        // Crear respuesta
        UserDTO userDTO = mapToDTO(savedUser);
        return new AuthenticationResponse("Usuario registrado exitosamente", userDTO, jwtToken);
    }
    
    public AuthenticationResponse authenticate(LoginRequest request) {
        try {
            // Autenticar con Spring Security
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getPassword()
                )
            );
            
            // Si llega aquí, la autenticación fue exitosa
            User user = (User) authentication.getPrincipal();
            
            // Generar token JWT
            String jwtToken = jwtService.generateToken(user);
            
            UserDTO userDTO = mapToDTO(user);
            
            return new AuthenticationResponse("Login exitoso", userDTO, jwtToken);
            
        } catch (AuthenticationException e) {
            throw new IllegalArgumentException("Credenciales inválidas");
        }
    }
    
    private UserDTO mapToDTO(User user) {
        return new UserDTO(
            user.getId(),
            user.getUsernameField(),
            user.getEmail(),
            user.getName(),
            user.getApellido(),
            user.getRole(),
            user.getCreatedAt()
        );
    }
}
