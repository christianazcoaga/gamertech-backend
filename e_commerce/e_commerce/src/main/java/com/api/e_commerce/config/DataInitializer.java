package com.api.e_commerce.config;

import com.api.e_commerce.model.Category;
import com.api.e_commerce.model.Role;
import com.api.e_commerce.model.User;
import com.api.e_commerce.repository.CategoryRepository;
import com.api.e_commerce.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(CategoryRepository categoryRepository, 
                                   UserRepository userRepository,
                                   PasswordEncoder passwordEncoder) {
        return args -> {
            // Insertar categorías si no existen
            if (categoryRepository.count() == 0) {
                System.out.println("🔄 Insertando categorías iniciales...");
                
                categoryRepository.save(new Category("Procesadores", "CPUs y procesadores para PC"));
                categoryRepository.save(new Category("Tarjetas Gráficas", "GPUs y tarjetas de video"));
                categoryRepository.save(new Category("Placas Madre", "Motherboards y placas base"));
                categoryRepository.save(new Category("Memoria RAM", "Módulos de memoria RAM"));
                categoryRepository.save(new Category("Almacenamiento", "Discos duros, SSD y NVMe"));
                categoryRepository.save(new Category("Periféricos", "Teclados, mouse, auriculares"));
                categoryRepository.save(new Category("Monitores", "Pantallas y monitores gaming"));
                categoryRepository.save(new Category("Gabinetes", "Cases y gabinetes para PC"));
                
                System.out.println("✅ Categorías insertadas correctamente");
            }
            
            // Crear usuarios de prueba si no existen
            if (userRepository.count() == 0) {
                System.out.println("🔄 Creando usuarios de prueba...");
                
                // Usuario ADMIN
                User admin = new User();
                admin.setUsername("admin");
                admin.setEmail("admin@gamertech.com");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole(Role.ADMIN);
                admin.setName("Admin");
                admin.setApellido("GamerTech");
                userRepository.save(admin);
                
                // Usuario USER
                User user = new User();
                user.setUsername("usuario");
                user.setEmail("usuario@gamertech.com");
                user.setPassword(passwordEncoder.encode("user123"));
                user.setRole(Role.USER);
                user.setName("Usuario");
                user.setApellido("Demo");
                userRepository.save(user);
                
                System.out.println("✅ Usuarios de prueba creados:");
                System.out.println("   👤 ADMIN - Email: admin@gamertech.com, Password: admin123");
                System.out.println("   👤 USER  - Email: usuario@gamertech.com, Password: user123");
            }
            
            System.out.println("🚀 Base de datos H2 inicializada correctamente");
        };
    }
}
