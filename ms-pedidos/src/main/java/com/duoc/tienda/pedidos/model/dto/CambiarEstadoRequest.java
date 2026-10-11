package com.duoc.tienda.pedidos.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CambiarEstadoRequest {

    @NotBlank(message = "El nuevo estado es obligatorio")
    private String estado;
}
