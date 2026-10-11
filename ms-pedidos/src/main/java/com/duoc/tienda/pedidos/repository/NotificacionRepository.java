package com.duoc.tienda.pedidos.repository;

import com.duoc.tienda.pedidos.model.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
    List<Notificacion> findByPedidoId(Long pedidoId);
}
