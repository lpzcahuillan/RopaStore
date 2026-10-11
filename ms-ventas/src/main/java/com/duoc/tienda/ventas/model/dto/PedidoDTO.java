package com.duoc.tienda.ventas.model.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoDTO {
    private Long id;
    private Long clienteId;
    private String estado;
    private BigDecimal total;
    private List<DetallePedidoDTO> detalles;
}
