package src;
import java.util.Map;

public class Funcionalidades {
    public static void AvisoProntoPago(Editorial editorial, Stand stand, Fecha fecha){
        System.out.println("Aviso: Faltan: " + (Macros.DiasParaProntoPago - fecha.Dia) + " dias para el pronto pago del stand " + stand.Identificador);
    }

    public static void AvisoDiasParaLimitePago(Editorial editorial, Stand stand, Fecha fecha){
        System.out.println("Aviso: Faltan: " + (Macros.LimitePago - fecha.Dia) + " dias para el limite de pago del stand " + stand.Identificador);
    }

    public static void AvisoPagoTardio(Editorial editorial, Stand stand, Fecha fecha){ 
        System.out.println("Aviso: Se ha excedido la fecha limite de pago del stand " + stand.Identificador);
    }

    public static void ImprimirListaDeReservas(Editorial editorial){
        System.out.println("Lista de reservas de la editorial " + editorial.Nombre + ":");
        for (Map.Entry<Stand, Fecha> entry : editorial.Reservas.entrySet()) {
            Stand stand = entry.getKey();
            Fecha fecha = entry.getValue();
            System.out.println("Stand: " + stand.Identificador + "Dimensiones: " + stand.Alto + "x" + stand.Ancho + "\nFecha de reserva: " + fecha.Dia + "/" + fecha.Mes + "\nPrecio: " + stand.Costo + " (Pronto pago: " + stand.Costo * Macros.DescuentoProntoPago + ")");
        }
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

        Integer Valor = Macros.EditorialesEspeciales.get(editorial.IdentificadorUnico);

        if(Valor != null){
            Stand stand = EncontrarStand(Valor);
            if(stand != null){
                DesbloquearStand(stand);
            }else{
                System.out.println("Stand no existente");
                return;
            }
        }
    }
}
