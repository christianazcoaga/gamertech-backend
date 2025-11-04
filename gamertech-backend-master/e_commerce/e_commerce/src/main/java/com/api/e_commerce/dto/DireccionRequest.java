package com.api.e_commerce.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DireccionRequest {
    @NotBlank(message = "La calle es obligatoria")
    private String calle;
    @NotBlank(message = "La ciudad es obligatoria")
    private String ciudad;
    @NotBlank(message = "El código postal es obligatorio")
    private String cp;
    @NotBlank(message = "El país es obligatorio")
    private String pais;
}
