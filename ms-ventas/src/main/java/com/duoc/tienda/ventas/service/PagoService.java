package com.duoc.tienda.ventas.service;

import com.duoc.tienda.ventas.client.PedidoClient;
import com.duoc.tienda.ventas.exception.ConflictoNegocioException;
import com.duoc.tienda.ventas.exception.PagoRechazadoException;
import com.duoc.tienda.ventas.model.DetalleVenta;
import com.duoc.tienda.ventas.model.EstadoVenta;
import com.duoc.tienda.ventas.model.Venta;
import com.duoc.tienda.ventas.model.dto.DetallePedidoDTO;
import com.duoc.tienda.ventas.model.dto.PedidoDTO;
import com.duoc.tienda.ventas.model.dto.SolicitudPagoDTO;
import com.duoc.tienda.ventas.pago.EstrategiaPago;
import com.duoc.tienda.ventas.pago.FabricaEstrategiaPago;
import com.duoc.tienda.ventas.pago.ResultadoPago;
import com.duoc.tienda.ventas.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PagoService {

    private final PedidoClient pedidoClient;
    private final FabricaEstrategiaPago fabricaEstrategiaPago;
    private final VentaRepository ventaRepository;

    @Transactional
    public Venta procesarPago(SolicitudPagoDTO solicitud) {
        // 1. Obtener pedido de ms-pedidos
        PedidoDTO pedido = pedidoClient.obtenerPedido(solicitud.getPedidoId());

        // 2. Verificar que esté CREADO (si no, 409)
        if (!"CREADO".equalsIgnoreCase(pedido.getEstado())) {
            throw new ConflictoNegocioException(
                    "El pedido #" + pedido.getId() + " no está disponible para pago. Estado actual: " + pedido.getEstado() + " (debe ser CREADO)"
            );
        }

        // 3. Ejecutar estrategia de pago
        EstrategiaPago estrategia = fabricaEstrategiaPago.obtenerEstrategia(solicitud.getMetodoPago());
        ResultadoPago resultado = estrategia.procesar(solicitud, pedido.getTotal());

        // 4. Si se aprueba
        if (resultado.isAprobado()) {
            Venta venta = Venta.builder()
                    .pedidoId(pedido.getId())
                    .clienteId(pedido.getClienteId())
                    .monto(pedido.getTotal())
                    .metodoPago(solicitud.getMetodoPago())
                    .estado(EstadoVenta.APROBADO)
                    .fecha(LocalDateTime.now())
                    .referenciaPago(resultado.getReferenciaPago())
                    .build();

            List<DetalleVenta> detalles = new ArrayList<>();
            if (pedido.getDetalles() != null) {
                for (DetallePedidoDTO dp : pedido.getDetalles()) {
                    BigDecimal montoLinea = dp.getPrecioUnitario().multiply(BigDecimal.valueOf(dp.getCantidad()));
                    DetalleVenta dv = DetalleVenta.builder()
                            .venta(venta)
                            .productoId(dp.getProductoId())
                            .nombreProducto(dp.getNombreProducto())
                            .cantidad(dp.getCantidad())
                            .montoLinea(montoLinea)
                            .build();
                    detalles.add(dv);
                }
            }
            venta.setDetalles(detalles);

            Venta guardada = ventaRepository.save(venta);

            // Cambiar pedido a PAGADO en ms-pedidos
            pedidoClient.cambiarEstado(pedido.getId(), "PAGADO");

            log.info("Pago procesado exitosamente para pedido #{}. Venta #{} generada.", pedido.getId(), guardada.getId());
            return guardada;
        } else {
            // 5. Si se rechaza
            Venta ventaRechazada = Venta.builder()
                    .pedidoId(pedido.getId())
                    .clienteId(pedido.getClienteId())
                    .monto(pedido.getTotal())
                    .metodoPago(solicitud.getMetodoPago())
                    .estado(EstadoVenta.RECHAZADO)
                    .fecha(LocalDateTime.now())
                    .referenciaPago("RECHAZADO: " + resultado.getMotivoRechazo())
                    .build();

            ventaRepository.save(ventaRechazada);

            log.warn("Pago rechazado para pedido #{}. Motivo: {}", pedido.getId(), resultado.getMotivoRechazo());

            // Responder 402 y no cambiar el pedido
            throw new PagoRechazadoException("Pago rechazado: " + resultado.getMotivoRechazo());
        }
    }
}
