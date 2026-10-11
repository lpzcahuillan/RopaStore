# Sistema de Microservicios - Tienda de Ropa (RopaStore)

Arquitectura de microservicios desarrollada con **Java 17**, **Spring Boot 3.2**, **Spring Data JPA**, **Spring Security**, **JJWT 0.12.6**, **RestClient**, **HikariCP**, **Lombok** y bases de datos en memoria **H2** independientes para cada servicio.

---

## 🏛️ Arquitectura del Sistema

El sistema está compuesto por 4 microservicios independientes:

```
                  ┌──────────────────────────────────────────────┐
                  │                 API GATEWAY                  │
                  │           (Capa de Entrada / Ruteo)          │
                  └──────┬────────────┬────────────┬─────────────┘
                         │            │            │
            ┌────────────┘            │            └────────────┐
            ▼                         ▼                         ▼
   ┌─────────────────┐       ┌─────────────────┐       ┌─────────────────┐
   │   ms-usuarios   │       │  ms-inventario  │       │   ms-pedidos    │
   │  (Puerto 8081)  │       │  (Puerto 8082)  │       │  (Puerto 8083)  │
   │  H2: usuariosdb │       │ H2: inventariodb│       │  H2: pedidosdb  │
   └────────▲────────┘       └────────▲────────┘       └────────▲───▲────┘
            │                         │                         │   │
            │                         └───────── RestClient ────┤   │
            │                                   (Ajuste Stock)  │   │
            └─────────────────────────── RestClient ────────────┘   │
                                         (Valida Cliente)           │
                                                                    │ RestClient
                                                                    │ (Estado Pedido)
                                                       ┌────────────┴────┐
                                                       │    ms-ventas    │
                                                       │  (Puerto 8084)  │
                                                       │   H2: ventasdb  │
                                                       └─────────────────┘
```

| Microservicio | Puerto | Base de Datos H2 | Pool HikariCP | Responsabilidad Principal |
| :--- | :---: | :--- | :--- | :--- |
| **ms-usuarios** | `8081` | `jdbc:h2:mem:usuariosdb` | `HikariPool-Usuarios` | Autenticación JWT, registro, recuperación de clave y clientes. |
| **ms-inventario** | `8082` | `jdbc:h2:mem:inventariodb` | `HikariPool-Inventario` | Catálogo de productos, categorías y ajuste de stock transaccional. |
| **ms-pedidos** | `8083` | `jdbc:h2:mem:pedidosdb` | `HikariPool-Pedidos` | Creación de pedidos, reserva/reposición de stock y notificaciones. |
| **ms-ventas** | `8084` | `jdbc:h2:mem:ventasdb` | `HikariPool-Ventas` | Pasarela de pago simulada (Strategy), ventas e informes analíticos. |

---

## 🔑 Credenciales de Ejemplo Precargadas (ms-usuarios)

Las siguientes cuentas son creadas automáticamente al iniciar `ms-usuarios` mediante `CommandLineRunner` con contraseñas hasheadas con BCrypt:

| Rol | Nombre | Email | Contraseña |
| :--- | :--- | :--- | :--- |
| **ADMIN** | Administrador Principal | `admin@tienda.com` | `Admin1234!` |
| **EMPLEADO** | Carlos Empleado | `empleado@tienda.com` | `Empleado1234!` |
| **CLIENTE** | Juan Perez | `cliente1@tienda.com` | `Cliente1234!` |
| **CLIENTE** | Maria Gomez | `cliente2@tienda.com` | `Cliente1234!` |

> [!NOTE]
> El endpoint público `GET /api/clientes/{id}/existe` en `ms-usuarios` está habilitado con `permitAll()` para permitir que `ms-pedidos` verifique la existencia de clientes sin necesidad de token.
> Los endpoints protegidos `/api/clientes/**` requieren un token JWT de rol **ADMIN** o **EMPLEADO**.

---

## 🚀 Orden Recomendado para Levantar los Servicios

Debido a las dependencias de comunicación HTTP sincrónica vía `RestClient`, se recomienda levantar los servicios en el siguiente orden:

1. **`ms-usuarios` (Puerto 8081)**: Servicio base para autenticación y validación de clientes.
2. **`ms-inventario` (Puerto 8082)**: Catálogo y control de stock de productos.
3. **`ms-pedidos` (Puerto 8083)**: Depende de `ms-usuarios` (8081) y `ms-inventario` (8082).
4. **`ms-ventas` (Puerto 8084)**: Depende de `ms-pedidos` (8083).

### Comandos de Ejecución

Desde la carpeta raíz del proyecto puedes compilar los 4 servicios de una sola vez:
```bash
mvn clean package -DskipTests
```

