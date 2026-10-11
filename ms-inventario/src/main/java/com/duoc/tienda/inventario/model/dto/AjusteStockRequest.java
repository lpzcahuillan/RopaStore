package com.duoc.tienda.inventario.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AjusteStockRequest {

    @NotNull(message = "La cantidad a ajustar es obligatoria (positiva suma, negativa resta)")
    private Integer cantidad;
}
