package com.duoc.tienda.ventas.model.dto;

import com.duoc.tienda.ventas.model.MetodoPago;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitudPagoDTO {

    @NotNull(message = "El ID del pedido es obligatorio")
    private Long pedidoId;

    @NotNull(message = "El método de pago es obligatorio (TARJETA_CREDITO o PAYPAL)")
    private MetodoPago metodoPago;

    private String numeroTarjeta;

    private String emailPaypal;
}
