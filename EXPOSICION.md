# Exposición · Laboratorio 13 (Proveedores y compras)

Todo lo necesario para **la demostración en Postman** y para **acompañar la exposición** de Renzo León Fernández. La instalación y los cambios en vivo están en [CLAUDE.md](CLAUDE.md).

> Para Claude: este archivo se generó a partir de `postman/Lab13-Proveedores-Compras.postman_collection.json` y de pruebas reales. Los códigos y mensajes son los que devuelve la API. Si Postman no puede importar la colección, se pueden recrear las peticiones con la sección 3, o usar los comandos `curl` de la sección 4.

## 1. Antes de empezar

1. Los 4 servicios corriendo en orden (Eureka, proveedor, producto y compra) con el perfil **dev**. Ver CLAUDE.md, sección 3.
2. http://localhost:8761 muestra los 3 servicios en UP. **Esperar unos 30 segundos** antes de registrar compras, para que Feign encuentre a los demás.
3. **Bases vacías.** Los ids de la colección suponen que se empieza de cero. Para vaciarlas, con los servicios apagados:

   ```bash
   mysql -u root -e "drop database if exists bd_proveedor_dev; drop database if exists bd_producto_dev; drop database if exists bd_compra_dev;"
   ```

   En Windows con XAMPP, el programa es `C:\xampp\mysql\bin\mysql.exe`. Al arrancar los servicios, las bases se crean solas.

## 2. Postman

- **Importar:** botón Import → arrastrar `postman/Lab13-Proveedores-Compras.postman_collection.json`.
- La colección trae sus propias variables, así que no hace falta crear un environment:

| Variable | Valor |
|---|---|
| `proveedor` | http://localhost:8081 |
| `producto` | http://localhost:8082 |
| `compra` | http://localhost:8083 |

- Para usar otro perfil (test 818x, prod 908x), cambiar los valores en la pestaña **Variables** de la colección.
- Ejecutar **en orden**, de la 01 a la 35. Cada petición trae una prueba de su código; con **Run collection** las 35 deben salir en verde. Para preparar todo automáticamente, ver [POSTMAN.md](POSTMAN.md).

### Resumen de las 35 peticiones

