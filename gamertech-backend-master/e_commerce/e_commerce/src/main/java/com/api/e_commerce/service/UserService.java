package com.api.e_commerce.service;

import com.api.e_commerce.dto.UserDTO;
import com.api.e_commerce.dto.UserRequest;
import com.api.e_commerce.dto.DireccionDTO;
import com.api.e_commerce.dto.DireccionRequest;
import com.api.e_commerce.exception.ResourceNotFoundException;
import com.api.e_commerce.model.User;
import com.api.e_commerce.model.Direccion;
import com.api.e_commerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return mapToDTO(user);
    }
    
    @Transactional(readOnly = true)
    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return mapToDTO(user);
    }
    
    public UserDTO createUser(UserRequest request) {
        // Validar email único
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("El correo ya existe: " + request.getEmail());
        }
        
        // Validar nombre de usuario único
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("El nombre de usuario ya existe: " + request.getUsername());
        }
        
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // Encriptar contraseña con BCrypt
        user.setName(request.getName());
        user.setApellido(request.getApellido());
        
        // Mapear dirección si viene en el request
        if (request.getDireccion() != null) {
            DireccionRequest dirReq = request.getDireccion();
            Direccion direccion = new Direccion(
                dirReq.getCalle(),
                dirReq.getCiudad(),
                dirReq.getCp(),
                dirReq.getPais()
            );
            user.setDireccion(direccion);
        }
        User savedUser = userRepository.save(user);
        return mapToDTO(savedUser);
    }
    
    public UserDTO updateUser(Long id, UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        
        // Validar email único (si se cambió)
        if (!user.getEmail().equals(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("El correo ya existe: " + request.getEmail());
        }
        
        // Validar nombre de usuario único (si se cambió)
        if (!user.getUsernameField().equals(request.getUsername()) && userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("El nombre de usuario ya existe: " + request.getUsername());
        }
        
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // Encriptar contraseña al actualizar
        user.setName(request.getName());
        user.setApellido(request.getApellido());
        // Mapear dirección si viene en el request
        if (request.getDireccion() != null) {
            DireccionRequest dirReq = request.getDireccion();
            Direccion direccion = new Direccion(
                dirReq.getCalle(),
                dirReq.getCiudad(),
                dirReq.getCp(),
                dirReq.getPais()
            );
            user.setDireccion(direccion);
        }
        
        User updatedUser = userRepository.save(user);
        return mapToDTO(updatedUser);
    }
    
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }
    
    private UserDTO mapToDTO(User user) {
        DireccionDTO direccionDTO = null;
        if (user.getDireccion() != null) {
            Direccion dir = user.getDireccion();
            direccionDTO = new DireccionDTO(
                dir.getCalle(),
                dir.getCiudad(),
                dir.getCp(),
                dir.getPais()
            );
        }
        return new UserDTO(
                user.getId(),
                user.getUsernameField(),
                user.getEmail(),
                user.getName(),
                user.getApellido(),
                user.getRole(),
                user.getCreatedAt(),
                direccionDTO
        );
    }
}
