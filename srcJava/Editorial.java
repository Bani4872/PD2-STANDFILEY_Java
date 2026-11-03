package srcJava;
import java.util.*;

public class Editorial {
    public String Nombre;
    public int IdentificadorUnico;
    
    public Map<Stand,Fecha> Reservas;
    
    public List<Stand> Comprados;

    public Editorial(String nombre){
        this.Nombre = nombre;
        this.IdentificadorUnico = nombre.hashCode();

        this.Reservas = new HashMap<Stand, Fecha>();
        this.Comprados = new ArrayList<Stand>();
        Macros.Editoriales.add(this);
        return;
    }

    public void Reservar_Stand(Stand stand, Fecha fecha){
        this.Reservas.put(stand, fecha);
        stand.Estado = 1;
        return;
    }

    public void Comprar_Stand(Stand stand){
        this.Comprados.add(stand);
        stand.Estado = 2;
        return;
    }

    public void Cancelar_Reserva(Stand stand){
        this.Reservas.remove(stand);
        stand.Estado = 0;
        return;
    }

    public void Acompletar_Reserva(Stand stand, Fecha fecha){
        this.Reservas.remove(stand);
        stand.Estado = 2;
        this.Comprados.add(stand);
        return;
    }

    public void Imprimir_Lista_De_Reservas(){
        System.out.println("Lista de reservas: ");
        System.out.println("-----------------------");
        for (Map.Entry<Stand, Fecha> entry : this.Reservas.entrySet()) {
            Stand stand = entry.getKey();
            Fecha fecha = entry.getValue();
            System.out.println("Stand: " + stand.Identificador + " \nDimensiones: " + stand.Alto + "x" + stand.Ancho + "\nFecha de reserva: " + fecha.Dia + "/" + fecha.Mes + "\nPrecio: " + stand.Costo + " (Pronto pago: " + stand.Costo * Macros.DescuentoProntoPago + ")" + "\n-----------------------");
        }
    }

    public void Procesar_Notificaciones_Y_Vencimientos() {
    // Usamos una lista auxiliar para evitar modificar la colección mientras se recorre
    List<Stand> aCancelar = new ArrayList<>();

    for (Map.Entry<Stand, Fecha> entry : this.Reservas.entrySet()) {
        Stand stand = entry.getKey();
        Fecha fechaReserva = entry.getValue();

        int diasTranscurridos = fechaReserva.Dias_Hasta(Macros.FechadeHoy);
        int diasParaProntoPago = Macros.DiasParaProntoPago - diasTranscurridos;
        int diasParaLimite = Macros.LimitePago - diasTranscurridos;

        if (diasTranscurridos > Macros.LimitePago) {
            System.out.println("Aviso: Se ha excedido la fecha límite de pago del stand " 
                    + stand.Identificador + ". La reserva ha sido liberada.");
            aCancelar.add(stand);
        } else if (diasParaLimite <= 5) {
            System.out.println("Aviso: Faltan " + diasParaLimite 
                    + " días para el límite de pago del stand " + stand.Identificador);
        } else if (diasParaProntoPago <= 5 && diasParaProntoPago >= 0) {
            System.out.println("Aviso: Faltan " + diasParaProntoPago 
                    + " días para el pronto pago del stand " + stand.Identificador);
        }
    }

    // Cancelar reservas vencidas de forma efectiva
    for (Stand stand : aCancelar) {
        this.Cancelar_Reserva(stand);
    }
}
}
