package com.emidomi.emidomi_cliente.service;

import com.emidomi.emidomi_cliente.model.Cliente;
import com.emidomi.emidomi_cliente.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Capa de servicio para Cliente.
 * Aquí va la lógica de negocio: validaciones, reglas propias de EMIDOMI.
 * El Controller nunca habla directamente con el Repository; siempre pasa por aquí.
 */
@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    // Inyección de dependencias por constructor: Spring crea automáticamente
    // una instancia de ClienteRepository y la "inyecta" aquí. Es el equivalente
    // moderno a lo que antes hacías manualmente al abrir la Connection en el DAO.
    @Autowired
    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    /**
     * Registra un nuevo cliente, validando que el correo no esté repetido.
     */
    public Cliente registrarCliente(Cliente cliente) {
        // Regla de negocio: no permitir correos duplicados
        boolean correoExiste = clienteRepository.findAll().stream()
                .anyMatch(c -> c.getCorreo().equalsIgnoreCase(cliente.getCorreo()));

        if (correoExiste) {
            throw new IllegalArgumentException("Ya existe un cliente registrado con ese correo.");
        }

        return clienteRepository.save(cliente);
    }

    /**
     * Devuelve la lista completa de clientes registrados.
     */
    public List<Cliente> listarClientes() {
        return clienteRepository.findAll();
    }

    /**
     * Busca un cliente por su id. Devuelve Optional para manejar
     * de forma segura el caso en que no exista.
     */
    public Optional<Cliente> buscarPorId(Integer id) {
        return clienteRepository.findById(id);
    }
}