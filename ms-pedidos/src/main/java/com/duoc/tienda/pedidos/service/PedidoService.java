package com.duoc.tienda.pedidos.service;

import com.duoc.tienda.pedidos.client.InventarioClient;
import com.duoc.tienda.pedidos.client.UsuarioClient;
import com.duoc.tienda.pedidos.exception.RecursoNoEncontradoException;
import com.duoc.tienda.pedidos.model.DetallePedido;
import com.duoc.tienda.pedidos.model.EstadoPedido;
import com.duoc.tienda.pedidos.model.Notificacion;
import com.duoc.tienda.pedidos.model.Pedido;
import com.duoc.tienda.pedidos.model.dto.CrearPedidoRequest;
import com.duoc.tienda.pedidos.model.dto.ItemPedidoDTO;
import com.duoc.tienda.pedidos.model.dto.ProductoDTO;
import com.duoc.tienda.pedidos.repository.NotificacionRepository;
import com.duoc.tienda.pedidos.repository.PedidoRepository;
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
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final NotificacionRepository notificacionRepository;
    private final InventarioClient inventarioClient;
    private final UsuarioClient usuarioClient;

    @Transactional
    public Pedido crearPedido(CrearPedidoRequest request) {
        // 1. Validar que el cliente exista
        boolean existeCliente = usuarioClient.existeCliente(request.getClienteId());
        if (!existeCliente) {
            throw new RecursoNoEncontradoException("Cliente con ID " + request.getClienteId() + " no encontrado en el sistema de usuarios");
        }

        // 2. Procesar ítems y descontar stock
        List<ItemPedidoDTO> itemsDescontados = new ArrayList<>();
        List<DetallePedido> detalles = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        try {
            for (ItemPedidoDTO item : request.getItems()) {
                ProductoDTO producto = inventarioClient.obtenerProducto(item.getProductoId());
                inventarioClient.ajustarStock(item.getProductoId(), -item.getCantidad());
                itemsDescontados.add(item);

                BigDecimal subtotal = producto.getPrecio().multiply(BigDecimal.valueOf(item.getCantidad()));
                total = total.add(subtotal);

                DetallePedido detalle = DetallePedido.builder()
                        .productoId(producto.getId())
                        .nombreProducto(producto.getNombre())
                        .cantidad(item.getCantidad())
                        .precioUnitario(producto.getPrecio())
                        .build();

                detalles.add(detalle);
            }
        } catch (RuntimeException e) {
            log.warn("Fallo al procesar ítems del pedido. Iniciando reposición de stock para los ítems ya descontados...");
            for (ItemPedidoDTO revertir : itemsDescontados) {
                try {
                    inventarioClient.ajustarStock(revertir.getProductoId(), revertir.getCantidad());
                } catch (Exception exRevert) {
                    log.error("Error crítico al reponer stock del producto ID {}: {}", revertir.getProductoId(), exRevert.getMessage());
                }
            }
            throw e;
        }

        // 3. Crear y guardar el Pedido
        Pedido pedido = Pedido.builder()
                .clienteId(request.getClienteId())
                .fecha(LocalDateTime.now())
                .estado(EstadoPedido.CREADO)
                .total(total)
                .build();

        for (DetallePedido detalle : detalles) {
            detalle.setPedido(pedido);
        }
        pedido.setDetalles(detalles);

        Pedido guardado = pedidoRepository.save(pedido);

        // 4. Crear Notificación
        Notificacion notificacion = Notificacion.builder()
                .pedidoId(guardado.getId())
                .clienteId(guardado.getClienteId())
                .mensaje("Pedido #" + guardado.getId() + " creado exitosamente en estado " + guardado.getEstado())
                .fecha(LocalDateTime.now())
                .build();
        notificacionRepository.save(notificacion);

        return guardado;
    }

    @Transactional
    public Pedido cambiarEstado(Long id, String nuevoEstadoStr) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado con ID: " + id));

        EstadoPedido nuevoEstado;
        try {
            nuevoEstado = EstadoPedido.valueOf(nuevoEstadoStr.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Estado no válido: '" + nuevoEstadoStr + "'. Estados permitidos: CREADO, PAGADO, ENVIADO, ENTREGADO, CANCELADO");
        }

        EstadoPedido actual = pedido.getEstado();

        // Validar transiciones permitidas:
        // CREADO -> PAGADO o CANCELADO
        // PAGADO -> ENVIADO
        // ENVIADO -> ENTREGADO
        boolean transicionValida = switch (actual) {
            case CREADO -> (nuevoEstado == EstadoPedido.PAGADO || nuevoEstado == EstadoPedido.CANCELADO);
            case PAGADO -> (nuevoEstado == EstadoPedido.ENVIADO);
            case ENVIADO -> (nuevoEstado == EstadoPedido.ENTREGADO);
            default -> false;
        };

        if (!transicionValida) {
            throw new IllegalArgumentException("Transición de estado inválida: no se permite cambiar de " + actual + " a " + nuevoEstado);
        }

        // Si se cancela, reponer stock
        if (nuevoEstado == EstadoPedido.CANCELADO) {
            log.info("Cancelando pedido #{}. Reponiendo stock en inventario...", pedido.getId());
            for (DetallePedido d : pedido.getDetalles()) {
                try {
                    inventarioClient.ajustarStock(d.getProductoId(), d.getCantidad());
                } catch (Exception e) {
                    log.error("Error al reponer stock del producto ID {} al cancelar pedido: {}", d.getProductoId(), e.getMessage());
                }
            }
        }

        pedido.setEstado(nuevoEstado);
        Pedido actualizado = pedidoRepository.save(pedido);

        // Notificación de cambio de estado
        Notificacion notificacion = Notificacion.builder()
                .pedidoId(pedido.getId())
                .clienteId(pedido.getClienteId())
                .mensaje("El pedido #" + pedido.getId() + " ha cambiado su estado a " + nuevoEstado)
                .fecha(LocalDateTime.now())
                .build();
        notificacionRepository.save(notificacion);

        return actualizado;
    }

    public List<Pedido> listarTodos() {
        return pedidoRepository.findAll();
    }

    public Pedido obtenerPorId(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado con ID: " + id));
    }

    public List<Pedido> listarPorCliente(Long clienteId) {
        return pedidoRepository.findByClienteId(clienteId);
    }

    public List<Notificacion> listarNotificacionesPorPedido(Long pedidoId) {
        return notificacionRepository.findByPedidoId(pedidoId);
    }
}
