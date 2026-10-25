package com.emidomi.emidomi_cliente.dto;

import java.math.BigDecimal;

/** Una línea de un pedido ya guardada (precio congelado al momento de la compra). */
public record ProductoPedidoResponse(
        String nombre,
        Integer cantidad,
        BigDecimal precioUnitario,
        BigDecimal subtotal) {
}