Luego, abre 4 terminales independientes y ejecuta cada servicio:

#### Terminal 1: ms-usuarios
```bash
cd ms-usuarios
mvn spring-boot:run
# O alternativamente: java -jar target/ms-usuarios-1.0.0.jar
```

#### Terminal 2: ms-inventario
```bash
cd ms-inventario
mvn spring-boot:run
# O alternativamente: java -jar target/ms-inventario-1.0.0.jar
```

#### Terminal 3: ms-pedidos
```bash
cd ms-pedidos
mvn spring-boot:run
# O alternativamente: java -jar target/ms-pedidos-1.0.0.jar
```

#### Terminal 4: ms-ventas
```bash
cd ms-ventas
mvn spring-boot:run
# O alternativamente: java -jar target/ms-ventas-1.0.0.jar
```

---

## 🔄 Recorrido de Prueba Punta a Punta (End-to-End Walkthrough)

Puedes seguir este flujo utilizando la colección de Postman importando el archivo `tienda-ropa.postman_collection.json` o ejecutando estos comandos cURL:

### Paso 1: Autenticarse como CLIENTE (o ADMIN) en ms-usuarios
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "cliente1@tienda.com",
    "password": "Cliente1234!"
  }'
```
*Respuesta (200 OK):*
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tipo": "Bearer",
  "rol": "CLIENTE"
}
```

### Paso 2: Consultar Stock Inicial del Producto en ms-inventario
Consultamos el producto con ID `1` ("Camisa Oxford Formal"):
```bash
curl -X GET http://localhost:8082/api/productos/1
```
*Respuesta (200 OK):*
```json
{
  "id": 1,
  "nombre": "Camisa Oxford Formal",
  "talla": "L",
  "color": "Blanco",
  "precio": 24990.00,
  "stock": 25,
  "categoria": { "id": 1, "nombre": "Poleras y Camisas" }
}
```
*(Observa que el stock inicial es **25**).*

### Paso 3: Crear un Pedido en ms-pedidos
Creamos un pedido para el cliente ID `3` solicitando 2 unidades del producto ID `1`:
```bash
curl -X POST http://localhost:8083/api/pedidos \
  -H "Content-Type: application/json" \
  -d '{
    "clienteId": 3,
    "items": [
      {
        "productoId": 1,
        "cantidad": 2
      }
    ]
  }'
```
*Respuesta (201 Created):*
```json
{
  "id": 1,
  "clienteId": 3,
  "fecha": "2026-10-11T12:30:00",
  "estado": "CREADO",
  "total": 49980.00,
  "detalles": [
    {
      "id": 1,
      "productoId": 1,
      "nombreProducto": "Camisa Oxford Formal",
      "cantidad": 2,
      "precioUnitario": 24990.00
    }
  ]
}
```
*(El pedido queda creado con ID `1` en estado `CREADO` y se genera su notificación).*

### Paso 4: Verificar la Baja Automática de Stock en ms-inventario
Volvemos a consultar el producto ID `1`:
```bash
curl -X GET http://localhost:8082/api/productos/1
```
*Respuesta (200 OK):*
```json
{
  "id": 1,
  "nombre": "Camisa Oxford Formal",
  "stock": 23
}
```
*(El stock bajó exitosamente de **25** a **23**).*

### Paso 5: Pagar el Pedido en ms-ventas
Procesamos el pago del pedido ID `1` usando tarjeta de crédito (o PayPal):
```bash
curl -X POST http://localhost:8084/api/pagos \
  -H "Content-Type: application/json" \
  -d '{
    "pedidoId": 1,
    "metodoPago": "TARJETA_CREDITO",
    "numeroTarjeta": "4532112233441234"
  }'
```
*Respuesta (201 Created):*
```json
{
  "id": 16,
  "pedidoId": 1,
  "clienteId": 3,
  "monto": 49980.00,
  "metodoPago": "TARJETA_CREDITO",
  "estado": "APROBADO",
  "fecha": "2026-10-11T12:35:00",
  "referenciaPago": "TX-TC-A1B2C3D4 (**** 1234)",
  "detalles": [
    {
      "id": 16,
      "productoId": 1,
      "nombreProducto": "Camisa Oxford Formal",
      "cantidad": 2,
      "montoLinea": 49980.00
    }
  ]
}
```

