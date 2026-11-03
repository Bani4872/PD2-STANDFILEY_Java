package srcJava;

public class Fecha {
    public int Dia;
    public int Mes;

    public Fecha(int Dia, int Mes){
        this.Dia = Dia;
        this.Mes = Mes;
    };

    public int Dias_Hasta(Fecha fechaFin) {
    return (fechaFin.Mes - this.Mes) * 30 + (fechaFin.Dia - this.Dia);
    }
}
