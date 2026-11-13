# Dejar Postman y el proyecto listos para la demostración en vivo

Instrucciones para que **Claude** y la persona que proyecta dejen todo preparado **sin cabos sueltos**. Seguir el orden. Cada paso dice quién lo hace.

**Resultado esperado:** los 4 servicios arriba, la colección importada en Postman con sus 35 peticiones, las 35 probadas sin fallos y las bases vacías, para que en vivo los ids empiecen en 1.

> **Para Claude:** Claude no maneja la ventana de Postman, pero puede ejecutar la misma colección con **Newman**, el ejecutor de Postman por terminal. El script `demo.sh` hace todo lo automatizable: levanta los servicios, espera a Eureka, ejecuta las 35 peticiones con sus comprobaciones y vacía las bases. La persona solo importa la colección y presiona Send.

## Paso 1 · Programas necesarios (Claude, macOS)

Comprobar cada uno e instalar solo lo que falte:

| Programa | Comprobar | Instalar con Homebrew |
|---|---|---|
| Java 25 | `java -version` | `brew install --cask temurin@25` |
| MySQL 8 (root sin clave) | `mysql -u root -e "select 1"` | `brew install mysql && brew services start mysql` |
| Node.js (para Newman) | `npx -v` | `brew install node` |
| Postman | `ls /Applications/Postman.app` | `brew install --cask postman` |
| Git | `git --version` | `xcode-select --install` |

Si MySQL tiene contraseña para root, antes de usar el script ejecutar `export MYSQL_PWD=la_clave`. El script se la pasa a los servicios, y no hace falta tocar el código.

## Paso 2 · Bajar el proyecto (Claude)

```bash
git clone https://github.com/RXDG2908/eval03-proveedores-compras.git
```

```bash
cd eval03-proveedores-compras && chmod +x demo.sh */mvnw
```

## Paso 3 · Preparar todo con un comando (Claude)

```bash
./demo.sh listo
```

Tarda 2 o 3 minutos la primera vez, porque compila. Lo que tiene que pasar:

1. Inicia `eureka-server` (8761), `proveedor-service` (8081), `producto-service` (8082) y `compra-service` (8083).
2. Espera a que los 3 servicios aparezcan en Eureka, y luego 35 segundos más para que Feign actualice su lista.
3. Vacía las tablas y ejecuta la colección con Newman. Al final debe decir **requests 35 · failed 0** y **assertions 38 · failed 0**.
4. Vuelve a vaciar las tablas y muestra `LISTO: servicios arriba, las 35 pruebas pasaron y las bases quedaron vacías.`

Si algo falla, el script dice qué revisar. Los registros de cada servicio quedan en `.demo/<servicio>.log`. Ver la tabla del paso 7.

## Paso 4 · Importar la colección en Postman (la persona)

1. Abrir Postman. Desde la terminal, Claude puede abrirlo con `open -a Postman`.
2. Si pide iniciar sesión, se puede entrar con una cuenta o elegir la opción de continuar sin cuenta.
3. **Import** → pestaña **Link** → pegar:

   ```
   https://raw.githubusercontent.com/RXDG2908/eval03-proveedores-compras/main/postman/Lab13-Proveedores-Compras.postman_collection.json
   ```

   O arrastrar el archivo `postman/Lab13-Proveedores-Compras.postman_collection.json` a la ventana.
4. Debe aparecer **Lab 13 - Proveedores y compras** con 4 carpetas: Proveedores, Productos, Compras y Perfiles.
5. Clic en la colección → pestaña **Variables**. Deben estar `proveedor` = http://localhost:8081, `producto` = http://localhost:8082 y `compra` = http://localhost:8083. No hace falta crear un environment.
6. **Usar la aplicación de escritorio, no Postman en el navegador.** La versión web no llega a `localhost` sin instalar un agente.

## Paso 5 · Ensayo en Postman (la persona, una vez)

