package src;

public class Main {
    public static void main(String[] args){
        BasedeDatos.CrearTablas();


        Stand stand = new Stand(1, 0, 5, 5);
        Stand stand1 = new Stand(2, 0, 5, 5);
        Stand stand2 = new Stand(3, 3, 5, 5); 
        Stand stand3 = new Stand(4, 3, 5, 5); 
        Stand stand4 = new Stand(5, 3, 5, 5); 
        Stand stand5 = new Stand(6, 0, 5, 5);
        Stand stand6 = new Stand(7, 0, 5, 5); 
        Stand stand7 = new Stand(8, 0, 5, 5);
        Stand stand8 = new Stand(9, 0, 5, 5);
        Stand stand9 = new Stand(10, 0, 5, 5);

        Editorial Editorial1 = new Editorial("Editorial1");
        Editorial Editorial2 = new Editorial("Editorial2");
        Editorial Editorial3 = new Editorial("Editorial3");
        Editorial Editorial4 = new Editorial("Editorial4");

        Fecha FechaReserva = new Fecha(12, 10);
        
        Editorial1.ReservarStand(stand, FechaReserva);
        Editorial2.ComprarStand(stand1);
        Editorial3.ReservarStand(stand8, FechaReserva);
        Editorial4.ComprarStand(stand6);

        Web.EditorialesRegistradas.put(Editorial1.Nombre, Editorial1);
        Web.EditorialesRegistradas.put(Editorial2.Nombre, Editorial2);
        Web.EditorialesRegistradas.put(Editorial3.Nombre, Editorial3);
        Web.EditorialesRegistradas.put(Editorial4.Nombre, Editorial4);
        
        Web.Iniciar();
        
    }
}
