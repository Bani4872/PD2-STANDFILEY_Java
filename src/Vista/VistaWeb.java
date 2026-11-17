package Vista;

import java.util.List;
import java.util.Map;
import java.util.Vector;
import Modelo.*;

public class VistaWeb {

    // Componente reutilizable para no repetir el menú en todas las páginas
    private static String barraNavegacion(String nombreEditorial, String pestanaActiva) {
        String colorMapa = pestanaActiva.equals("mapa") ? "#4CAF50" : "white";
        String colorNotif = pestanaActiva.equals("notificaciones") ? "#4CAF50" : "white";
        String colorStands = pestanaActiva.equals("mis-stands") ? "#4CAF50" : "white";

        return "<div style='background: #333; padding: 10px; color: white;'>" +
               "<b>Editorial: " + nombreEditorial + "</b> | " +
               "<a href='/mapa' style='color: " + colorMapa + "; margin: 0 10px;'>Mapa de Stands</a> | " +
               "<a href='/notificaciones' style='color: " + colorNotif + "; margin: 0 10px;'>Notificaciones</a> | " +
               "<a href='/mis-stands' style='color: " + colorStands + "; margin: 0 10px;'>Mis Stands</a> | " +
               "<a href='/' style='color: #ff4d4d; margin: 0 10px;'>Salir</a>" +
               "</div>";
    }

    public static String generarLogin() {
        return "<html><body style='font-family: Arial; text-align: center; margin-top: 50px;'>" +
               "<h1>Bienvenido a la gestión de Stands (FILEY)</h1>" +
               "<p>Introduce el nombre de tu editorial para iniciar sesión o registrarte:</p>" +
               "<form action='/login' method='GET'>" +
               "<input type='text' name='nombre' required style='padding: 5px;'>" +
               "<button type='submit' style='padding: 5px;'>Ingresar</button>" +
               "</form></body></html>";
    }

    public static String generarMapa(Editorial editorialActual, Vector<Stand> todosLosStands, Stand standSeleccionado) {
        String nombreUsuario = (editorialActual != null) ? editorialActual.Nombre : "Desconocido";
        StringBuilder html = new StringBuilder();

        html.append("<html><body style='font-family: Arial; background-color: #f4f4f9; margin: 0;'>")
            .append(barraNavegacion(nombreUsuario, "mapa"))
            .append("<h2 style='text-align:center;'>Croquis FILEY</h2>")
            .append("<div style='display: grid; grid-template-columns: repeat(5, 90px); grid-template-rows: repeat(3, 90px); gap: 20px; " +
                    "justify-content: center; margin-top: 30px; background-color: #e0e0e0; padding: 30px; border: 3px dashed #888; width: fit-content; margin-left: auto; margin-right: auto;'>");

        for (Stand stand : todosLosStands) {
            if (stand.Identificador < 1 || stand.Identificador > 10) continue;

            String colorFondo;
            switch (stand.Estado) {
                case 0: colorFondo = "#4CAF50"; break;
                case 1: colorFondo = "#ffc73b"; break;
                case 2: colorFondo = "#FF0000"; break;
                case 3: colorFondo = "#9E9E9E"; break;
                default: colorFondo = "#FFFFFF";
            }

            String nombreEstado = Macros.EstadoStand.get(stand.Estado);
            String detallesHover = String.format("Stand: %d | Estado: %s | Dim: %dx%d | Costo: $%.2f",
                    stand.Identificador, nombreEstado, stand.Alto, stand.Ancho, stand.Costo);

            String coordenadasGrid = "";
            switch (stand.Identificador) {
                case 1: coordenadasGrid = "grid-column: 1; grid-row: 1;"; break;
                case 2: coordenadasGrid = "grid-column: 2; grid-row: 1;"; break;
                case 3: coordenadasGrid = "grid-column: 4; grid-row: 1;"; break;
                case 4: coordenadasGrid = "grid-column: 5; grid-row: 1;"; break;
                case 5: coordenadasGrid = "grid-column: 1; grid-row: 3;"; break;
                case 6: coordenadasGrid = "grid-column: 2; grid-row: 3;"; break;
                case 7: coordenadasGrid = "grid-column: 4; grid-row: 3;"; break;
                case 8: coordenadasGrid = "grid-column: 5; grid-row: 3;"; break;
                case 9: coordenadasGrid = "grid-column: 1; grid-row: 2;"; break;
                case 10: coordenadasGrid = "grid-column: 5; grid-row: 2;"; break;
            }

            html.append(String.format(
                "<a href='/mapa?id=%d' title='%s' style='text-decoration: none; color: black; %s'>" +
                "<div style='background: %s; width: 100%%; height: 100%%; display: flex; align-items: center; justify-content: center; " +
                "border-radius: 4px; border: 2px solid #000; font-weight: bold; font-size: 22px; transition: transform 0.2s; cursor: pointer; box-shadow: 2px 2px 5px rgba(0,0,0,0.3);' " +
                "onmouseover=\"this.style.transform='scale(1.08)'\" onmouseout=\"this.style.transform='scale(1)'\">%d</div></a>",
                stand.Identificador, detallesHover, coordenadasGrid, colorFondo, stand.Identificador));
        }
        html.append("</div>");

        if (standSeleccionado != null) {
            html.append("<div style='position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.7); display: flex; align-items: center; justify-content: center;'>")
                .append("<div style='background: white; padding: 20px; border-radius: 10px; width: 300px; text-align: center; position: relative;'>")
                .append("<a href='/mapa' style='position: absolute; top: 10px; right: 15px; text-decoration: none; font-size: 20px; color: red; font-weight: bold;'>X</a>")
                .append("<h3>Opciones Stand ").append(standSeleccionado.Identificador).append("</h3>")
                .append("<p>Dimensiones: ").append(standSeleccionado.Alto).append("x").append(standSeleccionado.Ancho).append("</p>")
                .append("<p>Costo Original: $").append(standSeleccionado.Costo).append("</p>");

            if (standSeleccionado.Estado == 0) {
                html.append("<a href='/gestionar?id=").append(standSeleccionado.Identificador).append("&accion=reservar' style='display: block; padding: 10px; background: #ffc73b; margin: 10px; text-decoration: none; color: black; font-weight: bold; border-radius: 5px;'>Reservar Stand</a>")
                    .append("<a href='/gestionar?id=").append(standSeleccionado.Identificador).append("&accion=comprar' style='display: block; padding: 10px; background: #FF0000; margin: 10px; text-decoration: none; color: white; font-weight: bold; border-radius: 5px;'>Comprar Stand</a>");
            } else {
                html.append("<p style='color: red;'><b>Este stand no está disponible (Estado: ").append(Macros.EstadoStand.get(standSeleccionado.Estado)).append(").</b></p>");
            }
            html.append("</div></div>");
        }

        html.append("</body></html>");
        return html.toString();
    }

