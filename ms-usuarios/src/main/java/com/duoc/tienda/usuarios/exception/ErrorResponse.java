package com.duoc.tienda.usuarios.exception;

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
