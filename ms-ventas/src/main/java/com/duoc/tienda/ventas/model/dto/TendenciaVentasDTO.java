package com.duoc.tienda.ventas.model.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TendenciaVentasDTO {
    private LocalDate fecha;
    private long cantidadVentas;
    private BigDecimal montoTotal;
}
