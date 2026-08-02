package com.emidomi.emidomi_cliente.repository;

import com.emidomi.emidomi_cliente.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, Integer> {
}