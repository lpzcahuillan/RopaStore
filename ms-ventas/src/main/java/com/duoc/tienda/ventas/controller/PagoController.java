package com.duoc.tienda.ventas.controller;

import com.duoc.tienda.ventas.model.Venta;
import com.duoc.tienda.ventas.model.dto.SolicitudPagoDTO;
import com.duoc.tienda.ventas.service.PagoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
public class PagoController {

    private final PagoService pagoService;

    @PostMapping
    public ResponseEntity<Venta> procesarPago(@Valid @RequestBody SolicitudPagoDTO solicitud) {
        Venta venta = pagoService.procesarPago(solicitud);
        return ResponseEntity.status(HttpStatus.CREATED).body(venta);
    }
}
