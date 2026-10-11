package com.duoc.tienda.usuarios.service;

import com.duoc.tienda.usuarios.exception.AutenticacionException;
import com.duoc.tienda.usuarios.exception.ConflictoNegocioException;
import com.duoc.tienda.usuarios.exception.RecursoNoEncontradoException;
import com.duoc.tienda.usuarios.model.Rol;
import com.duoc.tienda.usuarios.model.TokenRecuperacion;
import com.duoc.tienda.usuarios.model.Usuario;
import com.duoc.tienda.usuarios.model.dto.*;
import com.duoc.tienda.usuarios.repository.TokenRecuperacionRepository;
import com.duoc.tienda.usuarios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final TokenRecuperacionRepository tokenRecuperacionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public UsuarioDTO registro(RegistroRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new ConflictoNegocioException("El email '" + request.getEmail() + "' ya se encuentra registrado");
        }

        Usuario usuario = Usuario.builder()
                .nombre(request.getNombre())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .rol(Rol.CLIENTE)
                .activo(true)
                .build();

        Usuario guardado = usuarioRepository.save(usuario);
        return mapToDTO(guardado);
    }

    public LoginResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AutenticacionException("Credenciales inválidas: usuario o contraseña incorrectos"));

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPasswordHash())) {
            throw new AutenticacionException("Credenciales inválidas: usuario o contraseña incorrectos");
        }

        if (!usuario.isActivo()) {
            throw new AutenticacionException("La cuenta de usuario se encuentra inactiva");
        }

        String token = jwtService.generarToken(usuario);

        return LoginResponse.builder()
                .token(token)
                .tipo("Bearer")
                .rol(usuario.getRol().name())
                .build();
    }

    @Transactional
    public MensajeResponse recuperarPassword(RecuperarRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un usuario con el email: " + request.getEmail()));

        String tokenStr = UUID.randomUUID().toString();
        TokenRecuperacion tokenRecuperacion = TokenRecuperacion.builder()
                .token(tokenStr)
                .usuario(usuario)
                .expiracion(LocalDateTime.now().plusMinutes(30))
                .usado(false)
                .build();

        tokenRecuperacionRepository.save(tokenRecuperacion);

        return MensajeResponse.builder()
                .mensaje("Se ha generado el token de recuperación (correo simulado). Expira en 30 minutos.")
                .token(tokenStr)
                .build();
    }

    @Transactional
    public MensajeResponse restablecerPassword(RestablecerRequest request) {
        TokenRecuperacion token = tokenRecuperacionRepository.findByToken(request.getToken())
                .orElseThrow(() -> new IllegalArgumentException("Token de recuperación no válido o inexistente"));

        if (token.isUsado()) {
            throw new IllegalArgumentException("El token de recuperación ya ha sido utilizado");
        }

        if (token.getExpiracion().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("El token de recuperación ha expirado");
        }

        Usuario usuario = token.getUsuario();
        usuario.setPasswordHash(passwordEncoder.encode(request.getNuevaPassword()));
        usuarioRepository.save(usuario);

        token.setUsado(true);
        tokenRecuperacionRepository.save(token);

        return MensajeResponse.builder()
                .mensaje("La contraseña ha sido restablecida exitosamente")
                .build();
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
