package com.duoc.tienda.ventas.pago;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResultadoPago {
    private boolean aprobado;
    private String referenciaPago;
    private String motivoRechazo;
}
