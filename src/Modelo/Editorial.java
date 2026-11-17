package Modelo;
import java.util.Map;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

    public class Editorial {
    public String Nombre;
    public int IdentificadorUnico;
    public Map<Stand,Fecha> Reservas = new HashMap<>();
    public ArrayList<Stand> Comprados = new ArrayList<>();

    private long calcularDiasTranscurridos(Fecha fechaReserva) {
        LocalDate hoy = LocalDate.now();
        LocalDate inicio = LocalDate.of(hoy.getYear(), fechaReserva.Mes, fechaReserva.Dia);

        // Si el apartado fue en diciembre del año pasado y hoy es enero, ajustamos el año
        if (inicio.isAfter(hoy)) {
            inicio = inicio.minusYears(1);
        }

        return ChronoUnit.DAYS.between(inicio, hoy);
    }

    public void ReservarStand(Stand stand, Fecha fecha) {
        this.Reservas.put(stand, fecha);
        stand.Estado = 1;
        BasedeDatos.GuardarReserva(this.IdentificadorUnico, stand, fecha);
    }

    public void ComprarStand(Stand stand) {
        this.Comprados.add(stand);
        stand.Estado = 2;
        BasedeDatos.GuardarCompra(this.IdentificadorUnico, stand);
    }

    public Editorial(String nombre){
        this.Nombre = nombre;
        this.IdentificadorUnico = nombre.hashCode();
        BasedeDatos.GuardarEditorial(this);
    }

        public void DesbloquearStand(Stand stand){
        stand.Estado = 0;
    }

    public String AvisoProntoPago(Stand stand, Fecha fecha) {
        long diasTranscurridos = calcularDiasTranscurridos(fecha);
        long diasFaltantes = Macros.DiasParaProntoPago - diasTranscurridos;

        if (diasFaltantes >= 0 && diasFaltantes <= 5) {
            return "Aviso: Faltan " + diasFaltantes + " días para el pronto pago del stand " + stand.Identificador;
        }
        return null;
    }

    public String AvisoDiasParaLimitePago(Stand stand, Fecha fecha) {
        long diasTranscurridos = calcularDiasTranscurridos(fecha);
        long diasFaltantes = Macros.LimitePago - diasTranscurridos;

        if (diasFaltantes >= 0 && diasFaltantes <= 5) {
            return "Aviso: Faltan " + diasFaltantes + " días para el límite de pago del stand " + stand.Identificador;
        }
        return null;
    }

    public String AvisoPagoTardio(Stand stand, Fecha fecha) {
        long diasTranscurridos = calcularDiasTranscurridos(fecha);

        if (diasTranscurridos > Macros.LimitePago) {
            long diasRetraso = diasTranscurridos - Macros.LimitePago;
            DesbloquearStand(stand);
            BasedeDatos.ActualizarEstadoStand(stand);
            return "Aviso: Se ha excedido por " + diasRetraso + " días la fecha límite de pago del stand " + stand.Identificador + " y se ha liberado...";
        }
        return null;
    }

    public List<String> ObtenerNotificacionesEditorial() {
        List<String> notificaciones = new ArrayList<>();
            for (Map.Entry<Stand, Fecha> entry : this.Reservas.entrySet()) {
                Stand stand = entry.getKey();
                Fecha fecha = entry.getValue();

                String avisoPronto = AvisoProntoPago(stand, fecha);
                if (avisoPronto != null) notificaciones.add(avisoPronto);

                String avisoLimite = AvisoDiasParaLimitePago(stand, fecha);
                if (avisoLimite != null) notificaciones.add(avisoLimite);

                String avisoTardio = AvisoPagoTardio(stand, fecha);
                if (avisoTardio != null) notificaciones.add(avisoTardio);
            }
        return notificaciones;  
    }
}
