package com.duoc.tienda.pedidos.client;

import com.duoc.tienda.pedidos.exception.ConflictoNegocioException;
import com.duoc.tienda.pedidos.exception.RecursoNoEncontradoException;
import com.duoc.tienda.pedidos.exception.ServicioExternoException;
import com.duoc.tienda.pedidos.model.dto.AjusteStockDTO;
import com.duoc.tienda.pedidos.model.dto.ProductoDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

@Component
public class InventarioClient {

    private final RestClient restClient;

    public InventarioClient(RestClient.Builder builder, @Value("${microservicio.inventario.url}") String baseUrl) {
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    public ProductoDTO obtenerProducto(Long productoId) {
        try {
            return restClient.get()
                    .uri("/api/productos/{id}", productoId)
                    .retrieve()
                    .body(ProductoDTO.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new RecursoNoEncontradoException("Producto con ID " + productoId + " no existe en ms-inventario");
        } catch (ResourceAccessException e) {
            throw new ServicioExternoException("Servicio ms-inventario no responde (tiempo de espera o fuera de línea): " + e.getMessage());
        } catch (HttpServerErrorException e) {
            throw new ServicioExternoException("Servicio ms-inventario no disponible (código " + e.getStatusCode() + ")");
        } catch (Exception e) {
            throw new ServicioExternoException("Error al comunicarse con ms-inventario: " + e.getMessage());
        }
    }

    public void ajustarStock(Long productoId, int cantidad) {
        try {
            restClient.patch()
                    .uri("/api/productos/{id}/stock", productoId)
                    .body(new AjusteStockDTO(cantidad))
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.NotFound e) {
            throw new RecursoNoEncontradoException("Producto con ID " + productoId + " no existe en ms-inventario");
        } catch (HttpClientErrorException.Conflict e) {
            throw new ConflictoNegocioException("Stock insuficiente en inventario para el producto ID " + productoId);
        } catch (ResourceAccessException e) {
            throw new ServicioExternoException("Servicio ms-inventario no responde (tiempo de espera o fuera de línea): " + e.getMessage());
        } catch (HttpServerErrorException e) {
            throw new ServicioExternoException("Servicio ms-inventario no disponible (código " + e.getStatusCode() + ")");
        } catch (Exception e) {
            throw new ServicioExternoException("Error al ajustar stock en ms-inventario: " + e.getMessage());
        }
    }
}
