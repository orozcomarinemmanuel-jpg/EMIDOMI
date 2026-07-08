package com.emidomi.emidomi_cliente.repository;

import com.emidomi.emidomi_cliente.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la entidad Cliente.
 * Al extender JpaRepository, heredamos automáticamente métodos como:
 * save(), findById(), findAll(), deleteById(), etc.
 * No necesitamos escribir SQL ni implementar nada: Spring Data JPA
 * genera la implementación en tiempo de ejecución.
 *
 * JpaRepository<Cliente, Integer>:
 *   - Cliente: la entidad que maneja este repositorio
 *   - Integer: el tipo de dato de la llave primaria (id_cliente)
 */
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    // Aquí podemos declarar métodos personalizados más adelante,
    // por ejemplo: Cliente findByCorreo(String correo);
    // Spring Data JPA genera el SQL automáticamente a partir del nombre del método.

}