| # | Petición | Método | Ruta | Código | Qué mostrar en la respuesta |
|---|---|---|---|---|---|
| 01 | Registrar proveedor 1 | `POST` | `:8081/api/proveedores` | **201** | idProveedor 1 |
| 02 | Registrar proveedor 2 | `POST` | `:8081/api/proveedores` | **201** | idProveedor 2 |
| 03 | Listar proveedores | `GET` | `:8081/api/proveedores` | **200** | lista con 2 proveedores |
| 04 | Buscar proveedor por id | `GET` | `:8081/api/proveedores/1` | **200** | datos del proveedor 1 |
| 05 | Actualizar proveedor | `PUT` | `:8081/api/proveedores/1` | **200** | dirección nueva |
| 06 | ERROR datos invalidos | `POST` | `:8081/api/proveedores` | **400** | mensajes de ruc, razonSocial y correo |
| 07 | ERROR RUC repetido | `POST` | `:8081/api/proveedores` | **400** | Ya existe un proveedor con el RUC 20512345678 |
| 08 | ERROR proveedor inexistente | `GET` | `:8081/api/proveedores/999` | **404** | Proveedor no encontrado con id: 999 |
| 09 | Registrar proveedor temporal | `POST` | `:8081/api/proveedores` | **201** | idProveedor 3 |
| 10 | Eliminar proveedor temporal | `DELETE` | `:8081/api/proveedores/3` | **200** | Proveedor eliminado correctamente |
| 11 | Registrar producto 1 | `POST` | `:8082/api/productos` | **201** | idProducto 1 y el objeto proveedor (lo trae Feign) |
| 12 | Registrar producto 2 | `POST` | `:8082/api/productos` | **201** | idProducto 2 |
| 13 | Registrar producto de otro proveedor | `POST` | `:8082/api/productos` | **201** | idProducto 3, del proveedor 2 |
| 14 | Listar productos | `GET` | `:8082/api/productos` | **200** | lista con 3 productos |
| 15 | Buscar producto por id | `GET` | `:8082/api/productos/1` | **200** | producto 1 con su proveedor |
| 16 | Actualizar producto | `PUT` | `:8082/api/productos/1` | **200** | descripción Core i7 |
| 17 | ERROR datos invalidos | `POST` | `:8082/api/productos` | **400** | mensajes de nombre, precio, stock e idProveedor |
| 18 | ERROR proveedor inexistente | `POST` | `:8082/api/productos` | **404** | Proveedor no encontrado con id: 999 |
| 19 | ERROR producto inexistente | `GET` | `:8082/api/productos/999` | **404** | Producto no encontrado con id: 999 |
| 20 | Registrar producto temporal | `POST` | `:8082/api/productos` | **201** | idProducto 4 |
| 21 | Eliminar producto temporal | `DELETE` | `:8082/api/productos/4` | **200** | Producto eliminado correctamente |
| 22 | PRUEBA DE ACEPTACION compra con 2 productos | `POST` | `:8083/api/compras` | **201** | subtotales 5000.0 y 136.5, total 5136.5 |
| 23 | Listar compras | `GET` | `:8083/api/compras` | **200** | lista con la compra 1 |
| 24 | Buscar compra por id | `GET` | `:8083/api/compras/1` | **200** | compra 1 con detalles y total |
| 25 | ERROR cantidad 0 | `POST` | `:8083/api/compras` | **400** | detalles[0].cantidad: La cantidad debe ser mayor a 0 |
| 26 | ERROR compra sin detalles | `POST` | `:8083/api/compras` | **400** | La compra debe tener al menos un detalle |
| 27 | ERROR producto de otro proveedor | `POST` | `:8083/api/compras` | **400** | El producto Papel bond A4 no pertenece al proveedor Distribuidora Andina SAC |
| 28 | ERROR proveedor inexistente | `POST` | `:8083/api/compras` | **404** | Proveedor no encontrado con id: 999 |
| 29 | ERROR producto inexistente | `POST` | `:8083/api/compras` | **404** | Producto no encontrado con id: 999 |
| 30 | ERROR compra inexistente | `GET` | `:8083/api/compras/999` | **404** | Compra no encontrada con id: 999 |
| 31 | Registrar compra temporal | `POST` | `:8083/api/compras` | **201** | idCompra 2 |
| 32 | Eliminar compra temporal | `DELETE` | `:8083/api/compras/2` | **200** | Compra eliminada correctamente |
| 33 | Perfil de proveedor-service | `GET` | `:8081/ambiente` | **200** | perfil dev, puerto 8081, bd_proveedor_dev |
| 34 | Perfil de producto-service | `GET` | `:8082/ambiente` | **200** | perfil dev, puerto 8082, bd_producto_dev |
| 35 | Perfil de compra-service | `GET` | `:8083/ambiente` | **200** | perfil dev, puerto 8083, bd_compra_dev |

Totales esperados: 14 respuestas 200, 9 respuestas 201, 6 respuestas 400 y 6 respuestas 404.

## 3. Peticiones completas (para recrearlas a mano)

Todas las peticiones con cuerpo llevan el encabezado `Content-Type: application/json`.

### Proveedores

**01 Registrar proveedor 1** · `POST http://localhost:8081/api/proveedores` → **201**

```json
{"ruc": "20512345678", "razonSocial": "Distribuidora Andina SAC", "direccion": "Av. Industrial 123, Lima", "telefono": "987654321", "correo": "ventas@andina.pe"}
```

**02 Registrar proveedor 2** · `POST http://localhost:8081/api/proveedores` → **201**

```json
{"ruc": "20601234567", "razonSocial": "Suministros del Sur EIRL", "direccion": "Jr. Arequipa 450, Lima", "telefono": "912345678", "correo": "contacto@sursum.pe"}
```

**03 Listar proveedores** · `GET http://localhost:8081/api/proveedores` → **200**

**04 Buscar proveedor por id** · `GET http://localhost:8081/api/proveedores/1` → **200**

**05 Actualizar proveedor** · `PUT http://localhost:8081/api/proveedores/1` → **200**

```json
{"ruc": "20512345678", "razonSocial": "Distribuidora Andina SAC", "direccion": "Av. Industrial 500, Lima", "telefono": "987654321", "correo": "ventas@andina.pe"}
```

**06 ERROR datos invalidos (400)** · `POST http://localhost:8081/api/proveedores` → **400**

```json
{"ruc": "123", "razonSocial": "", "correo": "malcorreo"}
```

**07 ERROR RUC repetido (400)** · `POST http://localhost:8081/api/proveedores` → **400**

```json
{"ruc": "20512345678", "razonSocial": "Otra Empresa SAC"}
```

