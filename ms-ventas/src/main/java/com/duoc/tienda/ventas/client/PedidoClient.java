package com.duoc.tienda.ventas.client;

import com.duoc.tienda.ventas.exception.ConflictoNegocioException;
import com.duoc.tienda.ventas.exception.RecursoNoEncontradoException;
import com.duoc.tienda.ventas.exception.ServicioExternoException;
import com.duoc.tienda.ventas.model.dto.CambiarEstadoPedidoDTO;
import com.duoc.tienda.ventas.model.dto.PedidoDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

@Component
public class PedidoClient {

    private final RestClient restClient;

    public PedidoClient(RestClient.Builder builder, @Value("${microservicio.pedidos.url}") String baseUrl) {
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    public PedidoDTO obtenerPedido(Long pedidoId) {
        try {
            return restClient.get()
                    .uri("/api/pedidos/{id}", pedidoId)
                    .retrieve()
                    .body(PedidoDTO.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new RecursoNoEncontradoException("Pedido con ID " + pedidoId + " no encontrado en ms-pedidos");
        } catch (ResourceAccessException e) {
            throw new ServicioExternoException("Servicio ms-pedidos no responde (tiempo de espera o fuera de línea): " + e.getMessage());
        } catch (HttpServerErrorException e) {
            throw new ServicioExternoException("Servicio ms-pedidos no disponible (código " + e.getStatusCode() + ")");
        } catch (Exception e) {
            throw new ServicioExternoException("Error al comunicarse con ms-pedidos: " + e.getMessage());
        }
    }

    public void cambiarEstado(Long pedidoId, String nuevoEstado) {
        try {
            restClient.patch()
                    .uri("/api/pedidos/{id}/estado", pedidoId)
                    .body(new CambiarEstadoPedidoDTO(nuevoEstado))
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.NotFound e) {
            throw new RecursoNoEncontradoException("Pedido con ID " + pedidoId + " no encontrado en ms-pedidos");
        } catch (HttpClientErrorException.Conflict e) {
            throw new ConflictoNegocioException("Conflicto al actualizar estado en ms-pedidos para pedido ID " + pedidoId);
        } catch (HttpClientErrorException.BadRequest e) {
            throw new IllegalArgumentException("Estado o transición no válida enviada a ms-pedidos: " + nuevoEstado);
        } catch (ResourceAccessException e) {
            throw new ServicioExternoException("Servicio ms-pedidos no responde (tiempo de espera o fuera de línea): " + e.getMessage());
        } catch (HttpServerErrorException e) {
            throw new ServicioExternoException("Servicio ms-pedidos no disponible (código " + e.getStatusCode() + ")");
        } catch (Exception e) {
            throw new ServicioExternoException("Error al actualizar estado en ms-pedidos: " + e.getMessage());
        }
    }
}
