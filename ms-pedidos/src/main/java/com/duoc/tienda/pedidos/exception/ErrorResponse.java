package com.duoc.tienda.pedidos.exception;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {
    private int estado;
    private String mensaje;
    private LocalDateTime fecha;
}
