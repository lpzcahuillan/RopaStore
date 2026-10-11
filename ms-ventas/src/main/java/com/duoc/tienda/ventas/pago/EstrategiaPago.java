package com.duoc.tienda.ventas.pago;

import com.duoc.tienda.ventas.model.MetodoPago;
import com.duoc.tienda.ventas.model.dto.SolicitudPagoDTO;

import java.math.BigDecimal;

public interface EstrategiaPago {
    MetodoPago getMetodoPago();
    ResultadoPago procesar(SolicitudPagoDTO solicitud, BigDecimal monto);
}
