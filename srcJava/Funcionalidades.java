package srcJava;

public class Funcionalidades {

    public static void Desbloquear_Stand(Stand stand){
        stand.Estado = 0;
    }

    public static Stand Encontrar_Stand(int identificador){
        for (Stand stand : Macros.Stands) {
            if (stand.Identificador == identificador) {
                return stand;
            }
        }
        return null;
    }

    public static void Comparar_Editorial_Especial(String editorial){
        String editorialUpper = editorial.toUpperCase();
        int identificadorEditorial = editorialUpper.hashCode();
        if(Macros.EditorialesEspeciales.containsKey(identificadorEditorial)){
            int identificadorStand = Macros.EditorialesEspeciales.get(identificadorEditorial);
            Stand stand = Encontrar_Stand(identificadorStand);
            if(stand != null){
                Desbloquear_Stand(stand);
            }
        }
    }

    public static void Liberar_Stand(Stand stand){
        stand.Estado = 0;
    }

    public static void Registrar_Editorial(String nombre){
        Editorial editorial = new Editorial(nombre);
        Macros.Editoriales.add(editorial);
    }

    public static boolean Encontrar_Editorial(String nombre){
        for (Editorial editorial : Macros.Editoriales) {
            if (editorial.Nombre.equals(nombre)) {
                return true;
            }
        }
        return false;
    }

    public static void Limpiar_Consola() {
    System.out.print("\033[H\033[2J");
    System.out.flush(); 
    }
}
