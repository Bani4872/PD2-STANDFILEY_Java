package src;
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

    
    static Map<Integer, Integer> EditorialesEspeciales = Map.of(
        "UADY".hashCode(), 5,
        "Planeta".hashCode(), 3,
        "Diario de Yucatán".hashCode(), 4
    );

    public static Vector<Stand> Stands = new Vector<>();

}
