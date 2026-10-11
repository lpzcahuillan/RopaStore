package com.duoc.tienda.pedidos.client;

import com.duoc.tienda.pedidos.exception.ServicioExternoException;
import com.duoc.tienda.pedidos.model.dto.ExisteClienteDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

@Component
public class UsuarioClient {

    private final RestClient restClient;

    public UsuarioClient(
            RestClient.Builder builder,
            @Value("${microservicio.usuarios.url}") String baseUrl,
            @Value("${microservicio.timeout.connect-ms:3000}") int connectTimeoutMs,
            @Value("${microservicio.timeout.read-ms:5000}") int readTimeoutMs
    ) {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(connectTimeoutMs))
                .build();
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(Duration.ofMillis(readTimeoutMs));

        this.restClient = builder
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .build();
    }

    public boolean existeCliente(Long clienteId) {
        try {
            ExisteClienteDTO response = restClient.get()
                    .uri("/api/clientes/{id}/existe", clienteId)
                    .retrieve()
                    .body(ExisteClienteDTO.class);
            return response != null && response.isExiste();
        } catch (HttpClientErrorException.NotFound e) {
            return false;
        } catch (ResourceAccessException e) {
            throw new ServicioExternoException("Servicio ms-usuarios no responde (tiempo de espera o fuera de línea): " + e.getMessage());
        } catch (HttpServerErrorException e) {
            throw new ServicioExternoException("Servicio ms-usuarios no disponible (código " + e.getStatusCode() + ")");
        } catch (Exception e) {
            throw new ServicioExternoException("Error al comunicarse con ms-usuarios: " + e.getMessage());
        }
    }
}
