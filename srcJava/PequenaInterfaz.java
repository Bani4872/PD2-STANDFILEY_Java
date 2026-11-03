package srcJava;
import java.util.Scanner;
import java.io.IOException;

public class PequenaInterfaz {
    static Scanner scanner = new Scanner(System.in);
    
    public static Editorial InterfazInicioSesion(){
        System.out.print("Editorial: ");
        String Editorial = scanner.nextLine();

        if(!Funcionalidades.Encontrar_Editorial(Editorial)){
            Funcionalidades.Registrar_Editorial(Editorial);
            Funcionalidades.Comparar_Editorial_Especial(Editorial);
        }

        return Funcionalidades.Encontrar_Editorial(Editorial) ? Macros.Editoriales.stream().filter(e -> e.Nombre.equals(Editorial)).findFirst().orElse(null) : null;
    }

    public static void Interfaz_De_Usuario(){
        System.out.println("-----------------------");
        System.out.println("Seleccione una opción:");
        System.out.println("1. Comprar/Reservar stand");
        System.out.println("2. Acompletar/Cancelar reserva");
        System.out.println("3. Notificaciones");
        System.out.println("4. Salir");
        System.out.println("-----------------------");
    }

    public static void Imprimir_Stands(){
        System.out.println("Stands: ");
        System.out.println("-----------------------");
        for (Stand stand : Macros.Stands) {
            if(stand.Estado == 0){
                System.out.println("Stand: " + stand.Identificador + "\nDimensiones: " + stand.Alto + "x" + stand.Ancho + "\nPrecio: " + stand.Costo + "\n--------------------------");
            }
        }
    }

    public static void Interfaz_Comprar_Reservar_Stand(Editorial editorial){

        if(Macros.Stands.isEmpty()){
            System.out.println("No hay stands disponibles");
            return;
        }

        Imprimir_Stands();
        System.out.println("Ingrese el identificador del stand que desea comprar/reservar: ");
        int identificador = scanner.nextInt();
        Stand stand = Funcionalidades.Encontrar_Stand(identificador);
        if(stand == null){
            System.out.println("Stand no encontrado");
            Interfaz_Comprar_Reservar_Stand(editorial);
            return;
        }

        if(stand.Estado != 0){
            System.out.println("Stand no disponible");
            Interfaz_Comprar_Reservar_Stand(editorial);
            return;
        }

        System.out.println("Desea comprar o reservar el stand? (c/r)");
        String opcion = scanner.next();
        opcion = opcion.toLowerCase();
        if(opcion.equals("c")){
            editorial.Comprar_Stand(stand);
            System.out.println("Stand comprado");
        }else if(opcion.equals("r")){
            editorial.Reservar_Stand(stand, Macros.FechadeHoy);
        }
        return;
    }

    public static void Interfaz_Cancelar_Acompletar_Reserva(Editorial editorial){
        scanner.nextLine();

        if(editorial.Reservas.isEmpty()){
            System.out.println("No tiene reservas");
            return;
        }

        editorial.Imprimir_Lista_De_Reservas();
        System.out.println("Ingrese el identificador del stand que desea cancelar o acompletar la reserva: ");
        int identificador = scanner.nextInt();
        Stand stand = Funcionalidades.Encontrar_Stand(identificador);
        if(stand == null){
            System.out.println("Stand no encontrado");
            Interfaz_Cancelar_Acompletar_Reserva(editorial);
            return;
        }

        if(!editorial.Reservas.containsKey(stand)){
            System.out.println("No tiene una reserva para este stand");
            Interfaz_Cancelar_Acompletar_Reserva(editorial);
            return;
        }

        System.out.println("Desea cancelar o acompletar la reserva? (c/a)");
        String opcion = scanner.next();
        opcion = opcion.toLowerCase();
        if(opcion.equals("c")){
            editorial.Cancelar_Reserva(stand);
            System.out.println("Reserva cancelada");
        }else if(opcion.equals("a")){
            editorial.Acompletar_Reserva(stand, Macros.FechadeHoy);
            System.out.println("Reserva acompletada");
        }

        return;
    }

    public static void Notificaciones(Editorial editorial){
        editorial.Procesar_Notificaciones_Y_Vencimientos();

        System.out.println("Presione cualquier tecla para continuar");
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void Seleccionar_Opcion(Editorial editorial){
    boolean Salir = true;

    while(Salir){
        Interfaz_De_Usuario();

        System.out.print("Opcion:");
        switch(scanner.nextInt()){
            case 1:
                Interfaz_Comprar_Reservar_Stand(editorial);
                break;
            case 2:
                Interfaz_Cancelar_Acompletar_Reserva(editorial);
                break;
            case 3:
                Notificaciones(editorial);
                break;
            case 4:
                Salir = false;
                System.out.println("Saliendo...");
                scanner.nextLine();
                break;
            default:
                System.out.println("Opción inválida");
                break;
            }
        }
    }

}
