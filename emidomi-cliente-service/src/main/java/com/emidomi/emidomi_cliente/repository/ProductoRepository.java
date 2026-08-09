package com.emidomi.emidomi_cliente.repository;

import com.emidomi.emidomi_cliente.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    List<Producto> findByIdRestaurante(Integer idRestaurante);
}