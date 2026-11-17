# Documentación Técnica: Sistema de Gestión de Stands (FILEY)

## 1. Descripción General
El proyecto es una aplicación web desarrollada en **Java** bajo el patrón arquitectónico **Modelo-Vista-Controlador (MVC)** para administrar la reserva y compra de stands en la Feria Internacional de la Lectura Yucatán (FILEY). El sistema utiliza un servidor HTTP nativo (`com.sun.net.httpserver.HttpServer`) en el puerto `8080` y persiste toda la información en una base de datos relacional **MySQL alojada en Clever Cloud**.

---

## 2. Arquitectura del Proyecto
El código fuente está estructurado en tres paquetes principales más un punto de entrada en la raíz:

```text
PD2-STANDFILEY_Java/
├── lib/
│   └── mysql-connector-j-26.10.0.jar   # Driver JDBC para conexión con MySQL
└── src/
    ├── Main.java                       # Punto de arranque e inicialización
    ├── Controlador/
    │   └── Web.java                    # Servidor HTTP, enrutamiento y flujo de control
    ├── Modelo/
    │   ├── BasedeDatos.java            # Persistencia en Clever Cloud (JDBC / SQL)
    │   ├── Editorial.java              # Entidad Editorial y reglas de negocio/avisos
    │   ├── Fecha.java                  # Representación de fechas (Día / Mes)
    │   ├── Macros.java                 # Constantes globales y catálogo de estados
    │   └── Stand.java                  # Entidad Stand y cálculo de costos
    └── Vista/
        └── VistaWeb.java               # Generación dinámica de interfaces HTML/CSS
```

---

## 3. Esquema de Base de Datos (MySQL - Clever Cloud)
La clase `BasedeDatos` crea y gestiona automáticamente cinco tablas relacionales:

1. **`Editoriales`**: Almacena los usuarios registrados (`identificador` generado mediante `hashCode()`, `nombre`).
2. **`Stands`**: Almacena el inventario de espacios (`Identificador`, `Estado`, `Alto`, `Ancho`, `Costo`).
   * *Estados:* `0` = Disponible, `1` = Reservado, `2` = Comprado, `3` = Bloqueado.
3. **`Reservas`**: Relaciona una editorial con un stand apartado (`Id_Reserva`, `Id_Editorial`, `Id_Stand`, `Dia`, `Mes`).
4. **`Compras`**: Registra las adquisiciones definitivas (`Id_Compra`, `Id_Editorial`, `Id_Stand`).
5. **`EditorialesEspeciales`**: Asigna stands exclusivos pre-bloqueados para editoriales invitadas (`UADY` $\rightarrow$ Stand 5, `Planeta` $\rightarrow$ Stand 3, `Diario de Yucatán` $\rightarrow$ Stand 4).

---

## 4. Documentación por Clases y Capas

### Punto de Entrada (`src/Main.java`)
* **`main(String[] args)`**:
  1. Invoca `BasedeDatos.CrearTablas()` para verificar o construir el esquema en la nube.
  2. Comprueba si la tabla `Stands` está vacía mediante `BasedeDatos.ObtenerStands().isEmpty()`; de ser así, precarga los 10 stands iniciales (incluyendo los 3 bloqueados para editoriales especiales).
  3. Enciende el servidor web llamando a `Web.Iniciar()`.

---

### Capa Modelo (`src/Modelo/`)

#### `Macros.java`
Centraliza los parámetros de configuración del negocio:
* `CostoMetroCuadrado = 10`
* `DiasParaProntoPago = 15`
* `LimitePago = 30`
* `DescuentoProntoPago = 0.15` (15%)
* `EstadoStand`: Mapa inmutable que traduce los códigos numéricos (`0` a `3`) a texto legible.

#### `Fecha.java`
* **Atributos**: `Dia` (`int`), `Mes` (`int`), y las referencias estáticas `hoy` (`LocalDate`) y `FechadeHoy` (`Fecha`).
* **Constructor**: `Fecha(int Dia, int Mes)` inicializa una fecha específica de apartado.

#### `Stand.java`
* **Atributos**: `Identificador`, `Estado`, `Alto`, `Ancho`, `Costo`.
* **Constructor**: Calcula automáticamente el costo base (`Alto * Ancho * CostoMetroCuadrado`).
* **`CalcularPrecioProntoPago(Stand stand)`**: Método estático que devuelve el costo del stand aplicando el 15% de descuento por pronto pago.

#### `Editorial.java`
* **Atributos**: `Nombre`, `IdentificadorUnico` (`nombre.hashCode()`), `Reservas` (`Map<Stand, Fecha>`) y `Comprados` (`ArrayList<Stand>`).
* **Métodos de gestión**:
  * `ReservarStand(Stand, Fecha)`: Registra la reserva en memoria, cambia el estado del stand a `1` y persiste en MySQL.
  * `ComprarStand(Stand)`: Agrega el stand a `Comprados`, cambia su estado a `2` y persiste en MySQL.
  * `DesbloquearStand(Stand)`: Restablece el estado del stand a `0` (Disponible).
