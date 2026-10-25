package com.emidomi.emidomi_cliente.repository;

import com.emidomi.emidomi_cliente.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Integer> {
    List<Pedido> findByIdCliente(Integer idCliente);

    /** Pedidos de un cliente, más recientes primero (idPedido como desempate). */
    List<Pedido> findByIdClienteOrderByFechaPedidoDescIdPedidoDesc(Integer idCliente);
}
