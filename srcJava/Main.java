package srcJava;

public class Main {
    public static void main(String[] args) {
        Fecha Hoy = new Fecha(1, 1);
        Editorial EditorialPresente;
        Stand stand1 = new Stand(1, 0, 5, 5);
        Stand stand2 = new Stand(2, 0, 10, 10);
        Stand stand3 = new Stand(3, 0, 15, 15);
        Stand stand4 = new Stand(4, 3, 20, 20);
        Stand stand5 = new Stand(5, 3, 25, 25);
        Stand stand6 = new Stand(6, 3, 30, 30);
        Stand stand7 = new Stand(7, 0, 35, 35);
        Stand stand8 = new Stand(8, 0, 40, 40);
        Stand stand9 = new Stand(9, 0, 45, 45);
        Stand stand10 = new Stand(10, 0, 50, 50);

        Editorial editorial1 = new Editorial("Editorial1");
        Editorial editorial2 = new Editorial("Editorial2");
        Editorial editorial3 = new Editorial("Editorial3");

        editorial1.Reservar_Stand(stand1, Hoy);
        editorial2.Reservar_Stand(stand2, Hoy);
        editorial3.Reservar_Stand(stand3, Hoy);

        while(true){
            EditorialPresente = null;
            EditorialPresente = PequenaInterfaz.InterfazInicioSesion();
            PequenaInterfaz.Notificaciones(EditorialPresente);
            PequenaInterfaz.Seleccionar_Opcion(EditorialPresente);
        }

    }
}
