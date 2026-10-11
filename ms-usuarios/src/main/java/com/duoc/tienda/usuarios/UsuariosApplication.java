package com.duoc.tienda.usuarios;

import com.duoc.tienda.usuarios.model.Rol;
import com.duoc.tienda.usuarios.model.Usuario;
import com.duoc.tienda.usuarios.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@Slf4j
public class UsuariosApplication {

    public static void main(String[] args) {
        SpringApplication.run(UsuariosApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (usuarioRepository.count() == 0) {
                // 1 ADMIN
                Usuario admin = Usuario.builder()
                        .nombre("Administrador Principal")
                        .email("admin@tienda.com")
                        .passwordHash(passwordEncoder.encode("Admin1234!"))
                        .rol(Rol.ADMIN)
                        .activo(true)
                        .build();

                // 1 EMPLEADO
                Usuario empleado = Usuario.builder()
                        .nombre("Carlos Empleado")
                        .email("empleado@tienda.com")
                        .passwordHash(passwordEncoder.encode("Empleado1234!"))
                        .rol(Rol.EMPLEADO)
                        .activo(true)
                        .build();

                // 2 CLIENTES
                Usuario cliente1 = Usuario.builder()
                        .nombre("Juan Perez")
                        .email("cliente1@tienda.com")
                        .passwordHash(passwordEncoder.encode("Cliente1234!"))
                        .rol(Rol.CLIENTE)
                        .activo(true)
                        .build();

                Usuario cliente2 = Usuario.builder()
                        .nombre("Maria Gomez")
                        .email("cliente2@tienda.com")
                        .passwordHash(passwordEncoder.encode("Cliente1234!"))
                        .rol(Rol.CLIENTE)
                        .activo(true)
                        .build();

                usuarioRepository.save(admin);
                usuarioRepository.save(empleado);
                usuarioRepository.save(cliente1);
                usuarioRepository.save(cliente2);

                log.info("ms-usuarios: 4 usuarios de prueba inicializados correctamente.");
            }
        };
    }
}
