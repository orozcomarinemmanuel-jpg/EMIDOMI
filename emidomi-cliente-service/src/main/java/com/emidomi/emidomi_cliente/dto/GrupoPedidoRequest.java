package com.emidomi.emidomi_cliente.dto;

import java.util.List;

/** Productos de UN restaurante dentro de una confirmación de carrito. */
public record GrupoPedidoRequest(Integer idRestaurante, List<ItemPedidoRequest> items) {
}
