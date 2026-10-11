package com.duoc.tienda.ventas.controller;

import com.duoc.tienda.ventas.model.dto.ProductoMasVendidoDTO;
import com.duoc.tienda.ventas.model.dto.ResumenVentasDTO;
import com.duoc.tienda.ventas.model.dto.TendenciaVentasDTO;
import com.duoc.tienda.ventas.service.InformeService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/informes")
@RequiredArgsConstructor
public class InformeController {

    private final InformeService informeService;

    @GetMapping("/mas-vendidos")
    public ResponseEntity<List<ProductoMasVendidoDTO>> obtenerMasVendidos(
            @RequestParam(defaultValue = "5") int limite
    ) {
        return ResponseEntity.ok(informeService.obtenerMasVendidos(limite));
    }

    @GetMapping("/tendencias")
    public ResponseEntity<List<TendenciaVentasDTO>> obtenerTendencias(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta
    ) {
        return ResponseEntity.ok(informeService.obtenerTendencias(desde, hasta));
    }

    @GetMapping("/resumen")
    public ResponseEntity<ResumenVentasDTO> obtenerResumen() {
        return ResponseEntity.ok(informeService.obtenerResumen());
    }
}
