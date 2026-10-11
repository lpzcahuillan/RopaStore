package com.duoc.tienda.pedidos.model.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private String talla;
    private String color;
    private BigDecimal precio;
    private Integer stock;
}