**08 ERROR proveedor inexistente (404)** · `GET http://localhost:8081/api/proveedores/999` → **404**

**09 Registrar proveedor temporal** · `POST http://localhost:8081/api/proveedores` → **201**

```json
{"ruc": "10456789012", "razonSocial": "Proveedor Temporal"}
```

**10 Eliminar proveedor temporal** · `DELETE http://localhost:8081/api/proveedores/3` → **200**


### Productos

**11 Registrar producto 1** · `POST http://localhost:8082/api/productos` → **201**

```json
{"nombre": "Laptop Lenovo 14", "descripcion": "Core i5, 16 GB", "precio": 2500.0, "stock": 5, "idProveedor": 1}
```

**12 Registrar producto 2** · `POST http://localhost:8082/api/productos` → **201**

```json
{"nombre": "Mouse inalambrico", "descripcion": "USB", "precio": 45.5, "stock": 20, "idProveedor": 1}
```

**13 Registrar producto de otro proveedor** · `POST http://localhost:8082/api/productos` → **201**

```json
{"nombre": "Papel bond A4", "descripcion": "Paquete", "precio": 18.9, "stock": 100, "idProveedor": 2}
```

**14 Listar productos** · `GET http://localhost:8082/api/productos` → **200**

**15 Buscar producto por id** · `GET http://localhost:8082/api/productos/1` → **200**

**16 Actualizar producto** · `PUT http://localhost:8082/api/productos/1` → **200**

```json
{"nombre": "Laptop Lenovo 14", "descripcion": "Core i7, 16 GB", "precio": 2500.0, "stock": 5, "idProveedor": 1}
```

**17 ERROR datos invalidos (400)** · `POST http://localhost:8082/api/productos` → **400**

```json
{"nombre": "", "precio": -5, "stock": -1}
```

**18 ERROR proveedor inexistente (404)** · `POST http://localhost:8082/api/productos` → **404**

```json
{"nombre": "Teclado", "precio": 80, "stock": 1, "idProveedor": 999}
```

**19 ERROR producto inexistente (404)** · `GET http://localhost:8082/api/productos/999` → **404**

**20 Registrar producto temporal** · `POST http://localhost:8082/api/productos` → **201**

```json
{"nombre": "Producto Temporal", "precio": 10, "stock": 1, "idProveedor": 1}
```

**21 Eliminar producto temporal** · `DELETE http://localhost:8082/api/productos/4` → **200**


### Compras

**22 PRUEBA DE ACEPTACION compra con 2 productos** · `POST http://localhost:8083/api/compras` → **201**

```json
{"idProveedor": 1, "fecha": "2026-11-06", "detalles": [{"idProducto": 1, "cantidad": 2, "precio": 2500.0}, {"idProducto": 2, "cantidad": 3, "precio": 45.5}]}
```

**23 Listar compras** · `GET http://localhost:8083/api/compras` → **200**

**24 Buscar compra por id** · `GET http://localhost:8083/api/compras/1` → **200**

**25 ERROR cantidad 0 (400)** · `POST http://localhost:8083/api/compras` → **400**

```json
{"idProveedor": 1, "fecha": "2026-11-06", "detalles": [{"idProducto": 1, "cantidad": 0, "precio": 10}]}
```

**26 ERROR compra sin detalles (400)** · `POST http://localhost:8083/api/compras` → **400**

```json
{"idProveedor": 1, "fecha": "2026-11-06", "detalles": []}
```

**27 ERROR producto de otro proveedor (400)** · `POST http://localhost:8083/api/compras` → **400**

```json
{"idProveedor": 1, "fecha": "2026-11-06", "detalles": [{"idProducto": 3, "cantidad": 1, "precio": 18.9}]}
```

**28 ERROR proveedor inexistente (404)** · `POST http://localhost:8083/api/compras` → **404**

```json
{"idProveedor": 999, "fecha": "2026-11-06", "detalles": [{"idProducto": 1, "cantidad": 1, "precio": 10}]}
```

**29 ERROR producto inexistente (404)** · `POST http://localhost:8083/api/compras` → **404**

```json
{"idProveedor": 1, "fecha": "2026-11-06", "detalles": [{"idProducto": 999, "cantidad": 1, "precio": 10}]}
```

**30 ERROR compra inexistente (404)** · `GET http://localhost:8083/api/compras/999` → **404**

**31 Registrar compra temporal** · `POST http://localhost:8083/api/compras` → **201**

