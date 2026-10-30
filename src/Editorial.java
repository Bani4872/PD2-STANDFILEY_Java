package src;
import java.util.Map;
import java.util.ArrayList;

public class Editorial {
    public String Nombre;
    public int IdentificadorUnico;
    public Map<Stand,Fecha> Reservas;
    public ArrayList<Stand> Comprados;

    public void ReservarStand(Stand stand, Fecha fecha){
        this.Reservas.put(stand, fecha);
        stand.Estado = 1;
    }

    public void ComprarStand(Stand stand){
        this.Comprados.add(stand);
        stand.Estado = 2;
    }

    public void CancelarReserva(Stand stand){
        this.Reservas.remove(stand);
        stand.Estado = 0;
    }

    public void AcompletarReserva(Stand stand, Fecha fecha){
        this.Comprados.add(stand);
    }

    public Editorial(String nombre){
        this.Nombre = nombre;
        this.IdentificadorUnico = nombre.hashCode();
    }
}
