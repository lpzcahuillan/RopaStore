package com.duoc.tienda.pedidos.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String detalleErrores = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));
        ErrorResponse response = ErrorResponse.builder()
                .estado(HttpStatus.BAD_REQUEST.value())
                .mensaje("Error de validación: " + detalleErrores)
                .fecha(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorResponse response = ErrorResponse.builder()
                .estado(HttpStatus.BAD_REQUEST.value())
                .mensaje(ex.getMessage())
                .fecha(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(RecursoNoEncontradoException ex) {
        ErrorResponse response = ErrorResponse.builder()
                .estado(HttpStatus.NOT_FOUND.value())
                .mensaje(ex.getMessage())
                .fecha(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(ConflictoNegocioException.class)
    public ResponseEntity<ErrorResponse> handleConflictException(ConflictoNegocioException ex) {
        ErrorResponse response = ErrorResponse.builder()
                .estado(HttpStatus.CONFLICT.value())
                .mensaje(ex.getMessage())
                .fecha(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(ServicioExternoException.class)
    public ResponseEntity<ErrorResponse> handleServiceUnavailableException(ServicioExternoException ex) {
        ErrorResponse response = ErrorResponse.builder()
                .estado(HttpStatus.SERVICE_UNAVAILABLE.value())
                .mensaje(ex.getMessage())
                .fecha(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        ErrorResponse response = ErrorResponse.builder()
                .estado(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .mensaje("Error interno del servidor: " + ex.getMessage())
                .fecha(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