```json
{"idProveedor": 2, "fecha": "2026-11-06", "detalles": [{"idProducto": 3, "cantidad": 2, "precio": 18.9}]}
```

**32 Eliminar compra temporal** · `DELETE http://localhost:8083/api/compras/2` → **200**


### Perfiles

**33 Perfil de proveedor-service** · `GET http://localhost:8081/ambiente` → **200**

**34 Perfil de producto-service** · `GET http://localhost:8082/ambiente` → **200**

**35 Perfil de compra-service** · `GET http://localhost:8083/ambiente` → **200**


## 4. Respaldo sin Postman: comandos curl

Funcionan en la terminal de macOS, Linux o Git Bash. Ejecutarlos en orden:

```bash
# 01 Registrar proveedor 1
curl -s -X POST http://localhost:8081/api/proveedores -H 'Content-Type: application/json' -d '{"ruc": "20512345678", "razonSocial": "Distribuidora Andina SAC", "direccion": "Av. Industrial 123, Lima", "telefono": "987654321", "correo": "ventas@andina.pe"}'; echo
# 02 Registrar proveedor 2
curl -s -X POST http://localhost:8081/api/proveedores -H 'Content-Type: application/json' -d '{"ruc": "20601234567", "razonSocial": "Suministros del Sur EIRL", "direccion": "Jr. Arequipa 450, Lima", "telefono": "912345678", "correo": "contacto@sursum.pe"}'; echo
# 03 Listar proveedores
curl -s -X GET http://localhost:8081/api/proveedores; echo
# 04 Buscar proveedor por id
curl -s -X GET http://localhost:8081/api/proveedores/1; echo
# 05 Actualizar proveedor
curl -s -X PUT http://localhost:8081/api/proveedores/1 -H 'Content-Type: application/json' -d '{"ruc": "20512345678", "razonSocial": "Distribuidora Andina SAC", "direccion": "Av. Industrial 500, Lima", "telefono": "987654321", "correo": "ventas@andina.pe"}'; echo
# 06 ERROR datos invalidos (400)
curl -s -X POST http://localhost:8081/api/proveedores -H 'Content-Type: application/json' -d '{"ruc": "123", "razonSocial": "", "correo": "malcorreo"}'; echo
# 07 ERROR RUC repetido (400)
curl -s -X POST http://localhost:8081/api/proveedores -H 'Content-Type: application/json' -d '{"ruc": "20512345678", "razonSocial": "Otra Empresa SAC"}'; echo
# 08 ERROR proveedor inexistente (404)
curl -s -X GET http://localhost:8081/api/proveedores/999; echo
# 09 Registrar proveedor temporal
curl -s -X POST http://localhost:8081/api/proveedores -H 'Content-Type: application/json' -d '{"ruc": "10456789012", "razonSocial": "Proveedor Temporal"}'; echo
# 10 Eliminar proveedor temporal
curl -s -X DELETE http://localhost:8081/api/proveedores/3; echo
# 11 Registrar producto 1
curl -s -X POST http://localhost:8082/api/productos -H 'Content-Type: application/json' -d '{"nombre": "Laptop Lenovo 14", "descripcion": "Core i5, 16 GB", "precio": 2500.0, "stock": 5, "idProveedor": 1}'; echo
# 12 Registrar producto 2
curl -s -X POST http://localhost:8082/api/productos -H 'Content-Type: application/json' -d '{"nombre": "Mouse inalambrico", "descripcion": "USB", "precio": 45.5, "stock": 20, "idProveedor": 1}'; echo
# 13 Registrar producto de otro proveedor
curl -s -X POST http://localhost:8082/api/productos -H 'Content-Type: application/json' -d '{"nombre": "Papel bond A4", "descripcion": "Paquete", "precio": 18.9, "stock": 100, "idProveedor": 2}'; echo
# 14 Listar productos
curl -s -X GET http://localhost:8082/api/productos; echo
# 15 Buscar producto por id
curl -s -X GET http://localhost:8082/api/productos/1; echo
# 16 Actualizar producto
curl -s -X PUT http://localhost:8082/api/productos/1 -H 'Content-Type: application/json' -d '{"nombre": "Laptop Lenovo 14", "descripcion": "Core i7, 16 GB", "precio": 2500.0, "stock": 5, "idProveedor": 1}'; echo
# 17 ERROR datos invalidos (400)
curl -s -X POST http://localhost:8082/api/productos -H 'Content-Type: application/json' -d '{"nombre": "", "precio": -5, "stock": -1}'; echo
# 18 ERROR proveedor inexistente (404)
curl -s -X POST http://localhost:8082/api/productos -H 'Content-Type: application/json' -d '{"nombre": "Teclado", "precio": 80, "stock": 1, "idProveedor": 999}'; echo
# 19 ERROR producto inexistente (404)
curl -s -X GET http://localhost:8082/api/productos/999; echo
# 20 Registrar producto temporal
curl -s -X POST http://localhost:8082/api/productos -H 'Content-Type: application/json' -d '{"nombre": "Producto Temporal", "precio": 10, "stock": 1, "idProveedor": 1}'; echo
# 21 Eliminar producto temporal
curl -s -X DELETE http://localhost:8082/api/productos/4; echo
# 22 PRUEBA DE ACEPTACION compra con 2 productos
curl -s -X POST http://localhost:8083/api/compras -H 'Content-Type: application/json' -d '{"idProveedor": 1, "fecha": "2026-11-06", "detalles": [{"idProducto": 1, "cantidad": 2, "precio": 2500.0}, {"idProducto": 2, "cantidad": 3, "precio": 45.5}]}'; echo
# 23 Listar compras
curl -s -X GET http://localhost:8083/api/compras; echo
# 24 Buscar compra por id
curl -s -X GET http://localhost:8083/api/compras/1; echo
# 25 ERROR cantidad 0 (400)
curl -s -X POST http://localhost:8083/api/compras -H 'Content-Type: application/json' -d '{"idProveedor": 1, "fecha": "2026-11-06", "detalles": [{"idProducto": 1, "cantidad": 0, "precio": 10}]}'; echo
# 26 ERROR compra sin detalles (400)
curl -s -X POST http://localhost:8083/api/compras -H 'Content-Type: application/json' -d '{"idProveedor": 1, "fecha": "2026-11-06", "detalles": []}'; echo
# 27 ERROR producto de otro proveedor (400)
curl -s -X POST http://localhost:8083/api/compras -H 'Content-Type: application/json' -d '{"idProveedor": 1, "fecha": "2026-11-06", "detalles": [{"idProducto": 3, "cantidad": 1, "precio": 18.9}]}'; echo
# 28 ERROR proveedor inexistente (404)
curl -s -X POST http://localhost:8083/api/compras -H 'Content-Type: application/json' -d '{"idProveedor": 999, "fecha": "2026-11-06", "detalles": [{"idProducto": 1, "cantidad": 1, "precio": 10}]}'; echo
# 29 ERROR producto inexistente (404)
curl -s -X POST http://localhost:8083/api/compras -H 'Content-Type: application/json' -d '{"idProveedor": 1, "fecha": "2026-11-06", "detalles": [{"idProducto": 999, "cantidad": 1, "precio": 10}]}'; echo
# 30 ERROR compra inexistente (404)
curl -s -X GET http://localhost:8083/api/compras/999; echo
# 31 Registrar compra temporal
curl -s -X POST http://localhost:8083/api/compras -H 'Content-Type: application/json' -d '{"idProveedor": 2, "fecha": "2026-11-06", "detalles": [{"idProducto": 3, "cantidad": 2, "precio": 18.9}]}'; echo
# 32 Eliminar compra temporal
curl -s -X DELETE http://localhost:8083/api/compras/2; echo
# 33 Perfil de proveedor-service
curl -s -X GET http://localhost:8081/ambiente; echo
# 34 Perfil de producto-service
curl -s -X GET http://localhost:8082/ambiente; echo
# 35 Perfil de compra-service
curl -s -X GET http://localhost:8083/ambiente; echo
```

