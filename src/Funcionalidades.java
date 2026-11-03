package src;

import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class Funcionalidades {

    public static String AvisoProntoPago(Editorial editorial, Stand stand, Fecha fecha){
        int diasFaltantes = Macros.DiasParaProntoPago - fecha.Dia;
        if (diasFaltantes <= 5) {
            return "Aviso: Faltan " + diasFaltantes + " días para el pronto pago del stand " + stand.Identificador;
        }
        return null;
    }

    public static String AvisoDiasParaLimitePago(Editorial editorial, Stand stand, Fecha fecha){
        int diasFaltantes = Macros.LimitePago - fecha.Dia;
        if(diasFaltantes <= 5) {
            return "Aviso: Faltan " + diasFaltantes + " días para el límite de pago del stand " + stand.Identificador;
        }
        return null;
    }

    public static String AvisoPagoTardio(Editorial editorial, Stand stand, Fecha fecha){ 
        if(fecha.Dia > Macros.LimitePago) {
            return "Aviso: Se ha excedido la fecha límite de pago del stand " + stand.Identificador;
        }
        return null;
    }

    public static List<String> ObtenerNotificacionesEditorial(Editorial editorial) {
        List<String> notificaciones = new ArrayList<>();
        if (editorial == null || editorial.Reservas == null) return notificaciones;

        for (Map.Entry<Stand, Fecha> entry : editorial.Reservas.entrySet()) {
            Stand stand = entry.getKey();
            Fecha fecha = entry.getValue();

            String avisoPronto = AvisoProntoPago(editorial, stand, fecha);
            if (avisoPronto != null) notificaciones.add(avisoPronto);

            String avisoLimite = AvisoDiasParaLimitePago(editorial, stand, fecha);
            if (avisoLimite != null) notificaciones.add(avisoLimite);

            String avisoTardio = AvisoPagoTardio(editorial, stand, fecha);
            if (avisoTardio != null) notificaciones.add(avisoTardio);
        }
        return notificaciones;
    }

    public static void DesbloquearStand(Stand stand){
        stand.Estado = 0;
    }

    public static Stand EncontrarStand(int identificador){
        for (Stand stand : Macros.Stands) {
            if (stand.Identificador == identificador) {
                return stand;
            }
        }
        System.out.println("Stand con identificador " + identificador + " no encontrado.");
        return null;
    }

    public static void RastrearStandEspecial(Editorial editorial){
        if (editorial == null) return;

        Integer idStandEspecial = Macros.EditorialesEspeciales.get(editorial.IdentificadorUnico);

        if (idStandEspecial != null) {
            Stand stand = EncontrarStand(idStandEspecial);
            if (stand != null && stand.Estado == 3) {
                DesbloquearStand(stand);
                System.out.println("Stand " + idStandEspecial + " desbloqueado para " + editorial.Nombre);
            }
        }
    }
}
