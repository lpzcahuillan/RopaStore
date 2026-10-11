package com.duoc.tienda.usuarios.model.dto;

import com.duoc.tienda.usuarios.model.Rol;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioDTO {
    private Long id;
    private String nombre;
    private String email;
    private Rol rol;
    private boolean activo;
}
