package com.duoc.tienda.ventas.model.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoMasVendidoDTO {
    private Long productoId;
    private String nombreProducto;
    private Long cantidadTotal;
    private BigDecimal montoTotal;
}
