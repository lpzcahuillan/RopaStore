package com.duoc.tienda.ventas.pago;

import com.duoc.tienda.ventas.model.MetodoPago;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class FabricaEstrategiaPago {

    private final Map<MetodoPago, EstrategiaPago> estrategias;

    public FabricaEstrategiaPago(List<EstrategiaPago> listaEstrategias) {
        this.estrategias = listaEstrategias.stream()
                .collect(Collectors.toMap(EstrategiaPago::getMetodoPago, e -> e));
    }

    public EstrategiaPago obtenerEstrategia(MetodoPago metodoPago) {
        EstrategiaPago estrategia = estrategias.get(metodoPago);
        if (estrategia == null) {
            throw new IllegalArgumentException("Método de pago no soportado: " + metodoPago);
        }
        return estrategia;
    }
}
