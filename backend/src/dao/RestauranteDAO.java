package dao;

import model.Restaurante;
import util.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase DAO para gestionar las operaciones CRUD de la tabla restaurantes.
 */
public class RestauranteDAO {

    /**
     * Inserta un nuevo restaurante en la base de datos.
     * @param restaurante Restaurante a insertar
     * @return true si se insertó correctamente
     */
    public boolean insertar(Restaurante restaurante) {
        String sql = "INSERT INTO restaurantes (nombre, direccion, ciudad, telefono, correo, categoria, calificacion) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, restaurante.getNombre());
            ps.setString(2, restaurante.getDireccion());
            ps.setString(3, restaurante.getCiudad());
            ps.setString(4, restaurante.getTelefono());
            ps.setString(5, restaurante.getCorreo());
            ps.setString(6, restaurante.getCategoria());
            ps.setDouble(7, restaurante.getCalificacion());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al insertar restaurante: " + e.getMessage());
            return false;
        }
    }

    /**
     * Retorna todos los restaurantes activos.
     * @return Lista de restaurantes
     */
    public List<Restaurante> listarTodos() {
        List<Restaurante> restaurantes = new ArrayList<>();
        String sql = "SELECT * FROM restaurantes WHERE estado = 1";

        try (Connection con = ConexionDB.obtenerConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Restaurante r = new Restaurante();
                r.setIdRestaurante(rs.getInt("id_restaurante"));
                r.setNombre(rs.getString("nombre"));
                r.setDireccion(rs.getString("direccion"));
                r.setCiudad(rs.getString("ciudad"));
                r.setTelefono(rs.getString("telefono"));
                r.setCorreo(rs.getString("correo"));
                r.setCategoria(rs.getString("categoria"));
                r.setCalificacion(rs.getDouble("calificacion"));
                r.setEstado(rs.getInt("estado"));
                restaurantes.add(r);
            }

        } catch (SQLException e) {
            System.err.println("Error al listar restaurantes: " + e.getMessage());
        }
        return restaurantes;
    }

    /**
     * Busca un restaurante por su ID.
     * @param idRestaurante ID del restaurante
     * @return Restaurante encontrado o null
     */
    public Restaurante buscarPorId(int idRestaurante) {
        String sql = "SELECT * FROM restaurantes WHERE id_restaurante = ?";
        Restaurante restaurante = null;

        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idRestaurante);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                restaurante = new Restaurante();
                restaurante.setIdRestaurante(rs.getInt("id_restaurante"));
                restaurante.setNombre(rs.getString("nombre"));
                restaurante.setDireccion(rs.getString("direccion"));
                restaurante.setCiudad(rs.getString("ciudad"));
                restaurante.setTelefono(rs.getString("telefono"));
                restaurante.setCorreo(rs.getString("correo"));
                restaurante.setCategoria(rs.getString("categoria"));
                restaurante.setCalificacion(rs.getDouble("calificacion"));
                restaurante.setEstado(rs.getInt("estado"));
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar restaurante: " + e.getMessage());
        }
        return restaurante;
    }

    /**
     * Actualiza los datos de un restaurante existente.
     * @param restaurante Restaurante con datos actualizados
     * @return true si se actualizó correctamente
     */
    public boolean actualizar(Restaurante restaurante) {
        String sql = "UPDATE restaurantes SET nombre=?, direccion=?, ciudad=?, " +
                     "telefono=?, correo=?, categoria=?, calificacion=? WHERE id_restaurante=?";

        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, restaurante.getNombre());
            ps.setString(2, restaurante.getDireccion());
            ps.setString(3, restaurante.getCiudad());
            ps.setString(4, restaurante.getTelefono());
            ps.setString(5, restaurante.getCorreo());
            ps.setString(6, restaurante.getCategoria());
            ps.setDouble(7, restaurante.getCalificacion());
            ps.setInt(8, restaurante.getIdRestaurante());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar restaurante: " + e.getMessage());
            return false;
        }
    }

    /**
     * Elimina lógicamente un restaurante (cambia estado a 0).
     * @param idRestaurante ID del restaurante a eliminar
     * @return true si se eliminó correctamente
     */
    public boolean eliminar(int idRestaurante) {
        String sql = "UPDATE restaurantes SET estado = 0 WHERE id_restaurante = ?";

        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idRestaurante);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar restaurante: " + e.getMessage());
            return false;
        }
    }
}