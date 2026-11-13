# Guía para Claude: Evaluación 03 · Laboratorio 13 (Proveedores y compras)

Proyecto de Renzo León Fernández (Desarrollo de Aplicaciones Web, Tecsup, 4 C24 sección D). Este archivo es para que Claude ayude a **instalar, ejecutar y demostrar en vivo** el proyecto, y a **hacer cambios pequeños** si el profesor los pide durante la sustentación.

## 1. Qué es el proyecto

Cuatro proyectos Maven independientes, sin proyecto padre:

| Carpeta | Puerto dev | Base de datos dev | Qué hace |
|---|---|---|---|
| `eureka-server` | 8761 | – | Registro de servicios |
| `proveedor-service` | 8081 | `bd_proveedor_dev` | CRUD de proveedores, RUC único |
| `producto-service` | 8082 | `bd_producto_dev` | CRUD de productos; pregunta a proveedor-service con Feign |
| `compra-service` | 8083 | `bd_compra_dev` | Compras con detalles; pregunta a los dos con Feign y calcula subtotales y total |

- Java 25, Spring Boot 4.1.2, Spring Cloud 2025.1.4, MySQL. Sin Lombok y sin API Gateway.
- Paquetes de cada servicio en `src/main/java/com/tecsup/`: `model`, `dto`, `repository`, `services`, `controller`, `exception` y `client` (Feign).
- Perfiles en `src/main/resources/`: `application.yaml` elige el perfil con `PROFILE_ACTIVE` (por defecto `dev`). `application-dev.yml`, `-test.yml` y `-prod.yml` cambian puerto, base y `ddl-auto`.
- En **dev** las bases y tablas se crean solas (`createDatabaseIfNotExist=true` y `ddl-auto: update`). Usuario `root`, **sin contraseña**.

## 2. Instalación en macOS

Hacer en este orden y comprobar cada paso antes del siguiente.

1. **Java 25 (JDK).** Opciones:
   - Homebrew: `brew install --cask temurin@25`
   - O el instalador de https://adoptium.net (Temurin 25, macOS, `.pkg`).
   - Comprobar: `java -version` debe decir 25.
2. **MySQL 8 en el puerto 3306**, con usuario `root`:
   - Homebrew: `brew install mysql` y luego `brew services start mysql`. Por defecto, root queda sin contraseña, igual que en el proyecto.
   - O XAMPP para Mac, encendiendo MySQL desde su panel.
   - Comprobar: `mysql -u root -e "select version();"`
   - **Si root tiene contraseña**, no cambiar el código: pasarla por variable de entorno al ejecutar cada servicio, con `SPRING_DATASOURCE_PASSWORD=la_clave`. En IntelliJ va en Run → Edit Configurations → Environment variables.
3. **Permisos del wrapper de Maven** (solo si sale "permission denied"): `chmod +x */mvnw`
4. **IntelliJ IDEA** (sirve la versión gratuita): File → Open → la carpeta raíz del repositorio. Luego, en cada carpeta, clic derecho en `pom.xml` → **Add as Maven Project** (son 4). En File → Project Structure → SDK, elegir el JDK 25.
5. **Postman** (opcional): importar `postman/Lab13-Proveedores-Compras.postman_collection.json`.

En Windows es lo mismo: JDK 25, MySQL de XAMPP y `mvnw.cmd` en lugar de `./mvnw`.

## 3. Ejecutar

El orden importa: Eureka primero, porque los demás se registran en él.

**Desde IntelliJ:** ejecutar en orden `EurekaServerApplication`, `ProveedorServiceApplication`, `ProductoServiceApplication` y `CompraServiceApplication`. Esperar a que cada uno diga `Started ...` antes de lanzar el siguiente.

**Desde la terminal**, una terminal por servicio:

```bash
cd eureka-server && ./mvnw spring-boot:run
```

```bash
cd proveedor-service && ./mvnw spring-boot:run
```

```bash
cd producto-service && ./mvnw spring-boot:run
```

```bash
cd compra-service && ./mvnw spring-boot:run
```

**Comprobar:**

- http://localhost:8761 debe mostrar PROVEEDOR-SERVICE, PRODUCTO-SERVICE y COMPRA-SERVICE. Feign tarda unos 30 segundos en encontrar a los demás.
- `GET http://localhost:8081/ambiente` muestra el perfil, el puerto y la base.

Para compilar sin pruebas: `./mvnw -DskipTests package`. Las pruebas generadas por Initializr necesitan MySQL encendido.

## 4. Demostración en vivo (orden de la colección de Postman)

Con las bases vacías, ejecutar las peticiones en orden; los ids dependen del orden.

