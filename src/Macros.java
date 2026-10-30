package src;
import java.util.Map;
import java.util.Vector;

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
        123456, 1,
        789012, 2,
        345678, 3
    );

    static Vector<Stand> Stands = new Vector<Stand>();
}
