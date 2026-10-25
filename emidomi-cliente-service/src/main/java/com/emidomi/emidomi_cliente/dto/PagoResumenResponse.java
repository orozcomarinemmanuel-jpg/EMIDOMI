package com.emidomi.emidomi_cliente.dto;

import java.math.BigDecimal;

/** Pago simulado asociado a un pedido. */
public record PagoResumenResponse(
        Integer idPago,
        String metodoPago,
        String estadoPago,
        BigDecimal monto,
        String referencia) {
}