1. `POST :8081/api/proveedores` con `{"ruc":"20512345678","razonSocial":"Distribuidora Andina SAC"}` → 201
2. `POST :8082/api/productos` con `{"nombre":"Laptop Lenovo 14","precio":2500,"stock":5,"idProveedor":1}` → 201
3. `POST :8082/api/productos` con `{"nombre":"Mouse inalambrico","precio":45.5,"stock":20,"idProveedor":1}` → 201
4. **Prueba de aceptación:** `POST :8083/api/compras` con:

   ```json
   {"idProveedor":1,"fecha":"2026-11-06","detalles":[{"idProducto":1,"cantidad":2,"precio":2500},{"idProducto":2,"cantidad":3,"precio":45.5}]}
   ```

   Responde 201 con subtotales 5000.0 y 136.5 y **total 5136.5**.
5. **Errores para mostrar:**
   - RUC `"123"` → 400
   - RUC repetido → 400
   - `cantidad: 0` → 400
   - producto de otro proveedor → 400
   - id inexistente (999) → 404
   - apagar proveedor-service y registrar una compra → 503
6. **Volver a empezar de cero:** detener los servicios y ejecutar:

   ```bash
   mysql -u root -e "drop database bd_proveedor_dev; drop database bd_producto_dev; drop database bd_compra_dev;"
   ```

   Al arrancar de nuevo, las bases se vuelven a crear.

**Qué pasa si un servicio se cae** (probado):

| Cae | Resultado |
|---|---|
| compra | proveedor y producto siguen al 100% |
| producto | compra responde 503 |
| proveedor | producto y compra responden 503 |
| Vuelve el que cayó | todo responde 200, sin reiniciar nada |

## 5. Si el profesor pide un cambio en vivo

**Reglas para Claude:**

- **Código simple y básico**, con el estilo que ya tiene el proyecto: getters y setters escritos a mano (sin Lombok), `@Autowired`, el método `copiarDatos` en el Service, y errores con `ResourceNotFoundException` (404) o `IllegalArgumentException` (400).
- **Usar solo temas vistos hasta la Semana 12 del curso:** Spring Web, JPA, DTO con Bean Validation, `@RestControllerAdvice`, perfiles YAML, Eureka y OpenFeign. No agregar API Gateway, Lombok, seguridad ni librerías nuevas.
- **Explicar cada cambio en una frase**, porque Renzo tiene que poder explicarlo en voz alta.
- **Al terminar, reiniciar el servicio cambiado y probarlo con Postman.**

**Dónde está cada cosa:**

| Si piden… | Archivo |
|---|---|
| Cambiar una validación o su mensaje | `*/dto/*DTO.java`. Por ejemplo, el RUC en `proveedor-service/.../dto/ProveedorDTO.java` y la cantidad en `compra-service/.../dto/DetalleCompraDTO.java` |
| RUC repetido | `proveedor-service/.../services/ProveedorService.java`, método `validarRucUnico` |
| Producto de otro proveedor | `compra-service/.../services/CompraService.java`, dentro de `registrar` |
| Cálculo de subtotal y total | `CompraService.registrar`; el redondeo está en `redondear` |
| Consultas a otro servicio | `*/client/*Client.java` (interfaces `@FeignClient`) |
| Formato de los errores | `*/exception/GlobalExceptionHandler.java` |
| Puertos y bases por perfil | `*/src/main/resources/application-{dev,test,prod}.yml` |
| Rutas REST | `*/controller/*Controller.java` |

**Recetas:**

- **Agregar un campo**, por ejemplo `estado` en Proveedor:
  1. Atributo con su `@Column` en `model/Proveedor.java`, más su getter y setter.
  2. Lo mismo en `dto/ProveedorDTO.java`, con su validación.
  3. Una línea en `copiarDatos` del Service.
  4. Reiniciar el servicio. En dev, `ddl-auto: update` agrega la columna solo.
- **Nueva consulta**, por ejemplo productos de un proveedor:
  1. En `ProductoRepository`: `List<Producto> findByIdProveedor(Long idProveedor);`
  2. Un método en el Service que la llame.
  3. En el Controller: `@GetMapping("/proveedor/{idProveedor}")`.
- **Nueva regla de negocio:** validar en el Service y lanzar `IllegalArgumentException("mensaje")`. El handler la devuelve como 400.
- **Cambiar un puerto:** editar `server.port` en `application-dev.yml`. Si es un servicio consultado por Feign no hace falta tocar nada más, porque Feign lo busca por nombre en Eureka.

## 6. Otros archivos del repositorio

- `informe/`: informe entregado (Word y PDF).
- `presentacion/Presentacion-Lab13-Renzo-Leon.html`: presentación en un solo archivo. Se abre en el navegador: → avanza, F pantalla completa, N notas, número + Enter salta.
- `capturas/` y `diagramas/`: evidencias del informe.
- `script.sql`: tablas para el perfil prod, que solo valida y no crea tablas.
