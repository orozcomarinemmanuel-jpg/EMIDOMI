package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase utilitaria para gestionar la conexión a la base de datos.
 * Patrón: Singleton
 */
public class ConexionDB {

    // Datos de conexión
    private static final String URL      = "jdbc:mysql://localhost:3306/db_domicilios";
    private static final String USUARIO  = "root";
    private static final String PASSWORD = "Claro2022*"; // Pon aquí tu contraseña de MySQL

    // Constructor privado para evitar instancias
    private ConexionDB() {}

    /**
     * Retorna una conexión activa a la base de datos.
     * @return Connection
     * @throws SQLException si no se puede conectar
     */
    public static Connection obtenerConexion() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, PASSWORD);
    }

    /**
     * Cierra la conexión si está activa.
     * @param conexion Connection a cerrar
     */
    public static void cerrarConexion(Connection conexion) {
        if (conexion != null) {
            try {
                conexion.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }
}