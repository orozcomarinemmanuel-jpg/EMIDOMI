package dao;

import model.Cliente;
import util.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase DAO para gestionar las operaciones CRUD de la tabla clientes.
 */
public class ClienteDAO {

    /**
     * Inserta un nuevo cliente en la base de datos.
     * @param cliente Cliente a insertar
     * @return true si se insertó correctamente
     */
    public boolean insertar(Cliente cliente) {
        String sql = "INSERT INTO clientes (nombre, apellido, correo, telefono, direccion, ciudad) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

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

    /**
     * Retorna todos los clientes registrados.
     * @return Lista de clientes
     */
    public List<Cliente> listarTodos() {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM clientes WHERE estado = 1";

        try (Connection con = ConexionDB.obtenerConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Cliente c = new Cliente();
                c.setIdCliente(rs.getInt("id_cliente"));
                c.setNombre(rs.getString("nombre"));
                c.setApellido(rs.getString("apellido"));
                c.setCorreo(rs.getString("correo"));
                c.setTelefono(rs.getString("telefono"));
                c.setDireccion(rs.getString("direccion"));
                c.setCiudad(rs.getString("ciudad"));
                c.setEstado(rs.getInt("estado"));
                clientes.add(c);
            }

        } catch (SQLException e) {
            System.err.println("Error al listar clientes: " + e.getMessage());
        }
        return clientes;
    }

    /**
     * Busca un cliente por su ID.
     * @param idCliente ID del cliente
     * @return Cliente encontrado o null
     */
    public Cliente buscarPorId(int idCliente) {
        String sql = "SELECT * FROM clientes WHERE id_cliente = ?";
        Cliente cliente = null;

        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCliente);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                cliente = new Cliente();
                cliente.setIdCliente(rs.getInt("id_cliente"));
                cliente.setNombre(rs.getString("nombre"));
                cliente.setApellido(rs.getString("apellido"));
                cliente.setCorreo(rs.getString("correo"));
                cliente.setTelefono(rs.getString("telefono"));
                cliente.setDireccion(rs.getString("direccion"));
                cliente.setCiudad(rs.getString("ciudad"));
                cliente.setEstado(rs.getInt("estado"));
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar cliente: " + e.getMessage());
        }
        return cliente;
    }

    /**
     * Actualiza los datos de un cliente existente.
     * @param cliente Cliente con datos actualizados
     * @return true si se actualizó correctamente
     */
    public boolean actualizar(Cliente cliente) {
        String sql = "UPDATE clientes SET nombre=?, apellido=?, correo=?, " +
                     "telefono=?, direccion=?, ciudad=? WHERE id_cliente=?";

        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getApellido());
            ps.setString(3, cliente.getCorreo());
            ps.setString(4, cliente.getTelefono());
            ps.setString(5, cliente.getDireccion());
            ps.setString(6, cliente.getCiudad());
            ps.setInt(7, cliente.getIdCliente());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar cliente: " + e.getMessage());
            return false;
        }
    }

    /**
     * Elimina lógicamente un cliente (cambia estado a 0).
     * @param idCliente ID del cliente a eliminar
     * @return true si se eliminó correctamente
     */
    public boolean eliminar(int idCliente) {
        String sql = "UPDATE clientes SET estado = 0 WHERE id_cliente = ?";

        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCliente);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar cliente: " + e.getMessage());
            return false;
        }
    }
}