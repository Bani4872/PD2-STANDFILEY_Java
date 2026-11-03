package srcJava;
import java.util.*;

public class Macros {
    static int CostoMetroCuadrado = 10;
    static int DiasParaProntoPago = 15;
    static int LimitePago = 30;
    static double DescuentoProntoPago = 0.15;
    static Map<Integer, String> EstadoStand = Map.of(
        0, "Disponible",
        1, "Reservado",
        2, "Comprado",
        3, "Bloqueado"
    );

    static Fecha FechadeHoy = new Fecha(1, 1); 

    //Identificador de Editorial, identificador de Stand en el mapa
    static Map<Integer, Integer> EditorialesEspeciales = Map.of(
        "UADY".hashCode(), 4,
        "PLANETA".hashCode(), 5,
        "DIARIO DE YUCATÁN".hashCode(), 6
    );

    static Vector<Stand> Stands = new Vector<Stand>();
    static List<Editorial> Editoriales = new Vector<Editorial>();
    
}