    public static String generarMisStands(Editorial editorialActual) {
        StringBuilder html = new StringBuilder();
        html.append("<html><body style='font-family: Arial; background-color: #f4f4f9; margin: 0;'>")
            .append(barraNavegacion(editorialActual.Nombre, "mis-stands"))
            .append("<div style='max-width: 800px; margin: 40px auto; background: white; padding: 30px; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1);'>")
            .append("<h2 style='color: #333; border-bottom: 3px solid #333; padding-bottom: 10px;'>Panel de Control: ").append(editorialActual.Nombre).append("</h2>");

        html.append("<h3 style='color: #FF0000; margin-top: 20px;'>Stands Comprados</h3>");
        if (editorialActual.Comprados.isEmpty()) {
            html.append("<p style='color: #666;'>No has comprado ningún stand aún.</p>");
        } else {
            html.append("<ul>");
            for (Stand stand : editorialActual.Comprados) {
                html.append("<li style='margin-bottom: 10px; padding: 10px; background: #ffe6e6; border-radius: 5px;'>")
                    .append("<b>Stand ").append(stand.Identificador).append("</b> - ")
                    .append("Dimensiones: ").append(stand.Alto).append("x").append(stand.Ancho)
                    .append(" | Pagado: $").append(stand.Costo).append("</li>");
            }
            html.append("</ul>");
        }

        html.append("<h3 style='color: #e6b800; margin-top: 30px;'>Stands Reservados</h3>");
        if (editorialActual.Reservas.isEmpty()) {
            html.append("<p style='color: #666;'>No tienes reservas activas.</p>");
        } else {
            html.append("<ul>");
            for (Map.Entry<Stand, Fecha> entrada : editorialActual.Reservas.entrySet()) {
                Stand stand = entrada.getKey();
                Fecha fecha = entrada.getValue();

                double precioProntoPago = Stand.CalcularPrecioProntoPago(stand);
                html.append("<li style='margin-bottom: 15px; padding: 15px; background: #fffde6; border-radius: 5px; border-left: 5px solid #ffc73b;'>")
                    .append("<b style='font-size: 18px;'>Stand ").append(stand.Identificador).append("</b><br>")
                    .append("Fecha de apartado: ").append(fecha.Dia).append("/").append(fecha.Mes).append("<br>")
                    .append("Costo original: <del>$").append(stand.Costo).append("</del><br>")
                    .append("<span style='color: #2e7d32; font-size: 18px;'><b>Precio con Pronto Pago (15% desc): $")
                    .append(precioProntoPago).append("</b></span></li>");
            }
            html.append("</ul>");
        }

        html.append("</div></body></html>");
        return html.toString();
    }

    public static String generarNotificaciones(Editorial editorialActual, List<String> avisos) {
        StringBuilder html = new StringBuilder();
        html.append("<html><body style='font-family: Arial; background-color: #f4f4f9; margin: 0;'>")
            .append(barraNavegacion(editorialActual.Nombre, "notificaciones"))
            .append("<div style='max-width: 800px; margin: 40px auto; background: white; padding: 30px; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1);'>")
            .append("<h2 style='color: #333; border-bottom: 3px solid #333; padding-bottom: 10px;'>Centro de Notificaciones y Avisos</h2>");

        if (avisos.isEmpty()) {
            html.append("<p style='color: #666;'>No tienes reservas pendientes con avisos activos.</p>");
        } else {
            html.append("<div style='display: flex; flex-direction: column; gap: 15px;'>");
            for (String aviso : avisos) {
                html.append("<div style='background: #e8f5e9; border-left: 5px solid #4CAF50; padding: 15px; border-radius: 4px;'>")
                    .append("<p style='margin: 0; color: #d47735; font-weight: bold;'>").append(aviso).append("</p>")
                    .append("</div>");
            }
            html.append("</div>");
        }

        html.append("</div></body></html>");
        return html.toString();
    }
}