## 5. Demostración corta (si hay poco tiempo)

1. **01** y **02**: registrar proveedores → 201.
2. **11**, **12** y **13**: registrar productos → 201. Mostrar que la respuesta trae el objeto `proveedor`: lo consultó **Feign** en otro servicio.
3. **22**: prueba de aceptación → 201. Bajar en la respuesta hasta **total 5136.5**.
4. **25** (cantidad 0 → 400) y **27** (producto de otro proveedor → 400): los dos tipos de error, validación y regla de negocio.
5. **28**: proveedor inexistente → 404.
6. **Caída controlada:** detener `proveedor-service` y volver a enviar la **22** → **503** con `"No se pudo comunicar con otro microservicio"`. Volver a encenderlo, esperar unos 30 segundos y la **22** vuelve a funcionar, sin reiniciar nada más.

## 6. Guion de la exposición (5 a 8 minutos)

La presentación es `presentacion/Presentacion-Lab13-Renzo-Leon.html`. Teclas: → avanza, ← vuelve, F pantalla completa, N notas, T cronómetro, número + Enter salta.

| # | Diapositiva | Idea en una frase |
|---|---|---|
| 1 | Portada | Laboratorio 13: gestión de proveedores y compras |
| 2 | Arranque | Log real: Eureka arranca y los tres servicios se registran en UP |
| 3 | Tres servicios | Tres responsabilidades, tres puertos (8081, 8082, 8083), una base cada uno |
| 4 | Base de datos | Una base por servicio. Entre servicios solo se guarda el id (REF); la única FK real es detalle_compra → compra |
| 5 | Arquitectura | Mismas capas en todos: Controller, DTO, Service, Client (Feign), Repository y Exception; Eureka para encontrarse por nombre |
| 6 | Dos ambientes | Un mismo .jar se vuelve dev o prod con PROFILE_ACTIVE: cambian el puerto, la base y cómo trata las tablas |
| 7 | Flujo de una compra | 8 pasos: validar DTO, proveedor, cada producto (dos veces), calcular y guardar. Cada error se desvía: 400, 404 o 503 |
| 8 | Prueba de aceptación | 2 × 2500 + 3 × 45.50 = 5136.50 |
| 9 | ¿Y si se cae? | Prueba real: si cae compra, los otros siguen; si cae otro, responde 503 controlado; al volver, todo se recupera solo |
| 10 | Postman | 35 peticiones, todas con el código esperado |
| 11 | Conclusiones | Cinco aprendizajes y la dificultad (base vieja con el mismo nombre) |
| 12 | Cierre | Gracias y preguntas |

