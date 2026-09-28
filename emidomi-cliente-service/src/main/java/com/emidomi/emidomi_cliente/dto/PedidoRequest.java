package com.emidomi.emidomi_cliente.dto;

import java.math.BigDecimal;
import java.util.List;

public record PedidoRequest(
        Integer idCliente,
        String direccionEntrega,
        BigDecimal subtotal,
        BigDecimal total,
        List<DetallePedidoRequest> detallePedido) {
}