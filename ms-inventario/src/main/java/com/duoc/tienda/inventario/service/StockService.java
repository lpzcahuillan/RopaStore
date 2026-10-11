package com.duoc.tienda.inventario.service;

import com.duoc.tienda.inventario.exception.RecursoNoEncontradoException;
import com.duoc.tienda.inventario.exception.StockInsuficienteException;
import com.duoc.tienda.inventario.model.Producto;
import com.duoc.tienda.inventario.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StockService {

    private final ProductoRepository productoRepository;

    @Transactional
    public Producto ajustarStock(Long productoId, int cantidad) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado con ID: " + productoId));

        int nuevoStock = producto.getStock() + cantidad;
        if (nuevoStock < 0) {
            throw new StockInsuficienteException(
                    "Stock insuficiente para el producto '" + producto.getNombre() + "' (ID: " + productoId + "). Stock disponible: " +
                            producto.getStock() + ", cantidad solicitada: " + Math.abs(cantidad)
            );
        }

        producto.setStock(nuevoStock);
        return productoRepository.save(producto);
    }
}
