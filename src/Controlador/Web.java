package Controlador;

import com.sun.net.httpserver.HttpServer;

import BD.BasedeDatos;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import Modelo.*;
import Vista.VistaWeb;

public class Web {

    public static Map<String, Editorial> EditorialesRegistradas = new HashMap<>();
    public static Editorial EditorialActual = null;

    public static void Iniciar() {
        try {
            HttpServer servidor = HttpServer.create(new InetSocketAddress(8080), 0);

            // RUTA 1: INICIO DE SESIÓN
            servidor.createContext("/", intercambio -> {
                String html = VistaWeb.generarLogin();
                enviarRespuesta(intercambio, html);
            });

            // RUTA 2: PROCESAR LOGIN
            servidor.createContext("/login", intercambio -> {
                String query = intercambio.getRequestURI().getQuery();
                if (query != null && query.startsWith("nombre=")) {
                    String textoCrudo = query.split("=")[1];
                    String nombreIngresado = URLDecoder.decode(textoCrudo, "UTF-8");

                    EditorialActual = new Editorial(nombreIngresado);
                    EditorialesRegistradas.put(nombreIngresado, EditorialActual);

                    BasedeDatos.RastrearEditorialEspecial(EditorialActual);
                    BasedeDatos.CargarStandsDeEditorial(EditorialActual);
                }
                redirigir(intercambio, "/mapa");
            });     

            // RUTA 3: MAPA DE STANDS
            servidor.createContext("/mapa", intercambio -> {
                String query = intercambio.getRequestURI().getQuery();
                Stand standSeleccionado = null;

                if (query != null && query.startsWith("id=")) {
                    try {
                        int id = Integer.parseInt(query.split("=")[1]);
                        standSeleccionado = BasedeDatos.EncontrarStand(id);
                    } catch (NumberFormatException ignored) {}
                }

                String html = VistaWeb.generarMapa(EditorialActual, BasedeDatos.ObtenerStands(), standSeleccionado);
                enviarRespuesta(intercambio, html);
            });

            // RUTA 4: RESERVAR O COMPRAR
            servidor.createContext("/gestionar", intercambio -> {
                if (EditorialActual == null) {
                    redirigir(intercambio, "/");
                    return;
                }

                String query = intercambio.getRequestURI().getQuery();
                int idStand = -1;
                String accion = "";

                if (query != null) {
                    for (String param : query.split("&")) {
                        if (param.startsWith("id=")) idStand = Integer.parseInt(param.split("=")[1]);
                        if (param.startsWith("accion=")) accion = param.split("=")[1];
                    }
                }

                if (idStand != -1 && !accion.isEmpty()) {
                    Stand stand = BasedeDatos.EncontrarStand(idStand);
                    if (stand != null && stand.Estado == 0) {
                        if (accion.equals("reservar")) {
                            EditorialActual.ReservarStand(stand, new Fecha(1, 11));
                        } else if (accion.equals("comprar")) {
                            EditorialActual.ComprarStand(stand);
                        }
                    }
                }
                redirigir(intercambio, "/mapa");
            });

            // RUTA 5: MIS STANDS
            servidor.createContext("/mis-stands", intercambio -> {
                if (EditorialActual == null) {
                    redirigir(intercambio, "/");
                    return;
                }
                BasedeDatos.CargarStandsDeEditorial(EditorialActual);
                String html = VistaWeb.generarMisStands(EditorialActual);
                enviarRespuesta(intercambio, html);
            });     

            // RUTA 6: NOTIFICACIONES
            servidor.createContext("/notificaciones", intercambio -> {
                if (EditorialActual == null) {
                    redirigir(intercambio, "/");
                    return;
                }
                List<String> avisos = EditorialActual.ObtenerNotificacionesEditorial();
                String html = VistaWeb.generarNotificaciones(EditorialActual, avisos);
                enviarRespuesta(intercambio, html);
            });

            servidor.start();
            System.out.println("Servidor: http://localhost:8080");

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void enviarRespuesta(HttpExchange intercambio, String html) throws IOException {
        intercambio.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        byte[] bytes = html.getBytes("UTF-8");
        intercambio.sendResponseHeaders(200, bytes.length);
        try (OutputStream salida = intercambio.getResponseBody()) {
            salida.write(bytes);
        }
    }

    private static void redirigir(HttpExchange intercambio, String ruta) throws IOException {
        intercambio.getResponseHeaders().set("Location", ruta);
        intercambio.sendResponseHeaders(302, -1);
        intercambio.close();
    }
}
