package com.emidomi.emidomi_cliente.dto;

import java.util.List;

/**
 * Cuerpo de POST /api/pedidos/confirmar.
 * El idCliente NO viaja aquí: sale del token JWT.
 * El navegador solo envía idProducto y cantidad; los precios los calcula el backend.
 */
public record ConfirmarPedidoRequest(
        String direccionEntrega,
        String ciudadEntrega,
        String notas,
        String metodoPago,
        List<GrupoPedidoRequest> grupos) {
}
