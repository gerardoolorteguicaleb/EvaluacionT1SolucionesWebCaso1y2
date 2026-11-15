# Caso 1 · Billetera Digital Java con Servlet y JSP

Aplicación independiente para este caso nro. 1. La vista se renderiza en el servidor con JSP y JSTL; un Servlet valida cada operación y conserva el saldo y el historial en `HttpSession`.

## Tecnologías y versiones

- OpenJDK 17 o superior.
- Maven 3.9 o superior.
- Apache Tomcat 10.1 o superior.
- Jakarta Servlet 6.0, JSP, JSTL 3.0 y Bootstrap 5.3.

## Pruebas y construcción

```sh
mvn test package
```

El WAR que esta en `target/billetera-digital-java.war` se puede desplegar en Tomcat 10.1 copiándolo en `webapps/` y reiniciando Tomcat. Visita `http://localhost:8080/billetera-digital-java/`. También abre `/billetera-digital-java/wallet` directamente.

## Comportamiento

- Saldo inicial: S/ 250.00; historial inicial: vacío.
- El formulario ofrece Recarga y Gasto, y requiere un monto positivo de hasta dos decimales.
- Un gasto no puede exceder el saldo disponible. Errores y operaciones no válidas no alteran el historial ni el saldo.
- Un movimiento válido se guarda al inicio de la lista y muestra tipo, monto, orden y fecha.
- Una sesión independiente mantiene los datos al recargar; las operaciones aceptadas terminan en una redirección Post/Redirect/Get.
- JSTL `<c:forEach>` construye el historial sin scriptlets de negocio en la vista.

No requiere base de datos ni APIs externas. Bootstrap 5.3 se sirve desde la CDN oficial de jsDelivr.

## Entrega exclusiva del Caso 1 y GitHub

El ZIP  contiene `java-wallet/` (proyecto fuente completo), `release/billetera-digital-java.war` (aplicación compilada) y `LEEME-ENTREGA.md`. 

Este proyecto se subió a GIBHUB y debe seleccionar **Code → Download ZIP**, descomprimir, copiar el WAR de `release/` a `webapps/` de Tomcat 10.1 e iniciar Tomcat. Necesita Java 17 o posterior y Tomcat 10.1; no necesita Maven para ese procedimiento. En Windows, configura `JAVA_HOME` con la carpeta de instalación del JDK y ejecuta `bin/startup.bat` de Tomcat; en Linux/macOS usa `bin/startup.sh`. Abre `http://localhost:8080/billetera-digital-java/`. Si el puerto está ocupado, cambia el puerto de Tomcat o detén el otro servicio. Para revisar y reconstruir el código, instalar además Maven y ejecuta `mvn clean test package` desde `java-wallet/`.

## Prueba manual local

1. En una sesión nueva: saldo S/ 250.00 e historial vacío.
2. Recarga 100: saldo S/ 350.00 y un movimiento.
3. Gasta 50: saldo S/ 300.00 y dos movimientos, con Gasto primero.
4. Gasta 400: error, saldo S/ 300.00 e historial sin cambios.
5. Envía un monto vacío, 0, -1 o 1.001: error sin modificar saldo ni historial. El Servlet también rechaza texto no numérico enviado directamente en una solicitud HTTP.
6. Recarga la página tras un registro aceptado: no se duplica el movimiento y conserva saldo e historial durante la sesión.
7. Gasta exactamente 300: saldo S/ 0.00. Un gasto adicional de 0.01 debe rechazarse.
8. Abre una ventana privada independiente: comienza con S/ 250.00 e historial vacío; no modifica la otra sesión.
