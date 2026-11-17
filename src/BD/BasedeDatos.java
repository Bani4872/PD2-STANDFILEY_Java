package BD;

import java.sql.*;
import java.util.Vector;

import Modelo.Editorial;
import Modelo.Fecha;
import Modelo.Macros;
import Modelo.Stand;

public class BasedeDatos {

    // Crear tablas
    public static void CrearTablas() {
        try (Connection conn = Link.conectar();
             Statement stmt = conn.createStatement()) {

            String sqlEditoriales = "CREATE TABLE IF NOT EXISTS Editoriales (" +
                                    "identificador INT PRIMARY KEY, " +
                                    "nombre VARCHAR(100))" +
                                    "";

            String sqlStands = "CREATE TABLE IF NOT EXISTS Stands (" +
                               "Identificador INT PRIMARY KEY, " +
                               "Estado INT, " +
                               "Alto INT, " +
                               "Ancho INT, " +
                               "Costo DOUBLE)";

            String sqlReservas = "CREATE TABLE IF NOT EXISTS Reservas (" +
                                 "Id_Reserva INT AUTO_INCREMENT PRIMARY KEY, " +
                                 "Id_Editorial INT, " +
                                 "Id_Stand INT UNIQUE, " +
                                 "Dia INT, " +
                                 "Mes INT)";

            String sqlCompras = "CREATE TABLE IF NOT EXISTS Compras (" +
                                "Id_Compra INT AUTO_INCREMENT PRIMARY KEY, " +
                                "Id_Editorial INT, " +
                                "Id_Stand INT UNIQUE)";

            String sqlEditorialesEspeciales = "CREATE TABLE IF NOT EXISTS EditorialesEspeciales (" +
                                            "Id_Editorial INT PRIMARY KEY, " +
                                            "Id_Stand INT)";

            String sqlInsertarEditorialesEspeciales = "INSERT INTO EditorialesEspeciales (Id_Editorial, Id_Stand) VALUES " + //Editoriales especiales
                                            "(" + "UADY".hashCode() + ", 5), " +
                                            "(" + "Planeta".hashCode() + ", 3), " +
                                            "(" + "Diario de Yucatán".hashCode() + ", 4) " +
                                            "ON DUPLICATE KEY UPDATE Id_Stand = VALUES(Id_Stand)";

            stmt.execute(sqlEditoriales);
            stmt.execute(sqlStands);
            stmt.execute(sqlReservas);
            stmt.execute(sqlCompras);
            stmt.execute(sqlEditorialesEspeciales);
            stmt.executeUpdate(sqlInsertarEditorialesEspeciales);
            System.out.println("Tablas verificadas en Clever Cloud.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void MostrarTablas(){
        try (Connection conn = Link.conectar()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("Conectado");
            } else {
                System.out.println("No se pudo conectar");
                return;
            }

            // 1. LISTAR STANDS
            System.out.println("\n--- TABLA: STANDS ---");
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM Stands")) {
                while (rs.next()) {
                    int id = rs.getInt("Identificador");
                    int estado = rs.getInt("Estado");
                    String nombreEstado = Macros.EstadoStand.getOrDefault(estado, "Desconocido");
                    System.out.printf("Stand #%d | Estado: %s (%d) | Dim: %dx%d | Costo: $%.2f%n",
                            id, nombreEstado, estado, rs.getInt("Alto"), rs.getInt("Ancho"), rs.getDouble("Costo"));
                }
            }

            // 2. LISTAR EDITORIALES
            System.out.println("\n--- TABLA: EDITORIALES ---");
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM Editoriales")) {
                boolean hayEditoriales = false;
                while (rs.next()) {
                    hayEditoriales = true;
                    System.out.printf("ID: %d | Nombre: %s%n",
                            rs.getInt("identificador"), rs.getString("nombre"));
                }
                if (!hayEditoriales) System.out.println("(Sin editoriales registradas aún)");
            }

            // 3. LISTAR RESERVAS
            System.out.println("\n--- TABLA: RESERVAS ---");
            String sqlReservas = "SELECT r.Id_Reserva, e.nombre AS Editorial, r.Id_Stand, r.Dia, r.Mes " +
                                 "FROM Reservas r LEFT JOIN Editoriales e ON r.Id_Editorial = e.identificador";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sqlReservas)) {
                boolean hayReservas = false;
                while (rs.next()) {
                    hayReservas = true;
                    String editorial = rs.getString("Editorial") != null ? rs.getString("Editorial") : "Desconocida";
                    System.out.printf("Reserva #%d | Editorial: %s | Stand #%d | Fecha: %02d/%02d%n",
                            rs.getInt("Id_Reserva"), editorial, rs.getInt("Id_Stand"),
                            rs.getInt("Dia"), rs.getInt("Mes"));
                }
                if (!hayReservas) System.out.println("(Sin reservaciones activas)");
            }
            System.out.println("\n==========================================\n");

        } catch (SQLException e) {
            System.out.println("Error al consultar tablas en consola: " + e.getMessage());
        }
    }

    public static void GuardarEditorial(Editorial editorial) {
        String sql = "INSERT INTO Editoriales (identificador, nombre) VALUES (?, ?) ON DUPLICATE KEY UPDATE nombre = VALUES(nombre)";
        try (Connection conn = Link.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, editorial.IdentificadorUnico);
            pstmt.setString(2, editorial.Nombre);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al guardar editorial: " + e.getMessage());
        }
    }

    public static void GuardarReserva(int idEditorial, Stand stand, Fecha fecha) {
        String sql = "INSERT INTO Reservas (Id_Editorial, Id_Stand, Dia, Mes) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE Dia = VALUES(Dia), Mes = VALUES(Mes)";
        try (Connection conn = Link.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idEditorial);
            pstmt.setInt(2, stand.Identificador);
            pstmt.setInt(3, fecha.Dia);
            pstmt.setInt(4, fecha.Mes);
            pstmt.executeUpdate();
            ActualizarEstadoStand(stand);
        } catch (SQLException e) {
            System.out.println("Error al guardar reserva: " + e.getMessage());
        }
    }

    public static void GuardarCompra(int idEditorial, Stand stand) {
        String sql = "INSERT INTO Compras (Id_Editorial, Id_Stand) VALUES (?, ?) ON DUPLICATE KEY UPDATE Id_Stand = VALUES(Id_Stand)";
        try (Connection conn = Link.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idEditorial);
            pstmt.setInt(2, stand.Identificador);
            pstmt.executeUpdate();
            ActualizarEstadoStand(stand);
        } catch (SQLException e) {
            System.out.println("Error al guardar compra: " + e.getMessage());
        }
    }

    public static void GuardarStand(Stand stand) {
        String sql = "INSERT INTO Stands (Identificador, Estado, Alto, Ancho, Costo) VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE Estado = VALUES(Estado), Alto = VALUES(Alto), Ancho = VALUES(Ancho), Costo = VALUES(Costo)";
        try (Connection conn = Link.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, stand.Identificador);
            pstmt.setInt(2, stand.Estado);
            pstmt.setInt(3, stand.Alto);
            pstmt.setInt(4, stand.Ancho);
            pstmt.setDouble(5, stand.Costo);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al insertar stand: " + e.getMessage());
        }
    }

    public static Stand EncontrarStand(int identificador) {
        String sql = "SELECT * FROM Stands WHERE Identificador = ?";
        try (Connection conn = Link.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, identificador);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int estado = rs.getInt("Estado");
                int alto = rs.getInt("Alto");
                int ancho = rs.getInt("Ancho");
                return new Stand(identificador, estado, alto, ancho);
            } else {
                System.out.println("Stand con identificador " + identificador + " no encontrado.");
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar stand: " + e.getMessage());
        }
        return null;
    }

    public static void RastrearEditorialEspecial(Editorial editorial) {
        if (editorial == null) return;

        String sql = "SELECT Id_Stand FROM EditorialesEspeciales WHERE Id_Editorial = ?";
        try (Connection conn = Link.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, editorial.IdentificadorUnico);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int idStandEspecial = rs.getInt("Id_Stand");
                Stand stand = EncontrarStand(idStandEspecial);
                if (stand != null && stand.Estado == 3) {
                    editorial.DesbloquearStand(stand);
                    ActualizarEstadoStand(stand);
                    System.out.println("Stand " + idStandEspecial + " desbloqueado para " + editorial.Nombre);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al rastrear stand especial: " + e.getMessage());
        }
    }

    public static void ActualizarEstadoStand(Stand stand) {
        String sql = "UPDATE Stands SET Estado = ? WHERE Identificador = ?";
        try (Connection conn = Link.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, stand.Estado);
            pstmt.setInt(2, stand.Identificador);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al actualizar estado del stand: " + e.getMessage());
        }
    }

    // ESTE MÉTODO VINCULA LOS STANDS DE LA NUBE CON EL OBJETO EDITORIAL
    public static void CargarStandsDeEditorial(Editorial editorial) {
        if (editorial == null) return;
        editorial.Reservas.clear();
        editorial.Comprados.clear();

        String sqlReservas = "SELECT s.Identificador, s.Estado, s.Alto, s.Ancho, r.Dia, r.Mes " +
                             "FROM Reservas r INNER JOIN Stands s ON r.Id_Stand = s.Identificador " +
                             "WHERE r.Id_Editorial = ?";

        String sqlCompras = "SELECT s.Identificador, s.Estado, s.Alto, s.Ancho " +
                            "FROM Compras c INNER JOIN Stands s ON c.Id_Stand = s.Identificador " +
                            "WHERE c.Id_Editorial = ?";

        try (Connection conn = Link.conectar()) {
            //Llenar el HashMap de Reservas de la editorial
            try (PreparedStatement psRes = conn.prepareStatement(sqlReservas)) {
                psRes.setInt(1, editorial.IdentificadorUnico);
                ResultSet rs = psRes.executeQuery();
                while (rs.next()) {
                    Stand s = new Stand(rs.getInt("Identificador"), rs.getInt("Estado"), rs.getInt("Alto"), rs.getInt("Ancho"));
                    Fecha f = new Fecha(rs.getInt("Dia"), rs.getInt("Mes"));
                    editorial.Reservas.put(s, f);
                }
            }

            //Llenar el ArrayList de Comprados de la editorial
            try (PreparedStatement psComp = conn.prepareStatement(sqlCompras)) {
                psComp.setInt(1, editorial.IdentificadorUnico);
                ResultSet rs = psComp.executeQuery();
                while (rs.next()) {
                    Stand s = new Stand(rs.getInt("Identificador"), rs.getInt("Estado"), rs.getInt("Alto"), rs.getInt("Ancho"));
                    editorial.Comprados.add(s);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al cargar stands de la editorial: " + e.getMessage());
        }
    }

    public static Vector<Stand> ObtenerStands() {
        String sql = "SELECT * FROM Stands";
        Vector<Stand> stands = new Vector<Stand>();
        try (Connection conn = Link.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int identificador = rs.getInt("Identificador");
                int estado = rs.getInt("Estado");
                int alto = rs.getInt("Alto");
                int ancho = rs.getInt("Ancho");
                double costo = rs.getDouble("Costo");
                Stand stand = new Stand(identificador, estado, alto, ancho);

                stands.add(stand);
                stand.Costo = costo;
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener stands: " + e.getMessage());
        }
        return stands;
    }
}
