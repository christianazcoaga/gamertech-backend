package com.api.e_commerce;

import com.api.e_commerce.dto.DireccionRequest;
import com.api.e_commerce.dto.UserDTO;
import com.api.e_commerce.dto.UserRequest;
import com.api.e_commerce.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class DireccionIntegrationTest {

    @Autowired
    private UserService userService;

    @Test
    void crearUsuarioConDireccion() {
        DireccionRequest direccion = new DireccionRequest("Calle Falsa 123", "Springfield", "1234", "Argentina");
        UserRequest request = new UserRequest("usuario1", "usuario1@email.com", "password123", "Homero", "Simpson", direccion);
        UserDTO userDTO = userService.createUser(request);
        Assertions.assertNotNull(userDTO.getDireccion());
        Assertions.assertEquals("Calle Falsa 123", userDTO.getDireccion().getCalle());
        Assertions.assertEquals("Springfield", userDTO.getDireccion().getCiudad());
        Assertions.assertEquals("1234", userDTO.getDireccion().getCp());
        Assertions.assertEquals("Argentina", userDTO.getDireccion().getPais());
    }
}
