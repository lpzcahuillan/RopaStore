package com.duoc.tienda.pedidos;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@Slf4j
public class PedidosApplication {

    public static void main(String[] args) {
        SpringApplication.run(PedidosApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            log.info("ms-pedidos iniciado correctamente. Listo para recibir pedidos desde Postman.");
        };
    }
}