* **Métodos de notificaciones y reglas de fechas**:
  * `calcularDiasTranscurridos(Fecha)`: Convierte la fecha de reserva a `LocalDate` y calcula los días naturales transcurridos hasta hoy usando `ChronoUnit.DAYS.between()`, ajustando el cambio de año si aplica.
  * `AvisoProntoPago(Stand, Fecha)`: Genera un aviso cuando faltan entre `0` y `5` días para cumplir los 15 días de pronto pago.
  * `AvisoDiasParaLimitePago(Stand, Fecha)`: Genera un aviso cuando faltan entre `0` y `5` días para el límite de 30 días.
  * `AvisoPagoTardio(Stand, Fecha)`: Si se superan los 30 días, libera el stand (`Estado = 0`), actualiza la base de datos y notifica el retraso.
  * `ObtenerNotificacionesEditorial()`: Recorre el mapa `Reservas` y recopila todos los avisos vigentes para la editorial.

#### `BasedeDatos.java`
Gestiona todas las operaciones CRUD con MySQL mediante `try-with-resources` y consultas preparadas (`PreparedStatement`):
* `conectar()`: Abre la conexión JDBC hacia Clever Cloud.
* `CrearTablas()`: Crea las 5 tablas e inserta las editoriales especiales.
* `GuardarEditorial`, `GuardarStand`, `GuardarReserva`, `GuardarCompra`: Inserta o actualiza registros en sus respectivas tablas y sincroniza el estado en `Stands`.
* `EncontrarStand(int)` / `ObtenerStands()`: Recupera uno o todos los stands de la nube reconstruyendo objetos `Stand`.
* `RastrearEditorialEspecial(Editorial)`: Verifica si la editorial que inicia sesión tiene un stand bloqueado asignado y lo desbloquea.
* `CargarStandsDeEditorial(Editorial)`: Ejecuta consultas `INNER JOIN` entre `Reservas`/`Compras` y `Stands` para poblar las colecciones `Reservas` y `Comprados` del objeto `Editorial` en memoria.
* `ActualizarEstadoStand(Stand)`: Actualiza la columna `Estado` en MySQL.

---

### Capa Vista (`src/Vista/VistaWeb.java`)
Clase sin estado que recibe objetos del Modelo y devuelve cadenas HTML formateadas con CSS en línea:
* `barraNavegacion(nombreEditorial, pestanaActiva)`: Renderiza la barra superior común con enlaces a `/mapa`, `/notificaciones`, `/mis-stands` y `/`.
* `generarLogin()`: Dibuja el formulario de acceso por nombre de editorial.
* `generarMapa(editorialActual, todosLosStands, standSeleccionado)`: Construye una cuadrícula CSS Grid (`5x3`) con los 10 stands coloreados según su estado (verde = disponible, amarillo = reservado, rojo = comprado, gris = bloqueado). Si `standSeleccionado` no es nulo, superpone una ventana modal con opciones de compra o reserva.
* `generarMisStands(editorialActual)`: Lista los stands comprados y los reservados, mostrando para estos últimos la fecha de apartado, el costo original y el precio con descuento de pronto pago.
* `generarNotificaciones(editorialActual, avisos)`: Muestra en tarjetas visuales los mensajes generados por `ObtenerNotificacionesEditorial()`.

---

### Capa Controlador (`src/Controlador/Web.java`)
* `Iniciar()`: Levanta el servidor en `http://localhost:8080` y define 6 rutas:
  * **`/`**: Muestra el login (`VistaWeb.generarLogin()`).
  * **`/login`**: Decodifica el nombre ingresado, crea `EditorialActual`, desbloquea su stand especial si aplica, carga sus stands desde Clever Cloud y redirige a `/mapa`.
  * **`/mapa`**: Obtiene todos los stands (y el stand seleccionado si recibe `?id=`) y renderiza el croquis.
  * **`/gestionar`**: Procesa `?id=X&accion=reservar|comprar` validando que el stand esté disponible (`Estado == 0`) y redirige de vuelta a `/mapa`.
  * **`/mis-stands`**: Sincroniza los stands de la editorial desde la nube y muestra su panel de control.
  * **`/notificaciones`**: Evalúa los avisos de fechas de la editorial activa y renderiza el centro de notificaciones.
* `enviarRespuesta(HttpExchange, String)` y `redirigir(HttpExchange, String)`: Métodos auxiliares para despachar respuestas HTTP `200` (HTML) y `302` (redirecciones).

---

## 5. Flujo Completo de Operación
1. **Inicio**: `Main` verifica las tablas en Clever Cloud, carga los 10 stands si es la primera vez y arranca el servidor web.
2. **Autenticación**: El usuario ingresa el nombre de su editorial en `/`. El controlador `/login` registra la editorial, desbloquea su stand especial (si corresponde) y descarga sus reservas/compras previas.
3. **Interacción**: En `/mapa`, el usuario selecciona un stand verde, abre el modal y elige **Reservar** o **Comprar**. `/gestionar` actualiza el objeto `Editorial`, modifica el estado en Clever Cloud y recarga el mapa.
4. **Seguimiento**: En `/mis-stands` consulta sus propiedades y precios con descuento; en `/notificaciones` recibe alertas automáticas según los días transcurridos desde la fecha de reserva.