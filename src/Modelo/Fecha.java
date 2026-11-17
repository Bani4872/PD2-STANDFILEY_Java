package Modelo;

import java.time.LocalDate;

public class Fecha {
    public int Dia;
    public int Mes;
    public static LocalDate hoy = LocalDate.now();
    public static Modelo.Fecha FechadeHoy = new Modelo.Fecha(hoy.getDayOfMonth(), hoy.getMonthValue());

    public Fecha(int Dia, int Mes){
        this.Dia = Dia;
        this.Mes = Mes;
    }

    
}
