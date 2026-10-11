package com.duoc.tienda.ventas.repository;

import com.duoc.tienda.ventas.model.DetalleVenta;
import com.duoc.tienda.ventas.model.dto.ProductoMasVendidoDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {

    @Query("SELECT new com.duoc.tienda.ventas.model.dto.ProductoMasVendidoDTO(d.productoId, d.nombreProducto, SUM(d.cantidad), SUM(d.montoLinea)) " +
           "FROM DetalleVenta d JOIN d.venta v " +
           "WHERE v.estado = com.duoc.tienda.ventas.model.EstadoVenta.APROBADO " +
           "GROUP BY d.productoId, d.nombreProducto " +
           "ORDER BY SUM(d.cantidad) DESC")
    List<ProductoMasVendidoDTO> findMasVendidos(Pageable pageable);
}
