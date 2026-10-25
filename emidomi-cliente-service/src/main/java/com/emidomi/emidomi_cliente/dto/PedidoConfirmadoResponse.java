package com.emidomi.emidomi_cliente.dto;

import java.math.BigDecimal;

/** Resumen de un pedido creado dentro de una confirmación. */
public record PedidoConfirmadoResponse(
        Integer idPedido,
        Integer idRestaurante,
        String nombreRestaurante,
        BigDecimal subtotal,
        BigDecimal costoDomicilio,
        BigDecimal total,
        String estadoPedido,
        Integer idRepartidor,
        PagoResumenResponse pago) {
}
