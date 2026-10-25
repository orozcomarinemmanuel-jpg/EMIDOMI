package com.emidomi.emidomi_cliente.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Flujo de confirmación de carrito (B2) y listado de pedidos del cliente (B3).
 * B1: solo define la constante de costo de domicilio.
 */
@Service
public class CheckoutService {

    /**
     * Costo de domicilio fijo POR RESTAURANTE. Cambiar aquí es el único lugar a tocar.
     * total = subtotal + COSTO_DOMICILIO
     */
    public static final BigDecimal COSTO_DOMICILIO = new BigDecimal("5000.00");
}
