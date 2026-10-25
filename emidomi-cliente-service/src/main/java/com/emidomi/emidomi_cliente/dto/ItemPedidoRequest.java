package com.emidomi.emidomi_cliente.dto;

/** Una línea del carrito: solo id y cantidad. El precio NO se acepta del cliente. */
public record ItemPedidoRequest(Integer idProducto, Integer cantidad) {
}
