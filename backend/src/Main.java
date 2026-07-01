import dao.ClienteDAO;
import dao.RestauranteDAO;
import model.Cliente;
import model.Restaurante;

import java.util.List;
import dao.RestauranteDAO;
import model.Restaurante;

/**
 * Clase principal para probar el módulo de clientes.
 */
public class Main {

    public static void main(String[] args) {

        ClienteDAO clienteDAO = new ClienteDAO();

        // =====================
        // 1. INSERTAR un cliente
        // =====================
        System.out.println("=== INSERTAR CLIENTE ===");
        Cliente nuevoCliente = new Cliente();
        nuevoCliente.setNombre("Emmanuel");
        nuevoCliente.setApellido("Pérez");
        nuevoCliente.setCorreo("emmanuel.perez@email.com");
        nuevoCliente.setTelefono("3001234567");
        nuevoCliente.setDireccion("Calle 5 #10-20");
        nuevoCliente.setCiudad("Santa Rosa de Cabal");

        boolean insertado = clienteDAO.insertar(nuevoCliente);
        System.out.println(insertado ? "Cliente insertado correctamente ✅" : "Error al insertar ❌");

        // =====================
        // 2. LISTAR todos los clientes
        // =====================
        System.out.println("\n=== LISTAR CLIENTES ===");
        List<Cliente> clientes = clienteDAO.listarTodos();
        for (Cliente c : clientes) {
            System.out.println(c);
        }

        // =====================
        // 3. BUSCAR cliente por ID
        // =====================
        System.out.println("\n=== BUSCAR CLIENTE POR ID ===");
        Cliente encontrado = clienteDAO.buscarPorId(1);
        if (encontrado != null) {
            System.out.println("Cliente encontrado: " + encontrado);
        } else {
            System.out.println("Cliente no encontrado ❌");
        }

        // =====================
        // 4. ACTUALIZAR cliente
        // =====================
        System.out.println("\n=== ACTUALIZAR CLIENTE ===");
        Cliente clienteActualizar = new Cliente();
        clienteActualizar.setIdCliente(1);
        clienteActualizar.setNombre("Carlos");
        clienteActualizar.setApellido("Ramírez Actualizado");
        clienteActualizar.setCorreo("carlos.actualizado@email.com");
        clienteActualizar.setTelefono("3109999999");
        clienteActualizar.setDireccion("Carrera 10 #20-30");
        clienteActualizar.setCiudad("Pereira");

        boolean actualizado = clienteDAO.actualizar(clienteActualizar);
        System.out.println(actualizado ? "Cliente actualizado correctamente ✅" : "Error al actualizar ❌");

        // =====================
        // 5. ELIMINAR cliente
        // =====================
        System.out.println("\n=== ELIMINAR CLIENTE ===");
        boolean eliminado = clienteDAO.eliminar(1);
        System.out.println(eliminado ? "Cliente eliminado correctamente ✅" : "Error al eliminar ❌");
        // ============================
// PRUEBA CRUD - RESTAURANTE
// ============================
RestauranteDAO restauranteDAO = new RestauranteDAO();

// 1. INSERTAR un restaurante
System.out.println("\n=== INSERTAR RESTAURANTE ===");
Restaurante nuevoRestaurante = new Restaurante();
nuevoRestaurante.setNombre("Sabores de Casa");
nuevoRestaurante.setDireccion("Cra 10 #15-30");
nuevoRestaurante.setCiudad("Santa Rosa de Cabal");
nuevoRestaurante.setTelefono("3157894561");
nuevoRestaurante.setCorreo("saboresdecasa@email.com");
nuevoRestaurante.setCategoria("Comida Casera");
nuevoRestaurante.setCalificacion(4.5);

boolean restauranteInsertado = restauranteDAO.insertar(nuevoRestaurante);
System.out.println(restauranteInsertado ? "Restaurante insertado correctamente ✅" : "Error al insertar ❌");

// 2. LISTAR todos los restaurantes
System.out.println("\n=== LISTAR RESTAURANTES ===");
List<Restaurante> restaurantes = restauranteDAO.listarTodos();
for (Restaurante r : restaurantes) {
    System.out.println(r);
}

// 3. ACTUALIZAR el primer restaurante de la lista (si existe)
if (!restaurantes.isEmpty()) {
    System.out.println("\n=== ACTUALIZAR RESTAURANTE ===");
    Restaurante paraActualizar = restaurantes.get(0);
    paraActualizar.setCalificacion(5.0);
    boolean restauranteActualizado = restauranteDAO.actualizar(paraActualizar);
    System.out.println(restauranteActualizado ? "Restaurante actualizado correctamente ✅" : "Error al actualizar ❌");

    // 4. ELIMINAR ese mismo restaurante
    System.out.println("\n=== ELIMINAR RESTAURANTE ===");
    boolean restauranteEliminado = restauranteDAO.eliminar(paraActualizar.getIdRestaurante());
    System.out.println(restauranteEliminado ? "Restaurante eliminado correctamente ✅" : "Error al eliminar ❌");
}
    }
}