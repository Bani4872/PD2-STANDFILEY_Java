package Modelo;
import java.util.*;

public class Macros {
    static int CostoMetroCuadrado = 10;
    static int DiasParaProntoPago = 15;
    static int LimitePago = 30;
    static double DescuentoProntoPago = 0.15;
    public static Map<Integer, String> EstadoStand = Map.of(
        0, "Disponible",
        1, "Reservado",
        2, "Comprado",
        3, "Bloqueado"
    );

}
