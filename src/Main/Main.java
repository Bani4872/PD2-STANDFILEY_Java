
import BD.BasedeDatos;
import Controlador.Web;
import Modelo.Stand;


public class Main{
    public static void main(String[] args) {
        BasedeDatos.CrearTablas();

        if (BasedeDatos.ObtenerStands().isEmpty()) {
            BasedeDatos.GuardarStand(new Stand(1, 0, 3, 3));
            BasedeDatos.GuardarStand(new Stand(2, 0, 3, 3));
            BasedeDatos.GuardarStand(new Stand(3, 3, 4, 4)); //planeta
            BasedeDatos.GuardarStand(new Stand(4, 3, 4, 4)); //diario
            BasedeDatos.GuardarStand(new Stand(5, 3, 5, 5)); //uady
            BasedeDatos.GuardarStand(new Stand(6, 0, 3, 3));
            BasedeDatos.GuardarStand(new Stand(7, 0, 3, 3));
            BasedeDatos.GuardarStand(new Stand(8, 0, 4, 4));
            BasedeDatos.GuardarStand(new Stand(9, 0, 3, 4));
            BasedeDatos.GuardarStand(new Stand(10, 0, 3, 4));
            System.out.println("Stands iniciales cargados en la nube.");
        }

        BasedeDatos.MostrarTablas();

        Web.Iniciar();
    }
}