1. Clic derecho en la colección → **Run collection** → **Run**. Las 35 peticiones deben salir en verde: cada una comprueba su código, y la 22 además comprueba el total.
2. Después del ensayo, **vaciar las bases otra vez** para que la demostración en vivo empiece en el id 1. Lo hace Claude:

   ```bash
   ./demo.sh limpiar
   ```

## Paso 6 · Durante la exposición (la persona)

Enviar con **Send**, en este orden. El código aparece arriba a la derecha de la respuesta, y el JSON en la pestaña **Body**.

| Orden | Petición | Debe salir | Qué señalar |
|---|---|---|---|
| 1 | 01 y 02 · registrar proveedores | 201 | se crean con id 1 y 2 |
| 2 | 11, 12 y 13 · registrar productos | 201 | la respuesta trae el objeto `proveedor`: lo consultó Feign en otro servicio |
| 3 | 22 · prueba de aceptación | 201 | bajar hasta `"total": 5136.5` y los subtotales 5000.0 y 136.5 |
| 4 | 06 · datos inválidos | 400 | un mensaje por cada campo |
| 5 | 07 · RUC repetido | 400 | regla de negocio |
| 6 | 25 · cantidad 0 | 400 | validación del detalle |
| 7 | 27 · producto de otro proveedor | 400 | regla de negocio entre servicios |
| 8 | 28 · proveedor inexistente | 404 | error claro, sin guardar nada |
| 9 | 33 · perfil | 200 | `dev`, puerto 8081, `bd_proveedor_dev` |

**Caída controlada (opcional).**

1. Claude detiene proveedor-service:

   ```bash
   kill $(cat .demo/proveedor-service.pid)
   ```

2. Enviar la **22** otra vez: debe responder **503**, "No se pudo comunicar con otro microservicio".
3. Claude lo vuelve a levantar:

   ```bash
   ./demo.sh reiniciar proveedor-service
   ```

4. Esperar unos 30 segundos y la **22** vuelve a responder 201.

**Para repetir la demostración desde cero:** `./demo.sh limpiar` y volver a empezar por la 01.

## Paso 7 · Si algo falla

| Síntoma | Causa | Qué hacer |
|---|---|---|
| Postman: "Could not send request" o ECONNREFUSED | el servicio está apagado | `./demo.sh estado` y luego `./demo.sh iniciar` |
| 503 al registrar productos o compras | Feign aún no encuentra al otro servicio, o está caído | esperar 30 segundos; ver http://localhost:8761 |
| 400 "Ya existe un proveedor con el RUC…" en la 01 | las bases tienen datos de antes | `./demo.sh limpiar` |
| 404 en la 22 o en los productos | se saltó una petición anterior, y los ids no coinciden | `./demo.sh limpiar` y empezar desde la 01 |
| "El puerto … sigue ocupado" | el servicio se inició desde IntelliJ u otra terminal | detenerlo ahí, o usar ese mismo |
| No entra a MySQL | MySQL apagado, o root con clave | `brew services start mysql` o `export MYSQL_PWD=…` |
| Un servicio no arranca | ver su registro | `tail -50 .demo/<servicio>.log` |

## Paso 8 · Si el profesor pide un cambio en el código

1. Hacer el cambio siguiendo las reglas y el mapa de [CLAUDE.md](CLAUDE.md), sección 5: código simple y solo temas hasta la Semana 12.
2. Recompilar y levantar **solo ese servicio**, por ejemplo:

   ```bash
   ./demo.sh reiniciar compra-service
   ```

3. Probar en Postman la petición relacionada. Si el cambio altera una respuesta esperada, por ejemplo un mensaje nuevo, actualizar la prueba de esa petición en la colección: pestaña **Scripts** → **Post-response**.
4. Para comprobar que no se rompió nada más:

   ```bash
   ./demo.sh limpiar && ./demo.sh probar && ./demo.sh limpiar
   ```

## Al terminar

```bash
./demo.sh detener
```
