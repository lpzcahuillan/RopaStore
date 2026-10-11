package com.duoc.tienda.usuarios.controller;

import com.duoc.tienda.usuarios.model.dto.*;
import com.duoc.tienda.usuarios.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/registro")
    public ResponseEntity<UsuarioDTO> registro(@Valid @RequestBody RegistroRequest request) {
        UsuarioDTO creado = authService.registro(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/recuperar")
    public ResponseEntity<MensajeResponse> recuperar(@Valid @RequestBody RecuperarRequest request) {
        MensajeResponse response = authService.recuperarPassword(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/restablecer")
    public ResponseEntity<MensajeResponse> restablecer(@Valid @RequestBody RestablecerRequest request) {
        MensajeResponse response = authService.restablecerPassword(request);
        return ResponseEntity.ok(response);
    }
}
