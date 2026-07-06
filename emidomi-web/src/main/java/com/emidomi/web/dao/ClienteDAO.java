package com.emidomi.web.dao;

import com.emidomi.web.model.Cliente;
import com.emidomi.web.util.ConexionDB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {
    public boolean insertar(Cliente cliente) {
        String sql = "INSERT INTO clientes (nombre, apellido, correo, telefono, direccion, ciudad) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = ConexionDB.obtenerConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getApellido());
            ps.setString(3, cliente.getCorreo());
            ps.setString(4, cliente.getTelefono());
            ps.setString(5, cliente.getDireccion());
            ps.setString(6, cliente.getCiudad());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar cliente: " + e.getMessage());
            return false;
        }
    }
}