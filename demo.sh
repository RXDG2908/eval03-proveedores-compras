#!/usr/bin/env bash
# Prepara la demostración del Laboratorio 13.
#   ./demo.sh listo     levanta todo, ejecuta las 35 pruebas y deja las bases vacías
#   ./demo.sh iniciar   levanta Eureka y los 3 servicios (perfil dev)
#   ./demo.sh probar    ejecuta la colección de Postman con Newman
#   ./demo.sh limpiar   vacía las tablas (los ids vuelven a empezar en 1)
#   ./demo.sh estado    muestra qué servicios están arriba
#   ./demo.sh detener   apaga lo que levantó este script
#   ./demo.sh reiniciar compra-service   recompila y vuelve a levantar un servicio (tras un cambio)
# Variables opcionales: MYSQL (ruta del cliente mysql), MYSQL_PWD (clave de root), JAVA_HOME.

set -u
cd "$(dirname "$0")"
MYSQL="${MYSQL:-mysql}"
JAVA="${JAVA_HOME:+$JAVA_HOME/bin/}java"
SERVICIOS="eureka-server proveedor-service producto-service compra-service"
COLECCION="postman/Lab13-Proveedores-Compras.postman_collection.json"
mkdir -p .demo

puerto() {
  case "$1" in
    eureka-server) echo 8761 ;;
    proveedor-service) echo 8081 ;;
    producto-service) echo 8082 ;;
    compra-service) echo 8083 ;;
  esac
}

arriba() { curl -s -o /dev/null -m 2 "http://localhost:$1/"; }

sql() { "$MYSQL" -u root -e "$1"; }

revisar() {
  "$JAVA" -version >/dev/null 2>&1 || { echo "Falta Java 25 (ver CLAUDE.md, sección 2)"; exit 1; }
  sql "select 1" >/dev/null 2>&1 || { echo "No se pudo entrar a MySQL como root. ¿Está encendido? Si root tiene clave: export MYSQL_PWD=la_clave"; exit 1; }
}

iniciar() {
  revisar
  for s in $SERVICIOS; do
    p=$(puerto "$s")
    if arriba "$p"; then echo "$s ya estaba arriba en :$p"; continue; fi
    jar=$(ls "$s"/target/*.jar 2>/dev/null | head -1)
    if [ -z "$jar" ]; then
      echo "Compilando $s..."
      (cd "$s" && chmod +x mvnw && ./mvnw -q -DskipTests package) || { echo "No compiló $s"; exit 1; }
      jar=$(ls "$s"/target/*.jar | head -1)
    fi
    echo "Iniciando $s en :$p"
    SPRING_DATASOURCE_PASSWORD="${MYSQL_PWD:-}" nohup "$JAVA" -jar "$jar" > ".demo/$s.log" 2>&1 &
    echo $! > ".demo/$s.pid"
    for i in $(seq 1 60); do arriba "$p" && break; sleep 2; done
    arriba "$p" || { echo "$s no arrancó: revisar .demo/$s.log"; exit 1; }
  done
  echo "Esperando a que los 3 servicios aparezcan en Eureka..."
  for i in $(seq 1 45); do
    n=$(curl -s -H "Accept: application/json" http://localhost:8761/eureka/apps | grep -o '"app":"[A-Z-]*SERVICE"' | sort -u | wc -l)
    [ "$n" -ge 3 ] && break
    sleep 2
  done
  [ "$n" -ge 3 ] || { echo "Eureka no tiene los 3 servicios"; exit 1; }
  echo "Registrados en Eureka. Esperando 35 s a que Feign actualice su lista..."
  sleep 35
  echo "Servicios listos."
}

limpiar() {
  revisar
  sql "SET FOREIGN_KEY_CHECKS=0;
       TRUNCATE TABLE bd_compra_dev.detalle_compra; TRUNCATE TABLE bd_compra_dev.compra;
       TRUNCATE TABLE bd_producto_dev.producto; TRUNCATE TABLE bd_proveedor_dev.proveedor;
       SET FOREIGN_KEY_CHECKS=1;" || { echo "No se pudieron vaciar las tablas (¿ya se iniciaron los servicios una vez?)"; exit 1; }
  echo "Bases vacías: los ids vuelven a empezar en 1."
}

probar() {
  command -v npx >/dev/null 2>&1 || { echo "Falta Node.js para ejecutar Newman (brew install node)"; exit 1; }
  npx --yes newman run "$COLECCION" --reporters cli --color off
}

estado() {
  for s in $SERVICIOS; do
    p=$(puerto "$s")
    if arriba "$p"; then echo "ARRIBA  $s :$p"; else echo "abajo   $s :$p"; fi
  done
}

detener() {
  for f in .demo/*.pid; do
    [ -f "$f" ] || continue
    kill "$(cat "$f")" 2>/dev/null && echo "Detenido $(basename "$f" .pid)"
    rm -f "$f"
  done
}

reiniciar() {
  s="${1:-}"
  p=$(puerto "$s")
  [ -n "$p" ] || { echo "Uso: ./demo.sh reiniciar proveedor-service|producto-service|compra-service|eureka-server"; exit 1; }
  revisar
  if [ -f ".demo/$s.pid" ]; then kill "$(cat ".demo/$s.pid")" 2>/dev/null; rm -f ".demo/$s.pid"; fi
  for i in $(seq 1 20); do arriba "$p" || break; sleep 1; done
  arriba "$p" && { echo "El puerto $p sigue ocupado: deténlo desde donde lo iniciaste (por ejemplo IntelliJ)"; exit 1; }
  echo "Compilando $s con los cambios..."
  (cd "$s" && chmod +x mvnw && ./mvnw -q -DskipTests package) || { echo "No compiló $s: revisar el error de arriba"; exit 1; }
  echo "Iniciando $s en :$p"
  SPRING_DATASOURCE_PASSWORD="${MYSQL_PWD:-}" nohup "$JAVA" -jar "$(ls "$s"/target/*.jar | head -1)" > ".demo/$s.log" 2>&1 &
  echo $! > ".demo/$s.pid"
  for i in $(seq 1 60); do arriba "$p" && break; sleep 2; done
  arriba "$p" || { echo "$s no arrancó: revisar .demo/$s.log"; exit 1; }
  echo "Arriba. Espera unos 30 s antes de usarlo desde otro servicio (Feign)."
}

case "${1:-}" in
  reiniciar) reiniciar "${2:-}" ;;
  iniciar) iniciar ;;
  limpiar) limpiar ;;
  probar) probar ;;
  estado) estado ;;
  detener) detener ;;
  listo)
    iniciar && limpiar && probar && limpiar && {
      echo
      echo "LISTO: servicios arriba, las 35 pruebas pasaron y las bases quedaron vacías."
      echo "Ahora abre Postman y ejecuta la colección en orden, empezando por la 01."
    } ;;
  *) sed -n '2,11p' "$0" ;;
esac
