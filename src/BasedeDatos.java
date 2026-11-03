package src;
import java.sql.*;

public class BasedeDatos {

    public static Connection conectar(){
        String Url = "jdbc:h2:mem:fileydb";
        String Usuario = "sa";
        String Contrasena = "";
        try {
            Connection Puente = DriverManager.getConnection(Url, Usuario, Contrasena);
            System.out.println("¡Conexión establecida con éxito!");
            return Puente;
        } catch (SQLException e) {
            System.out.println("El puente falló: " + e.getMessage());
            return null;
        }
    }

    public static void CrearTablas(){
        Connection Puente = conectar();
        if(Puente != null){
            try{
                Statement Carrito = Puente.createStatement();
                
                String SqlEditorial = "CREATE TABLE IF NOT EXISTS Editoriales (identificador INT PRIMARY KEY, nombre VARCHAR(100))";
                String SqlStand = "CREATE TABLE IF NOT EXISTS Stands (Identificador INT PRIMARY KEY, Alto INT, Ancho INT, Costo DOUBLE)";
                String SqlReserva = "CREATE TABLE IF NOT EXISTS Reservas (Id_Reserva INT AUTO_INCREMENT PRIMARY KEY, Id_Editorial INT, Id_Stand INT, Dia INT, Mes INT)";
                
                Carrito.execute(SqlEditorial);
                Carrito.execute(SqlStand);
                Carrito.execute(SqlReserva);

                Carrito.close();
                Puente.close();

            }catch (SQLException e) {
                System.out.println("Fallo al crear los moldes de las tablas: " + e.getMessage());
            }
        }
    }

    public static void GuardarReserva(int IdEditorial, int IdStand, int Dia, int Mes){
        Connection Puente = conectar();
        if (Puente != null){
            try{
                String Sql = "INSERT INTO reservas (Id_Editorial, Id_Stand, Dia, Mes) VALUES (?, ?, ?, ?)";
                PreparedStatement Plantilla = Puente.prepareStatement(Sql);

                Plantilla.setInt(1, IdEditorial);
                Plantilla.setInt(2, IdStand);
                Plantilla.setInt(3, Dia);
                Plantilla.setInt(4, Mes);

                Plantilla.executeUpdate();
                System.out.println("Plantilla guardada en la base de datos");

                Plantilla.close();
                Puente.close();

            }catch(SQLException e){
                System.out.println("Error al guardar la reserva: " + e.getMessage());
            }
        }
    }
}
