# Laboratorio 13: Gestión de proveedores y compras

Evaluación 03 de Desarrollo de Aplicaciones Web, sección D. Estudiante: Renzo Raúl León Fernández.

Una empresa controla sus proveedores, productos y compras con tres microservicios. Cada uno tiene su puerto y su base de datos, y entre ellos solo se guardan ids. Todos se registran en Eureka, y se consultan entre sí con OpenFeign.

> Guía completa de instalación (macOS y Windows), demostración en vivo y cambios rápidos: [CLAUDE.md](CLAUDE.md)

## Requisitos

- Java 25, Spring Boot 4.1.2 y Spring Cloud 2025.1.4
- Maven (cada proyecto trae su `mvnw`)
- MySQL en el puerto 3306, usuario `root` sin contraseña (XAMPP)
- IntelliJ IDEA y Postman

## Servicios, puertos y bases de datos

| Servicio | Responsabilidad | dev | test | prod | Base dev | Base test | Base prod |
|---|---|---|---|---|---|---|---|
| eureka-server | Registro de servicios | 8761 | 8761 | 8761 | – | – | – |
| proveedor-service | CRUD de proveedores | 8081 | 8181 | 9081 | bd_proveedor_dev | bd_proveedor_test | bd_proveedor_prod |
| producto-service | CRUD de productos | 8082 | 8182 | 9082 | bd_producto_dev | bd_producto_test | bd_producto_prod |
| compra-service | Compras con detalles | 8083 | 8183 | 9083 | bd_compra_dev | bd_compra_test | bd_compra_prod |

## Ejecución

1. Encender MySQL en XAMPP. En `dev` las bases y las tablas se crean solas.
2. En IntelliJ, ejecutar la clase principal de cada proyecto en este orden:
   1. `EurekaServerApplication`
   2. `ProveedorServiceApplication`
   3. `ProductoServiceApplication`
   4. `CompraServiceApplication`
3. Abrir http://localhost:8761 y comprobar que aparecen los 3 servicios.
4. Importar en Postman `postman/Lab13-Proveedores-Compras.postman_collection.json` y ejecutar las peticiones en orden, con las bases vacías.

## Perfiles

El perfil se elige con la variable de entorno `PROFILE_ACTIVE`; si no se indica, se usa `dev`.

| Valor | dev | test | prod |
|---|---|---|---|
| Puerto | 808x | 818x | 908x |
| Base de datos | `bd_*_dev` | `bd_*_test` | `bd_*_prod` (variable `DB_URL`) |
| Usuario y contraseña | fijos | fijos | variables `DB_USER` y `DB_PASSWORD` |
| Tablas (`ddl-auto`) | `update` | `update` | `validate`: solo las verifica |
| Mostrar SQL | sí | sí | no |

Antes de usar `prod` hay que ejecutar `script.sql`, porque ese perfil no crea tablas. Cada servicio muestra su perfil en `GET /ambiente`.

## Endpoints

| Servicio | Método | Ruta | Descripción |
|---|---|---|---|
| proveedor (8081) | GET, POST | `/api/proveedores` | Listar, registrar |
| proveedor (8081) | GET, PUT, DELETE | `/api/proveedores/{id}` | Buscar, actualizar, eliminar |
| producto (8082) | GET, POST | `/api/productos` | Listar, registrar |
| producto (8082) | GET, PUT, DELETE | `/api/productos/{id}` | Buscar, actualizar, eliminar |
| compra (8083) | GET, POST | `/api/compras` | Listar, registrar con detalles |
| compra (8083) | GET, DELETE | `/api/compras/{id}` | Buscar, eliminar |

## Respuestas de error

| Código | Cuándo ocurre |
|---|---|
| 400 | Datos inválidos, RUC repetido o producto de otro proveedor |
| 404 | El proveedor, el producto o la compra no existe |
| 503 | Otro microservicio está apagado |

## Evidencias

- `capturas/`: respuestas de Postman, Eureka y las tablas en phpMyAdmin
- `diagramas/`: arquitectura, flujo de una compra y tablas
- `informe/`: informe en Word y PDF
- `presentacion/Presentacion-Lab13-Renzo-Leon.html`: la presentación en un solo archivo, con fuentes incluidas; funciona sin internet en cualquier navegador.
- `presentacion/`: presentación para la sustentación. Se abre `presentacion/index.html` en Chrome: → avanza, ← vuelve, F pantalla completa, N notas, T tiempo, número + Enter salta a una diapositiva

## Avance

- [x] Eureka y los 3 microservicios, con perfiles dev, test y prod
- [x] Script SQL y colección de Postman (35 peticiones)
- [x] Capturas de Postman, Eureka y bases de datos
- [x] Diagramas
- [x] Informe con 5 conclusiones y 1 dificultad
- [x] Informe entregado en Canvas
- [x] Presentación para la sustentación

## Pendiente

- Preparar la sustentación individual
