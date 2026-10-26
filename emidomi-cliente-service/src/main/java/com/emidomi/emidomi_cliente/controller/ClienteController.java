package com.emidomi.emidomi_cliente.controller;

import com.emidomi.emidomi_cliente.model.Cliente;
import com.emidomi.emidomi_cliente.service.ClienteService;
import com.emidomi.emidomi_cliente.service.FotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    private final FotoService fotoService;

    @Autowired
    public ClienteController(ClienteService clienteService, FotoService fotoService) {
        this.clienteService = clienteService;
        this.fotoService = fotoService;
    }

    /**
     * POST /api/clientes/{id}/foto
     * Sube la foto de perfil (multipart, campo "foto"). Solo el propio cliente puede subirla.
     * La validación real del archivo (tipo por contenido, tamaño) está en FotoService.
     */
    @PostMapping(value = "/{id}/foto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> subirFoto(@PathVariable Integer id,
                                       @RequestParam("foto") MultipartFile foto,
                                       Authentication auth) throws IOException {
        Integer idDelToken = obtenerIdClienteDelToken(auth);
        if (!Objects.equals(idDelToken, id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo puedes cambiar tu propia foto.");
        }
        String fotoUrl = fotoService.guardarFoto(id, foto);
        return new ResponseEntity<>(Map.of("fotoUrl", fotoUrl), HttpStatus.OK); // 200
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

    /**
     * PUT /api/clientes/{id}
     * Actualiza los datos de un cliente existente.
     * Solo puede editarse a sí mismo: el id de la ruta debe coincidir con el id del token.
     * La comprobación se hace ANTES de buscar el cliente, así un usuario no puede
     * descubrir qué ids existen probando.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarCliente(@PathVariable Integer id,
                                               @RequestBody Cliente cliente,
                                               Authentication auth) {
        Integer idDelToken = obtenerIdClienteDelToken(auth);
        if (!Objects.equals(idDelToken, id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo puedes editar tus propios datos.");
        }
        try {
            Cliente clienteActualizado = clienteService.actualizarCliente(id, cliente);
            return new ResponseEntity<>(clienteActualizado, HttpStatus.OK); // 200
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND); // 404
        }
    }

    /**
     * Lee el idCliente que JwtAuthFilter guardó en auth.getDetails() a partir del token.
     * Si no hay un id válido en el token, responde 401.
     */
    private Integer obtenerIdClienteDelToken(Authentication auth) {
        if (auth == null || !(auth.getDetails() instanceof Integer idCliente)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token sin idCliente válido.");
        }
        return idCliente;
    }

    /**
     * DELETE /api/clientes/{id}
     * Elimina un cliente existente.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarCliente(@PathVariable Integer id) {
        clienteService.eliminarCliente(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204
    }
}