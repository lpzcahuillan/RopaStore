package com.duoc.tienda.ventas.pago;

import com.duoc.tienda.ventas.model.MetodoPago;
import com.duoc.tienda.ventas.model.dto.SolicitudPagoDTO;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class PagoTarjetaCreditoStrategy implements EstrategiaPago {

    @Override
    public MetodoPago getMetodoPago() {
        return MetodoPago.TARJETA_CREDITO;
    }

    @Override
    public ResultadoPago procesar(SolicitudPagoDTO solicitud, BigDecimal monto) {
        String numTarjeta = solicitud.getNumeroTarjeta();

        if (numTarjeta == null || numTarjeta.isBlank()) {
            throw new IllegalArgumentException("El número de tarjeta de crédito es obligatorio");
        }

        String limpio = numTarjeta.replaceAll("[\\s-]+", "");

        // Validar que sean entre 13 y 19 dígitos numéricos
        if (!limpio.matches("^\\d{13,19}$")) {
            throw new IllegalArgumentException("Formato de tarjeta de crédito inválido: debe contener entre 13 y 19 dígitos numéricos");
        }

        // Rechaza los que terminan en "0000"
        if (limpio.endsWith("0000")) {
            return ResultadoPago.builder()
                    .aprobado(false)
                    .motivoRechazo("Tarjeta rechazada por la entidad emisora (terminada en 0000)")
                    .build();
        }

        // Guardar solo los últimos 4 dígitos
        String ultimos4 = limpio.substring(limpio.length() - 4);
        String referencia = "TX-TC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase() + " (**** " + ultimos4 + ")";

        return ResultadoPago.builder()
                .aprobado(true)
                .referenciaPago(referencia)
                .build();
    }
}
