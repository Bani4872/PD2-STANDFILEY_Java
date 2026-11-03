package src;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class Web {
    
    // Aquí guardaremos las editoriales en memoria para el inicio de sesión
    public static Map<String, Editorial> EditorialesRegistradas = new HashMap<>();
    public static Editorial EditorialActual = null; // Simula la "sesión" activa

    public static void Iniciar() {
        try {
            HttpServer servidor = HttpServer.create(new InetSocketAddress(8080), 0);
            
            // RUTA 1: PESTAÑA DE INICIO DE SESIÓN
            servidor.createContext("/", new HttpHandler() {
                @Override
                public void handle(HttpExchange intercambio) throws IOException {
                    // Solución a las tildes: Declarar UTF-8
                    intercambio.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                    
                    String html = "<html><body style='font-family: Arial; text-align: center; margin-top: 50px;'>" +
                                  "<h1>Bienvenido a la gestión de Stands (FILEY)</h1>" +
                                  "<p>Introduce el nombre de tu editorial para iniciar sesión o registrarte:</p>" +
                                  "<form action='/login' method='GET'>" +
                                  "<input type='text' name='nombre' required style='padding: 5px;'>" +
                                  "<button type='submit' style='padding: 5px;'>Ingresar</button>" +
                                  "</form>" +
                                  "</body></html>";
                    
                    enviarRespuesta(intercambio, html);
                }
            });

            // RUTA 2: PROCESADOR DEL INICIO DE SESIÓN
            servidor.createContext("/login", new HttpHandler() {
                @Override
                public void handle(HttpExchange intercambio) throws IOException {
                    String query = intercambio.getRequestURI().getQuery();
                    if (query != null && query.startsWith("nombre=")) {
                        
                        // SOLUCIÓN: Usar el decodificador oficial de Java para interpretar espacios y tildes reales
                        String textoCrudo = query.split("=")[1];
                        String nombreIngresado = java.net.URLDecoder.decode(textoCrudo, "UTF-8");
                        
                        if (!EditorialesRegistradas.containsKey(nombreIngresado)) {
                            EditorialesRegistradas.put(nombreIngresado, new Editorial(nombreIngresado));
                        }
                        
                        EditorialActual = EditorialesRegistradas.get(nombreIngresado);
                        Funcionalidades.RastrearStandEspecial(EditorialActual);
                    }
                    
                    intercambio.getResponseHeaders().set("Location", "/mapa");
                    intercambio.sendResponseHeaders(302, -1);
                    intercambio.close();
                }
            });

            // RUTA 3: PESTAÑA DEL MAPA (VINCULACIÓN DINÁMICA POR IDENTIFICADOR)
            servidor.createContext("/mapa", new HttpHandler() {
                @Override
                public void handle(HttpExchange intercambio) throws IOException {
                    intercambio.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                    String nombreUsuario = (EditorialActual != null) ? EditorialActual.Nombre : "Desconocido";
                    
                    String query = intercambio.getRequestURI().getQuery();
                    Integer standSeleccionadoId = null;
                    if (query != null && query.startsWith("id=")) {
                        try {
                            standSeleccionadoId = Integer.parseInt(query.split("=")[1]);
                        } catch (Exception e) { }
                    }
                    
                    StringBuilder html = new StringBuilder();
                    html.append("<html><body style='font-family: Arial; background-color: #f4f4f9; margin: 0;'>");
                    
                    // Barra de navegación
                    html.append("<div style='background: #333; padding: 10px; color: white;'>")
                        .append("<b>Editorial: ").append(nombreUsuario).append("</b> | ")
                        .append("<a href='/mapa' style='color: #4CAF50; margin: 0 10px;'>Mapa de Stands</a> | ")
                        .append("<a href='/notificaciones' style='color: white; margin: 0 10px;'>Notificaciones</a> | ")
                        .append("<a href='/mis-stands' style='color: white; margin: 0 10px;'>Mis Stands</a> | ")
                        .append("<a href='/' style='color: #ff4d4d; margin: 0 10px;'>Salir</a>")
                        .append("</div>");
                        
                    html.append("<h2 style='text-align:center;'>Croquis FILEY - Vinculado a Objetos de Java</h2>");
                    
                    // CROQUIS CON ESTRUCTURA Y PASILLOS FIJOS
                    html.append("<div style='display: grid; grid-template-columns: repeat(5, 90px); grid-template-rows: repeat(3, 90px); gap: 20px; " +
                                "justify-content: center; margin-top: 30px; background-color: #e0e0e0; padding: 30px; border: 3px dashed #888; width: fit-content; margin-left: auto; margin-right: auto;'>");
                    
                    // Arreglo de posiciones fijas del croquis
                    int[] posicionesCroquis = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
                    
                    for (int idSlot : posicionesCroquis) {
                        // VINCULACIÓN CLAVE: Buscamos el objeto Stand instanciado en Main que coincida con esta posición
                        Stand standReal = Funcionalidades.EncontrarStand(idSlot);
                        
                        if (standReal != null) {
                            // Leemos los atributos reales cargados en memoria RAM desde Main.java
                            String colorFondo;
                            switch (standReal.Estado) {
                                case 0: colorFondo = "#4CAF50"; break; // Verde (Disponible)
                                case 1: colorFondo = "#ffc73b"; break; // Amarillo (Reservado)
                                case 2: colorFondo = "#FF0000"; break; // Rojo (Comprado)
                                case 3: colorFondo = "#9E9E9E"; break; // Gris (Bloqueado)
                                default: colorFondo = "#FFFFFF";
                            }
                            
                            String nombreEstado = Macros.EstadoStand.get(standReal.Estado);
                            String detallesHover = String.format("Stand: %d | Estado: %s | Dim: %dx%d | Costo: $%.2f", 
                                    standReal.Identificador, nombreEstado, standReal.Alto, standReal.Ancho, standReal.Costo);
                            
                            // Coordenadas fijas en el plano para cada ID
                            String coordenadasGrid = "";
                            switch (idSlot) {
                                case 1: coordenadasGrid = "grid-column: 1; grid-row: 1;"; break;
                                case 2: coordenadasGrid = "grid-column: 2; grid-row: 1;"; break;
                                case 3: coordenadasGrid = "grid-column: 4; grid-row: 1;"; break; // Pasillo en col 3
                                case 4: coordenadasGrid = "grid-column: 5; grid-row: 1;"; break;
                                case 5: coordenadasGrid = "grid-column: 1; grid-row: 3;"; break;
                                case 6: coordenadasGrid = "grid-column: 2; grid-row: 3;"; break;
                                case 7: coordenadasGrid = "grid-column: 4; grid-row: 3;"; break;
                                case 8: coordenadasGrid = "grid-column: 5; grid-row: 3;"; break;
                                case 9: coordenadasGrid = "grid-column: 1; grid-row: 2;"; break;
                                case 10: coordenadasGrid = "grid-column: 5; grid-row: 2;"; break;
                            }
                            
                            // Dibujamos la casilla con los datos del objeto Stand real
                            html.append(String.format(
                                "<a href='/mapa?id=%d' title='%s' style='text-decoration: none; color: black; %s'>" +
                                "<div style='background: %s; width: 100%%; height: 100%%; display: flex; align-items: center; justify-content: center; " +
                                "border-radius: 4px; border: 2px solid #000; font-weight: bold; font-size: 22px; transition: transform 0.2s; cursor: pointer; box-shadow: 2px 2px 5px rgba(0,0,0,0.3);' " +
                                "onmouseover=\"this.style.transform='scale(1.08)'\" onmouseout=\"this.style.transform='scale(1)'\">" +
                                "%d" +
                                "</div></a>", 
                                standReal.Identificador, detallesHover, coordenadasGrid, colorFondo, standReal.Identificador));
                        }
                    }
                    html.append("</div>"); 
                    
                    // VENTANA MODAL (OPCIONES DE RESERVA/COMPRA)
                    if (standSeleccionadoId != null) {
                        Stand standObjetivo = Funcionalidades.EncontrarStand(standSeleccionadoId);
                        if (standObjetivo != null) {
                            html.append("<div style='position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.7); display: flex; align-items: center; justify-content: center;'>");
                            html.append("<div style='background: white; padding: 20px; border-radius: 10px; width: 300px; text-align: center; position: relative;'>");
                            html.append("<a href='/mapa' style='position: absolute; top: 10px; right: 15px; text-decoration: none; font-size: 20px; color: red; font-weight: bold;'>X</a>");
                            
                            html.append("<h3>Opciones Stand " + standObjetivo.Identificador + "</h3>");
                            html.append("<p>Dimensiones: " + standObjetivo.Alto + "x" + standObjetivo.Ancho + "</p>");
                            html.append("<p>Costo Original: $" + standObjetivo.Costo + "</p>");
                            
                            if (standObjetivo.Estado == 0) {
                                html.append("<a href='/gestionar?id=" + standObjetivo.Identificador + "&accion=reservar' style='display: block; padding: 10px; background: #ffc73b; margin: 10px; text-decoration: none; color: black; font-weight: bold; border-radius: 5px;'>Reservar Stand</a>");
                                html.append("<a href='/gestionar?id=" + standObjetivo.Identificador + "&accion=comprar' style='display: block; padding: 10px; background: #FF0000; margin: 10px; text-decoration: none; color: white; font-weight: bold; border-radius: 5px;'>Comprar Stand</a>");
                            } else {
                                html.append("<p style='color: red;'><b>Este stand no está disponible (Estado: " + Macros.EstadoStand.get(standObjetivo.Estado) + ").</b></p>");
                            }
                            html.append("</div></div>");
                        }
                    }

                    html.append("</body></html>");
                    enviarRespuesta(intercambio, html.toString());
                }
            });
                         

            // RUTA 4: PROCESADOR PARA RESERVAR O COMPRAR UN STAND
            servidor.createContext("/gestionar", new HttpHandler() {
                @Override
                public void handle(HttpExchange intercambio) throws IOException {
                    if (EditorialActual == null) {
                        intercambio.getResponseHeaders().set("Location", "/");
                        intercambio.sendResponseHeaders(302, -1);
                        intercambio.close();
                        return;
                    }

                    // Ahora recibimos dos parámetros, ej: ?id=5&accion=comprar
                    String query = intercambio.getRequestURI().getQuery();
                    int idStand = -1;
                    String accion = "";
                    
                    if (query != null) {
                        String[] parametros = query.split("&"); // Separamos por el símbolo &
                        for (String param : parametros) {
                            if (param.startsWith("id=")) idStand = Integer.parseInt(param.split("=")[1]);
                            if (param.startsWith("accion=")) accion = param.split("=")[1];
                        }
                    }
                    
                    // Buscamos el objeto stand y aplicamos la regla de negocio
                    if (idStand != -1 && !accion.isEmpty()) {
                        Stand standSeleccionado = Funcionalidades.EncontrarStand(idStand);
                        
                        if (standSeleccionado != null && standSeleccionado.Estado == 0) { // Si está disponible[cite: 14, 15]
                            Fecha fechaHoy = new Fecha(1, 11);
                            
                            if (accion.equals("reservar")) {
                                EditorialActual.ReservarStand(standSeleccionado, fechaHoy); //[cite: 12]
                            } else if (accion.equals("comprar")) {
                                EditorialActual.ComprarStand(standSeleccionado); //[cite: 12]
                            }
                        }
                    }
                    
                    // Terminamos la gestión y regresamos al mapa limpio
                    intercambio.getResponseHeaders().set("Location", "/mapa");
                    intercambio.sendResponseHeaders(302, -1);
                    intercambio.close();
                }
            });

            // RUTA 5: PESTAÑA DE "MIS STANDS" (Tus requisitos del README)
            servidor.createContext("/mis-stands", new HttpHandler() {
                @Override
                public void handle(HttpExchange intercambio) throws IOException {
                    // Si intentan entrar aquí sin iniciar sesión, los regresamos al inicio
                    if (EditorialActual == null) {
                        intercambio.getResponseHeaders().set("Location", "/");
                        intercambio.sendResponseHeaders(302, -1);
                        intercambio.close();
                        return;
                    }

                    intercambio.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                    StringBuilder html = new StringBuilder();
                    html.append("<html><body style='font-family: Arial; background-color: #f4f4f9; margin: 0;'>");
                    
                    // Barra de navegación (resaltando la pestaña actual en verde)
                    html.append("<div style='background: #333; padding: 10px; color: white;'>")
                        .append("<b>Editorial: ").append(EditorialActual.Nombre).append("</b> | ")
                        .append("<a href='/mapa' style='color: white; margin: 0 10px;'>Mapa de Stands</a> | ")
                        .append("<a href='/notificaciones' style='color: white; margin: 0 10px;'>Notificaciones</a> | ")
                        .append("<a href='/mis-stands' style='color: #4CAF50; margin: 0 10px;'>Mis Stands</a> | ")
                        .append("<a href='/' style='color: #ff4d4d; margin: 0 10px;'>Salir</a>")
                        .append("</div>");

                    // Contenedor principal estilo "tarjeta"
                    html.append("<div style='max-width: 800px; margin: 40px auto; background: white; padding: 30px; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1);'>");
                    html.append("<h2 style='color: #333; border-bottom: 3px solid #333; padding-bottom: 10px;'>Panel de Control: ").append(EditorialActual.Nombre).append("</h2>");
                    
                    // SECCIÓN 1: RECORRER STANDS COMPRADOS
                    html.append("<h3 style='color: #FF0000; margin-top: 20px;'>Stands Comprados (Propiedad definitiva)</h3>");
                    if (EditorialActual.Comprados.isEmpty()) { // Usamos tu ArrayList original
                        html.append("<p style='color: #666;'>No has comprado ningún stand aún.</p>");
                    } else {
                        html.append("<ul>");
                        for (Stand stand : EditorialActual.Comprados) {
                            html.append("<li style='margin-bottom: 10px; padding: 10px; background: #ffe6e6; border-radius: 5px;'>")
                                .append("<b>Stand ").append(stand.Identificador).append("</b> - ")
                                .append("Dimensiones: ").append(stand.Alto).append("x").append(stand.Ancho)
                                .append(" | Pagado: $").append(stand.Costo).append("</li>");
                        }
                        html.append("</ul>");
                    }
                    
                    // SECCIÓN 2: RECORRER RESERVAS Y CALCULAR PRONTO PAGO
                    html.append("<h3 style='color: #e6b800; margin-top: 30px;'>Stands Reservados (Pendientes de Pago)</h3>");
                    if (EditorialActual.Reservas.isEmpty()) { // Usamos tu HashMap original
                        html.append("<p style='color: #666;'>No tienes reservas activas.</p>");
                    } else {
                        html.append("<ul>");
                        // Recorremos el mapa tal como lo hacías en tu consola
                        for (Map.Entry<Stand, Fecha> entrada : EditorialActual.Reservas.entrySet()) {
                            Stand stand = entrada.getKey();
                            Fecha fecha = entrada.getValue();
                            
                            // Lógica matemática requerida en tu README (15% de descuento)
                            double descuento = stand.Costo * Macros.DescuentoProntoPago;
                            double precioProntoPago = stand.Costo - descuento;
                            
                            html.append("<li style='margin-bottom: 15px; padding: 15px; background: #fffde6; border-radius: 5px; border-left: 5px solid #ffc73b;'>")
                                .append("<b style='font-size: 18px;'>Stand ").append(stand.Identificador).append("</b><br>")
                                .append("Fecha de apartado: ").append(fecha.Dia).append("/").append(fecha.Mes).append("<br>")
                                .append("Costo original: <del>$").append(stand.Costo).append("</del><br>")
                                .append("<span style='color: #2e7d32; font-size: 18px;'><b>Precio con Pronto Pago (15% desc): $").append(precioProntoPago).append("</b></span></li>");
                        }
                        html.append("</ul>");
                    }
                    
                    html.append("</div></body></html>");
                    enviarRespuesta(intercambio, html.toString());
                }
            });
            
            // RUTA 6: PESTAÑA DE NOTIFICACIONES (Conectada a Funcionalidades.java)
            servidor.createContext("/notificaciones", new HttpHandler() {
                @Override
                public void handle(HttpExchange intercambio) throws IOException {
                    if (EditorialActual == null) {
                        intercambio.getResponseHeaders().set("Location", "/");
                        intercambio.sendResponseHeaders(302, -1);
                        intercambio.close();
                        return;
                    }

                    intercambio.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                    StringBuilder html = new StringBuilder();
                    html.append("<html><body style='font-family: Arial; background-color: #f4f4f9; margin: 0;'>");
                    
                    // Barra de navegación
                    html.append("<div style='background: #333; padding: 10px; color: white;'>")
                        .append("<b>Editorial: ").append(EditorialActual.Nombre).append("</b> | ")
                        .append("<a href='/mapa' style='color: white; margin: 0 10px;'>Mapa de Stands</a> | ")
                        .append("<a href='/notificaciones' style='color: #4CAF50; margin: 0 10px;'>Notificaciones</a> | ")
                        .append("<a href='/mis-stands' style='color: white; margin: 0 10px;'>Mis Stands</a> | ")
                        .append("<a href='/' style='color: #ff4d4d; margin: 0 10px;'>Salir</a>")
                        .append("</div>");

                    html.append("<div style='max-width: 800px; margin: 40px auto; background: white; padding: 30px; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1);'>");
                    html.append("<h2 style='color: #333; border-bottom: 3px solid #333; padding-bottom: 10px;'>Centro de Notificaciones y Avisos</h2>");

                    // CONSUMO DIRECTO DE TU CÓDIGO JAVA
                    java.util.List<String> avisos = Funcionalidades.ObtenerNotificacionesEditorial(EditorialActual);

                    if (avisos.isEmpty()) {
                        html.append("<p style='color: #666;'>No tienes reservas pendientes con avisos activos.</p>");
                    } else {
                        html.append("<div style='display: flex; flex-direction: column; gap: 15px;'>");
                        for (String aviso : avisos) {
                            html.append("<div style='background: #e8f5e9; border-left: 5px solid #4CAF50; padding: 15px; border-radius: 4px;'>")
                                .append("<p style='margin: 0; color: #2e7d32; font-weight: bold;'>").append(aviso).append("</p>")
                                .append("</div>");
                        }
                        html.append("</div>");
                    }

                    html.append("</div></body></html>");
                    enviarRespuesta(intercambio, html.toString());
                }
            });
            
            servidor.start();
            System.out.println("Servidor web encendido. Abre tu navegador en: http://localhost:8080");
            
        } catch (IOException e) {
            System.out.println("Error al iniciar el servidor: " + e.getMessage());
        }
    }

    

    // Función auxiliar para no repetir código al enviar el HTML
    private static void enviarRespuesta(HttpExchange intercambio, String html) throws IOException {
        byte[] bytes = html.getBytes("UTF-8"); // Aseguramos que los bytes se codifiquen en UTF-8
        intercambio.sendResponseHeaders(200, bytes.length);
        OutputStream salida = intercambio.getResponseBody();
        salida.write(bytes);
        salida.close();
    }
}
