package com.emidomi.emidomi_cliente.repository;

import com.emidomi.emidomi_cliente.model.Restaurante;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestauranteRepository extends JpaRepository<Restaurante, Integer> {
    // JpaRepository ya nos da los métodos básicos: save, findAll, findById, deleteById, etc.
}