## 7. Preguntas probables del profesor

**¿Por qué separó el sistema en tres servicios?**
Proveedor, producto y compra son responsabilidades distintas. Cada uno tiene su propio puerto y su propia base, así que un cambio en uno no obliga a tocar los demás.

**¿Cómo persiste los datos?**
Con Spring Data JPA sobre MySQL, cada servicio en su base. La compra guarda solo el id del proveedor y de cada producto, y consulta sus datos con Feign. Solo compra y detalle tienen relación directa, porque viven en la misma base.

**¿Qué pasa si llega información inválida?**
El DTO con sus validaciones la detiene antes de guardar, y el `GlobalExceptionHandler` responde 400 con el campo y el motivo. Si el registro no existe, responde 404. En ningún caso se guarda nada.

**¿Por qué no hay llave foránea entre producto y proveedor?**
Porque están en bases distintas, de servicios distintos. Una FK no puede cruzar bases; por eso se guarda el id y se verifica por la API.

**¿Para qué sirve Eureka?**
Es un registro: cada servicio se anota con su nombre y su dirección. Así Feign llama a `proveedor-service` por su nombre, sin escribir IP ni puerto.

**¿Qué es Feign?**
Una interfaz con `@FeignClient` donde se declara la ruta del otro servicio, como si fuera un controlador. Spring hace la llamada HTTP por nosotros.

**¿Qué diferencia hay entre los perfiles?**
- **dev:** puertos 808x, bases `_dev`, las tablas se crean solas.
- **test:** puertos 818x, bases `_test`.
- **prod:** puertos 908x; base, usuario y clave por variables de entorno; `ddl-auto: validate` solo verifica las tablas, por eso antes se ejecuta `script.sql`.

**¿Para qué sirven `@Transient` y `@JsonIgnore`?**
- `@Transient` marca un campo que se muestra en el JSON pero no se guarda en la tabla, como el proveedor que llega por Feign.
- `@JsonIgnore` evita el bucle infinito de compra → detalle → compra al convertir a JSON.

**¿Por qué 503 y no 500 cuando se cae un servicio?**
Porque el handler captura la `FeignException` y responde un error controlado, con un mensaje claro, en vez de una caída genérica.

**¿Por qué `double` para los montos?**
Para dinero, lo ideal es un tipo exacto como `BigDecimal`. Usé `double` para mantenerlo simple, y redondeo a dos decimales al calcular subtotales y total.

**¿Qué dificultad tuvo?**
producto-service respondía 500 en todo porque ya existía una `bd_producto_dev` de otra práctica, con otras columnas. La moví a un respaldo sin perder datos y el servicio creó la suya.
