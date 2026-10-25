package com.emidomi.emidomi_cliente.dto;

import java.math.BigDecimal;
import java.util.List;

/** Respuesta 201 de POST /api/pedidos/confirmar: un pedido por restaurante. */
public record ConfirmarPedidoResponse(
        BigDecimal totalGeneral,
        List<PedidoConfirmadoResponse> pedidos) {
}
