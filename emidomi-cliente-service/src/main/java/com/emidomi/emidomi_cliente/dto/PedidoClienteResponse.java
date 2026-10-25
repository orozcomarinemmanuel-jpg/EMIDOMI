package com.emidomi.emidomi_cliente.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

/** Un pedido del cliente autenticado, con sus productos y su pago. */
public record PedidoClienteResponse(
        Integer idPedido,
        String nombreRestaurante,
        String estadoPedido,
        BigDecimal subtotal,
        BigDecimal costoDomicilio,
        BigDecimal total,
        String direccionEntrega,
        String ciudadEntrega,
        String notas,
        Timestamp fechaPedido,
        List<ProductoPedidoResponse> productos,
        PagoResumenResponse pago) {
}
