package com.duoc.tienda.ventas;

import com.duoc.tienda.ventas.model.DetalleVenta;
import com.duoc.tienda.ventas.model.EstadoVenta;
import com.duoc.tienda.ventas.model.MetodoPago;
import com.duoc.tienda.ventas.model.Venta;
import com.duoc.tienda.ventas.repository.VentaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@Slf4j
public class VentasApplication {

    public static void main(String[] args) {
        SpringApplication.run(VentasApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData(VentaRepository ventaRepository) {
        return args -> {
            if (ventaRepository.count() == 0) {
                List<Venta> ventasIniciales = new ArrayList<>();
                LocalDateTime baseDate = LocalDateTime.now();

                // 15 Ventas históricas APROBADO repartidas en las últimas semanas
                Object[][] datosVentas = {
                        // pedidoId, clienteId, diasAtras, metodoPago, ultimos4, prodId, prodNombre, cant, precioUnit
                        {101L, 3L, 25, MetodoPago.TARJETA_CREDITO, "1234", 1L, "Camisa Oxford Formal", 2, "24990"},
                        {102L, 4L, 23, MetodoPago.PAYPAL, "juan@gmail.com", 2L, "Polera Algodón Básica", 3, "12990"},
                        {103L, 3L, 21, MetodoPago.TARJETA_CREDITO, "5678", 3L, "Jeans Slim Fit Denim", 1, "34990"},
                        {104L, 4L, 19, MetodoPago.TARJETA_CREDITO, "9012", 5L, "Chaqueta Biker de Cuero", 1, "69990"},
                        {105L, 3L, 17, MetodoPago.PAYPAL, "maria@gmail.com", 2L, "Polera Algodón Básica", 4, "12990"},
                        {106L, 4L, 15, MetodoPago.TARJETA_CREDITO, "4321", 4L, "Pantalón Chino Casual", 2, "29990"},
                        {107L, 3L, 14, MetodoPago.TARJETA_CREDITO, "8765", 6L, "Cortavientos Deportivo", 2, "27990"},
                        {108L, 4L, 12, MetodoPago.PAYPAL, "carlos@gmail.com", 1L, "Camisa Oxford Formal", 3, "24990"},
                        {109L, 3L, 10, MetodoPago.TARJETA_CREDITO, "2109", 2L, "Polera Algodón Básica", 5, "12990"},
                        {110L, 4L, 8, MetodoPago.TARJETA_CREDITO, "6543", 3L, "Jeans Slim Fit Denim", 2, "34990"},
                        {111L, 3L, 7, MetodoPago.PAYPAL, "ana@gmail.com", 4L, "Pantalón Chino Casual", 1, "29990"},
                        {112L, 4L, 5, MetodoPago.TARJETA_CREDITO, "1122", 6L, "Cortavientos Deportivo", 1, "27990"},
                        {113L, 3L, 4, MetodoPago.TARJETA_CREDITO, "3344", 1L, "Camisa Oxford Formal", 1, "24990"},
                        {114L, 4L, 2, MetodoPago.PAYPAL, "pedro@gmail.com", 2L, "Polera Algodón Básica", 2, "12990"},
                        {115L, 3L, 1, MetodoPago.TARJETA_CREDITO, "5566", 5L, "Chaqueta Biker de Cuero", 1, "69990"}
                };

                for (Object[] d : datosVentas) {
                    Long pedidoId = (Long) d[0];
                    Long clienteId = (Long) d[1];
                    int diasAtras = (Integer) d[2];
                    MetodoPago metodo = (MetodoPago) d[3];
                    String extra = (String) d[4];
                    Long prodId = (Long) d[5];
                    String prodNombre = (String) d[6];
                    int cant = (Integer) d[7];
                    BigDecimal precioUnit = new BigDecimal((String) d[8]);
                    BigDecimal montoTotal = precioUnit.multiply(BigDecimal.valueOf(cant));

                    String ref = metodo == MetodoPago.TARJETA_CREDITO
                            ? "TX-TC-HIST-" + pedidoId + " (**** " + extra + ")"
                            : "TX-PP-HIST-" + pedidoId + " (" + extra + ")";

                    Venta venta = Venta.builder()
                            .pedidoId(pedidoId)
                            .clienteId(clienteId)
                            .monto(montoTotal)
                            .metodoPago(metodo)
                            .estado(EstadoVenta.APROBADO)
                            .fecha(baseDate.minusDays(diasAtras))
                            .referenciaPago(ref)
                            .build();

                    DetalleVenta detalle = DetalleVenta.builder()
                            .venta(venta)
                            .productoId(prodId)
                            .nombreProducto(prodNombre)
                            .cantidad(cant)
                            .montoLinea(montoTotal)
                            .build();

                    venta.setDetalles(new ArrayList<>(List.of(detalle)));
                    ventasIniciales.add(venta);
                }

                ventaRepository.saveAll(ventasIniciales);
                log.info("ms-ventas: 15 ventas históricas APROBADO inicializadas correctamente.");
            }
        };
    }
}
