package com.duoc.tienda.ventas.service;

import com.duoc.tienda.ventas.model.EstadoVenta;
import com.duoc.tienda.ventas.model.Venta;
import com.duoc.tienda.ventas.model.dto.ProductoMasVendidoDTO;
import com.duoc.tienda.ventas.model.dto.ResumenVentasDTO;
import com.duoc.tienda.ventas.model.dto.TendenciaVentasDTO;
import com.duoc.tienda.ventas.repository.DetalleVentaRepository;
import com.duoc.tienda.ventas.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InformeService {

    private final DetalleVentaRepository detalleVentaRepository;
    private final VentaRepository ventaRepository;

    public List<ProductoMasVendidoDTO> obtenerMasVendidos(int limite) {
        int tamano = limite > 0 ? limite : 5;
        return detalleVentaRepository.findMasVendidos(PageRequest.of(0, tamano));
    }

    public List<TendenciaVentasDTO> obtenerTendencias(LocalDate desde, LocalDate hasta) {
        LocalDate fechaInicio = (desde != null) ? desde : LocalDate.now().minusDays(30);
        LocalDate fechaFin = (hasta != null) ? hasta : LocalDate.now();

        List<Venta> ventas = ventaRepository.findByEstadoAndFechaBetween(
                EstadoVenta.APROBADO,
                fechaInicio.atStartOfDay(),
                fechaFin.atTime(LocalTime.MAX)
        );

        Map<LocalDate, List<Venta>> agrupadasPorDia = ventas.stream()
                .collect(Collectors.groupingBy(v -> v.getFecha().toLocalDate()));

        return agrupadasPorDia.entrySet().stream()
                .map(entry -> {
                    LocalDate fecha = entry.getKey();
                    List<Venta> ventasDia = entry.getValue();
                    long cantidad = ventasDia.size();
                    BigDecimal totalDia = ventasDia.stream()
                            .map(Venta::getMonto)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    return TendenciaVentasDTO.builder()
                            .fecha(fecha)
                            .cantidadVentas(cantidad)
                            .montoTotal(totalDia)
                            .build();
                })
                .sorted(Comparator.comparing(TendenciaVentasDTO::getFecha))
                .collect(Collectors.toList());
    }

    public ResumenVentasDTO obtenerResumen() {
        List<Venta> ventasAprobadas = ventaRepository.findByEstado(EstadoVenta.APROBADO);

        long totalVentas = ventasAprobadas.size();
        BigDecimal totalVendido = ventasAprobadas.stream()
                .map(Venta::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal ticketPromedio = totalVentas > 0
                ? totalVendido.divide(BigDecimal.valueOf(totalVentas), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        return ResumenVentasDTO.builder()
                .totalVendido(totalVendido)
                .totalVentas(totalVentas)
                .ticketPromedio(ticketPromedio)
                .build();
    }
}
