package com.duoc.tienda.usuarios.service;

import com.duoc.tienda.usuarios.exception.RecursoNoEncontradoException;
import com.duoc.tienda.usuarios.model.Usuario;
import com.duoc.tienda.usuarios.model.dto.ExisteResponse;
import com.duoc.tienda.usuarios.model.dto.UsuarioDTO;
import com.duoc.tienda.usuarios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final UsuarioRepository usuarioRepository;

    public List<UsuarioDTO> listarClientes() {
        return usuarioRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public UsuarioDTO obtenerPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID: " + id));
        return mapToDTO(usuario);
    }

    public ExisteResponse verificarExiste(Long id) {
        boolean existe = usuarioRepository.existsById(id);
        return ExisteResponse.builder().existe(existe).build();
    }

    private UsuarioDTO mapToDTO(Usuario usuario) {
        return UsuarioDTO.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .rol(usuario.getRol())
                .activo(usuario.isActivo())
                .build();
    }
}
