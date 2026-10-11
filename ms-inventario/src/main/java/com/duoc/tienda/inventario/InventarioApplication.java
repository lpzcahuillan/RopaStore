package com.duoc.tienda.inventario;

import com.duoc.tienda.inventario.model.Categoria;
import com.duoc.tienda.inventario.model.Producto;
import com.duoc.tienda.inventario.repository.CategoriaRepository;
import com.duoc.tienda.inventario.repository.ProductoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.util.List;

@SpringBootApplication
@Slf4j
public class InventarioApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventarioApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData(CategoriaRepository categoriaRepository, ProductoRepository productoRepository) {
        return args -> {
            if (categoriaRepository.count() == 0) {
                // 3 Categorías
                Categoria cat1 = Categoria.builder()
                        .nombre("Poleras y Camisas")
                        .descripcion("Poleras estampadas, básicas y camisas formales y casuales")
                        .build();

                Categoria cat2 = Categoria.builder()
                        .nombre("Pantalones y Jeans")
                        .descripcion("Jeans denim, pantalones de tela formales y bermudas")
                        .build();

                Categoria cat3 = Categoria.builder()
                        .nombre("Chaquetas y Abrigos")
                        .descripcion("Chaquetas de cuero, abrigos de invierno y cortavientos deportivos")
                        .build();

                categoriaRepository.saveAll(List.of(cat1, cat2, cat3));

                // 6 Productos de ropa
                Producto prod1 = Producto.builder()
                        .nombre("Camisa Oxford Formal")
                        .descripcion("Camisa de algodón de alta calidad con cuello abotonado")
                        .talla("L")
                        .color("Blanco")
                        .precio(new BigDecimal("24990"))
                        .stock(25)
                        .categoria(cat1)
                        .build();

                Producto prod2 = Producto.builder()
                        .nombre("Polera Algodón Básica")
                        .descripcion("Polera cuello redondo 100% algodón suave y transpirable")
                        .talla("M")
                        .color("Negro")
                        .precio(new BigDecimal("12990"))
                        .stock(50)
                        .categoria(cat1)
                        .build();

                Producto prod3 = Producto.builder()
                        .nombre("Jeans Slim Fit Denim")
                        .descripcion("Pantalón jeans corte slim fit en mezclilla elasticada")
                        .talla("42")
                        .color("Azul Marino")
                        .precio(new BigDecimal("34990"))
                        .stock(20)
                        .categoria(cat2)
                        .build();

                Producto prod4 = Producto.builder()
                        .nombre("Pantalón Chino Casual")
                        .descripcion("Pantalón chino cómodo ideal para oficina y uso casual")
                        .talla("40")
                        .color("Beige")
                        .precio(new BigDecimal("29990"))
                        .stock(15)
                        .categoria(cat2)
                        .build();

                Producto prod5 = Producto.builder()
                        .nombre("Chaqueta Biker de Cuero")
                        .descripcion("Chaqueta estilo motoquero con cierres metálicos y forro térmico")
                        .talla("XL")
                        .color("Negro")
                        .precio(new BigDecimal("69990"))
                        .stock(10)
                        .categoria(cat3)
                        .build();

                Producto prod6 = Producto.builder()
                        .nombre("Cortavientos Deportivo")
                        .descripcion("Cortavientos impermeable y ultraligero con capucha ajustable")
                        .talla("M")
                        .color("Gris")
                        .precio(new BigDecimal("27990"))
                        .stock(30)
                        .categoria(cat3)
                        .build();

                productoRepository.saveAll(List.of(prod1, prod2, prod3, prod4, prod5, prod6));

                log.info("ms-inventario: 3 categorías y 6 productos inicializados correctamente.");
            }
        };
    }
}
