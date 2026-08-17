package com.emidomi.web.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase utilitaria para gestionar la conexion a la base de datos.
 * Patron: Singleton
 */
public class ConexionDB {

    private static final String URL      = "jdbc:mysql://localhost:3306/db_domicilios";
    private static final String USUARIO  = "root";
    private static final String PASSWORD = "Claro2022*";

    private ConexionDB() {}

    public static Connection obtenerConexion() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, PASSWORD);
    }

    public static void cerrarConexion(Connection conexion) {
        if (conexion != null) {
            try {
                conexion.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexion: " + e.getMessage());
            }
        }
    }
}