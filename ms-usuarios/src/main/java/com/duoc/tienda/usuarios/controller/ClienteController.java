package com.duoc.tienda.usuarios.controller;

import com.duoc.tienda.usuarios.model.dto.ExisteResponse;
import com.duoc.tienda.usuarios.model.dto.UsuarioDTO;
import com.duoc.tienda.usuarios.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listarClientes() {
        return ResponseEntity.ok(clienteService.listarClientes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.obtenerPorId(id));
    }

    @GetMapping("/{id}/existe")
    public ResponseEntity<ExisteResponse> existeCliente(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.verificarExiste(id));
    }
}
