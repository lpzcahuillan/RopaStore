package com.duoc.tienda.pedidos.controller;

import com.duoc.tienda.pedidos.model.Notificacion;
import com.duoc.tienda.pedidos.model.Pedido;
import com.duoc.tienda.pedidos.model.dto.CambiarEstadoRequest;
import com.duoc.tienda.pedidos.model.dto.CrearPedidoRequest;
import com.duoc.tienda.pedidos.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    @PostMapping
    public ResponseEntity<Pedido> crearPedido(@Valid @RequestBody CrearPedidoRequest request) {
        Pedido creado = pedidoService.crearPedido(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @GetMapping
    public ResponseEntity<List<Pedido>> listarTodos() {
        return ResponseEntity.ok(pedidoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.obtenerPorId(id));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Pedido>> listarPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(pedidoService.listarPorCliente(clienteId));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<Pedido> cambiarEstado(@PathVariable Long id, @Valid @RequestBody CambiarEstadoRequest request) {
        Pedido actualizado = pedidoService.cambiarEstado(id, request.getEstado());
        return ResponseEntity.ok(actualizado);
    }

    @GetMapping("/{id}/notificaciones")
    public ResponseEntity<List<Notificacion>> listarNotificaciones(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.listarNotificacionesPorPedido(id));
    }
}