### Paso 6: Verificar que el Pedido cambió a PAGADO en ms-pedidos
Consultamos el pedido ID `1`:
```bash
curl -X GET http://localhost:8083/api/pedidos/1
```
*Respuesta (200 OK):*
```json
{
  "id": 1,
  "estado": "PAGADO",
  "total": 49980.00
}
```
Y sus notificaciones asociadas:
```bash
curl -X GET http://localhost:8083/api/pedidos/1/notificaciones
```
*Respuesta (200 OK):*
```json
[
  { "id": 1, "mensaje": "Pedido #1 creado exitosamente en estado CREADO" },
  { "id": 2, "mensaje": "El pedido #1 ha cambiado su estado a PAGADO" }
]
```

### Paso 7: Consultar Informes Analíticos en ms-ventas
1. **Productos Más Vendidos (JPQL agrupada):**
```bash
curl -X GET "http://localhost:8084/api/informes/mas-vendidos?limite=5"
```
2. **Tendencias de Ventas por Día:**
```bash
curl -X GET "http://localhost:8084/api/informes/tendencias?desde=2026-09-01&hasta=2026-10-31"
```
3. **Resumen General (Total vendido, cantidad de ventas y ticket promedio):**
```bash
curl -X GET "http://localhost:8084/api/informes/resumen"
```

---

## ⚠️ Casos de Error y Códigos HTTP Específicos

El sistema cuenta con un `GlobalExceptionHandler` uniforme en los 4 servicios que devuelve la estructura JSON:
```json
{
  "estado": <código_http>,
  "mensaje": "<detalle_del_error>",
  "fecha": "2026-10-11T12:00:00"
}
```

1. **401 Unauthorized (`ms-usuarios`):**
   - **Caso:** Intento de login con credenciales erróneas.
   - **Endpoint:** `POST /api/auth/login` con contraseña incorrecta.
2. **404 Not Found (`ms-inventario` / `ms-pedidos` / `ms-usuarios`):**
   - **Caso:** Consultar producto que no existe.
   - **Endpoint:** `GET /api/productos/9999`.
3. **409 Conflict (`ms-inventario` / `ms-pedidos`):**
   - **Caso:** Crear un pedido solicitando más stock del disponible.
   - **Endpoint:** `POST /api/pedidos` solicitando 99,999 unidades.
   - **Acción:** Si fallase a mitad del pedido, `ms-pedidos` repone el stock descontado de los ítems previos y cancela la operación.
4. **402 Payment Required (`ms-ventas`):**
   - **Caso:** Pago rechazado por pasarela con tarjeta terminada en `"0000"`.
   - **Endpoint:** `POST /api/pagos` con `"numeroTarjeta": "4532112233440000"`.
   - **Acción:** La venta queda registrada en BD como `RECHAZADO`, responde HTTP 402 y el pedido permanece en estado `CREADO`.
5. **400 Bad Request:**
   - **Caso:** Transición de estado inválida en pedidos (ej. `PAGADO` a `CREADO`) o validaciones de campos `@NotBlank` / `@Email`.
6. **503 Service Unavailable:**
   - **Caso:** Fallo de comunicación entre microservicios cuando un servicio destino está apagado o excede el timeout configurado.

---

## 📦 Colección Postman

En la raíz del proyecto se incluye el archivo:
- **`tienda-ropa.postman_collection.json`**

Importa este archivo en Postman para obtener:
- Una carpeta por microservicio (`ms-usuarios`, `ms-inventario`, `ms-pedidos`, `ms-ventas`).
- Variables de colección explícitas separadas por microservicio:
  - `{{baseUrlUsuarios}}`: `http://localhost:8081`
  - `{{baseUrlInventario}}`: `http://localhost:8082`
  - `{{baseUrlPedidos}}`: `http://localhost:8083`
  - `{{baseUrlVentas}}`: `http://localhost:8084`
- Pruebas y aserciones automatizadas (`pm.test`) en todas las peticiones para verificar códigos de estado y payloads.
- Suite de pruebas del **CRUD completo de productos** en `ms-inventario`:
  - **CREATE**: `8. Crear Producto` (`POST /api/productos` ➔ `201 Created`)
  - **READ (All)**: `5. Listar Productos` (`GET /api/productos` ➔ `200 OK`)
  - **READ (One)**: `6. Obtener Producto por ID` (`GET /api/productos/1` ➔ `200 OK`)
  - **UPDATE**: `9. Actualizar Producto` (`PUT /api/productos/1` ➔ `200 OK`)
  - **PATCH (Stock)**: `10. Ajustar Stock de Producto` (`PATCH /api/productos/1/stock` ➔ `200 OK`)
  - **DELETE**: `12. Eliminar Producto` (`DELETE /api/productos/6` ➔ `204 No Content`)
- Scripts de prueba automáticos para guardar el token JWT en las variables de la colección tras hacer login.
- Peticiones dedicadas para cada uno de los casos de error requeridos (401, 404, 409, 402).

