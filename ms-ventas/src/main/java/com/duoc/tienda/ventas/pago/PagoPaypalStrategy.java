package com.duoc.tienda.ventas.pago;

import com.duoc.tienda.ventas.model.MetodoPago;
import com.duoc.tienda.ventas.model.dto.SolicitudPagoDTO;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class PagoPaypalStrategy implements EstrategiaPago {

    @Override
    public MetodoPago getMetodoPago() {
        return MetodoPago.PAYPAL;
    }

    @Override
    public ResultadoPago procesar(SolicitudPagoDTO solicitud, BigDecimal monto) {
        String email = solicitud.getEmailPaypal();

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("El email de la cuenta PayPal es obligatorio");
        }

        if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new IllegalArgumentException("Formato de email PayPal inválido");
        }

        String referencia = "TX-PP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase() + " (" + email + ")";

        return ResultadoPago.builder()
                .aprobado(true)
                .referenciaPago(referencia)
                .build();
    }
}
