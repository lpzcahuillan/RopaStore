package com.duoc.tienda.ventas.repository;

import com.duoc.tienda.ventas.model.EstadoVenta;
import com.duoc.tienda.ventas.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {
    List<Venta> findByEstado(EstadoVenta estado);
    List<Venta> findByEstadoAndFechaBetween(EstadoVenta estado, LocalDateTime desde, LocalDateTime hasta);
}
