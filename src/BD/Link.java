package BD;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Link {
    
    private static final String HOST = "bqkzjmjlajv6gm0z04nh-mysql.services.clever-cloud.com";
    private static final String PUERTO = "3306";
    private static final String NOMBRE_BD = "bqkzjmjlajv6gm0z04nh";
    private static final String USUARIO = "ukuk9dwgvknjhevr";
    private static final String CONTRASENA = "Y52fvAyk0TUoYaOKrUnG";

    private static final String URL = "jdbc:mysql://" + HOST + ":" + PUERTO + "/" + NOMBRE_BD + "?useSSL=true&serverTimezone=UTC";

    public static Connection conectar() {
        try {
            return DriverManager.getConnection(URL, USUARIO, CONTRASENA);
        } catch (SQLException e) {
            System.out.println("Error al conectar con Clever Cloud: " + e.getMessage());
            return null;
        }
    }

}
