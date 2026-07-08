package com.emidomi.emidomi_cliente.controller;

import com.emidomi.emidomi_cliente.model.Cliente;
import com.emidomi.emidomi_cliente.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para el módulo Cliente.
 * Expone endpoints HTTP que reciben peticiones y devuelven respuestas en JSON.
 * Equivalente a RegistrarClienteServlet y ListarClientesServlet de AA2_EV02,
 * pero sin manejar manualmente HttpServletRequest/Response ni parsear formularios:
 * Spring hace esa conversión automáticamente.
 */
@RestController                      // Combina @Controller + @ResponseBody: cada método devuelve JSON directamente
@RequestMapping("/api/clientes")     // Prefijo base de todas las rutas de este controller
public class ClienteController {

    private final ClienteService clienteService;

    @Autowired
    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    /**
     * POST /api/clientes
     * Registra un nuevo cliente. Equivalente a RegistrarClienteServlet (POST).
     * El cuerpo de la petición (JSON) se convierte automáticamente en un objeto Cliente
     * gracias a @RequestBody.
     */
    @PostMapping
    public ResponseEntity<?> registrarCliente(@RequestBody Cliente cliente) {
        try {
            Cliente nuevoCliente = clienteService.registrarCliente(cliente);
            return new ResponseEntity<>(nuevoCliente, HttpStatus.CREATED); // 201
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST); // 400
        }
    }

    /**
     * GET /api/clientes
     * Lista todos los clientes registrados. Equivalente a ListarClientesServlet (GET).
     */
    @GetMapping
    public ResponseEntity<List<Cliente>> listarClientes() {
        List<Cliente> clientes = clienteService.listarClientes();
        return new ResponseEntity<>(clientes, HttpStatus.OK); // 200
    }

    /**
     * GET /api/clientes/{id}
     * Busca un cliente específico por su id.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Integer id) {
        return clienteService.buscarPorId(id)
                .map(cliente -> new ResponseEntity<Object>(cliente, HttpStatus.OK))
                .orElse(new ResponseEntity<Object>("Cliente no encontrado", HttpStatus.NOT_FOUND));
    }
}