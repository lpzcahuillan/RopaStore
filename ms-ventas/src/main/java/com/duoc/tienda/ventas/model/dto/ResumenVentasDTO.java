package com.duoc.tienda.ventas.model.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumenVentasDTO {
    private BigDecimal totalVendido;
    private long totalVentas;
    private BigDecimal ticketPromedio;
}
