package com.emidomi.emidomi_cliente.repository;

import com.emidomi.emidomi_cliente.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface PagoRepository extends JpaRepository<Pago, Integer> {
    List<Pago> findByIdPedido(Integer idPedido);

    /** Pagos de varios pedidos en una sola consulta. */
    List<Pago> findByIdPedidoIn(Collection<Integer> idsPedido);
}
