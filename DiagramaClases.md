# Diagrama de Clases UML - Sistema de Gestión de Stands (FILEY)

## Leyenda de Relaciones y Notación UML

### Tipos de Relaciones
* **Asociación (`-->`):** Representa una referencia directa y persistente en memoria hacia otro objeto (por ejemplo, el controlador `Web` mantiene la sesión activa `EditorialActual`).
* **Agregación (`o--`):** Indica una relación de pertenencia débil ("tiene un"). `Editorial` agrupa colecciones de `Stand` (en `Comprados` y `Reservas`), pero los stands existen independientemente en el sistema y en la base de datos.
* **Composición (`*--`):** Indica una relación de pertenencia fuerte. Los objetos `Fecha` existen vinculados directamente al ciclo de vida de cada reserva dentro de `Editorial`.
* **Dependencia (`..>`):** Señala que una clase utiliza temporalmente a otra (invocando sus métodos estáticos o recibiéndola como parámetro), sin conservarla como atributo propio (por ejemplo, `VistaWeb` leyendo datos del `Modelo`).

### Modificadores de Visibilidad y Alcance
* **`+` Público (`public`):** Accesible desde cualquier paquete del proyecto.
* **`-` Privado (`private`):** Accesible únicamente dentro de la misma clase.
* **`~` Paquete (`default`):** Accesible únicamente para clases dentro del mismo paquete (`Modelo`).
* **`$` Estático (`static`):** Pertenece directamente a la clase y no requiere instanciar un objeto para ser invocado.

---

## Diagrama de Clases (Arquitectura MVC)

```mermaid
classDiagram
    direction TB

    class Main {
        +main(String[] args)$ void
    }

    namespace Controlador {
        class Web {
            +EditorialesRegistradas$ Map~String, Editorial~
            +EditorialActual$ Editorial
            +Iniciar()$ void
            -enviarRespuesta(HttpExchange, String)$ void
            -redirigir(HttpExchange, String)$ void
        }
    }

    namespace Vista {
        class VistaWeb {
            -barraNavegacion(String, String)$ String
            +generarLogin()$ String
            +generarMapa(Editorial, Vector~Stand~, Stand)$ String
            +generarMisStands(Editorial)$ String
            +generarNotificaciones(Editorial, List~String~)$ String
        }
    }

    namespace Modelo {
        class Editorial {
            +Nombre String
            +IdentificadorUnico int
            +Reservas Map~Stand, Fecha~
            +Comprados ArrayList~Stand~
            +Editorial(String nombre)
            -calcularDiasTranscurridos(Fecha) long
            +ReservarStand(Stand, Fecha) void
            +ComprarStand(Stand) void
            +CancelarReserva(Stand) void
            +AcompletarReserva(Stand, Fecha) void
            +DesbloquearStand(Stand) void
            +AvisoProntoPago(Stand, Fecha) String
            +AvisoDiasParaLimitePago(Stand, Fecha) String
            +AvisoPagoTardio(Stand, Fecha) String
            +ObtenerNotificacionesEditorial() List~String~
        }

        class Stand {
            +Identificador int
            +Estado int
            +Alto int
            +Ancho int
            +Costo double
            +Stand(int, int, int, int)
            +CalcularPrecioProntoPago(Stand)$ double
        }

        class Fecha {
            +Dia int
            +Mes int
            +hoy$ LocalDate
            +FechadeHoy$ Fecha
            +Fecha(int, int)
        }

        class Macros {
            ~CostoMetroCuadrado$ int
            ~DiasParaProntoPago$ int
            ~LimitePago$ int
            ~DescuentoProntoPago$ double
            +EstadoStand$ Map~Integer, String~
        }

        class BasedeDatos {
            -HOST$ String
            -PUERTO$ String
            -NOMBRE_BD$ String
            -USUARIO$ String
            -CONTRASENA$ String
            -URL$ String
            +conectar()$ Connection
            +CrearTablas()$ void
            +GuardarEditorial(Editorial)$ void
            +GuardarReserva(int, Stand, Fecha)$ void
            +GuardarCompra(int, Stand)$ void
            +GuardarStand(Stand)$ void
            +EncontrarStand(int)$ Stand
            +RastrearEditorialEspecial(Editorial)$ void
            +ActualizarEstadoStand(Stand)$ void
            +CargarStandsDeEditorial(Editorial)$ void
            +ObtenerStands()$ Vector~Stand~
            +MostrarTablasEnConsola()$ void
        }
    }

    %% Relaciones de arranque
    Main ..> BasedeDatos : inicializa tablas y precarga
    Main ..> Web : inicia servidor HTTP

    %% Relaciones del Controlador
    Web --> Editorial : gestiona sesión (EditorialActual)
    Web ..> VistaWeb : solicita vistas HTML
    Web ..> BasedeDatos : consulta y sincroniza datos
    Web ..> Stand : consulta estado

    %% Relaciones de la Vista
    VistaWeb ..> Editorial : lee reservas y compras
    VistaWeb ..> Stand : lee dimensiones y costo
    VistaWeb ..> Fecha : lee día y mes
    VistaWeb ..> Macros : consulta nombres de estado

    %% Relaciones dentro del Modelo
    Editorial "1" o-- "0..*" Stand : Comprados
    Editorial "1" o-- "0..*" Stand : Reservas (clave)
    Editorial "1" *-- "0..*" Fecha : Reservas (valor)
    Editorial ..> BasedeDatos : persiste cambios
    Editorial ..> Macros : consulta plazos de pago
    Stand ..> Macros : calcula costo y descuento
    BasedeDatos ..> Editorial : reconstruye objetos
    BasedeDatos ..> Stand : lee y escribe registros
    BasedeDatos ..> Fecha : reconstruye fechas
```