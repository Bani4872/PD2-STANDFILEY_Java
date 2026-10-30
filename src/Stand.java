package src;
public class Stand {
    public int Identificador;
    public int Estado;
    public int Alto;
    public int Ancho;
    public int Costo;

    public Stand(int Identificador, int Estado, int Alto, int Ancho){
        this.Identificador = Identificador;
        this.Estado = Estado;
        this.Alto = Alto;
        this.Ancho = Ancho;
        this.Costo = Alto*Ancho*(Macros.CostoMetroCuadrado);
    }
}