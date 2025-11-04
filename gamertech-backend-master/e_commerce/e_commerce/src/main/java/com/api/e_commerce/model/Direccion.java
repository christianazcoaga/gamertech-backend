package com.api.e_commerce.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class Direccion {
    private String calle;
    private String ciudad;
    private String cp;
    private String pais;
}
