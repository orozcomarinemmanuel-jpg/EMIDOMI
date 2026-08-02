package com.emidomi.emidomi_cliente.repository;

import com.emidomi.emidomi_cliente.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PagoRepository extends JpaRepository<Pago, Integer> {
}