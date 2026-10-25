package com.emidomi.emidomi_cliente.controller;

import com.emidomi.emidomi_cliente.dto.ConfirmarPedidoRequest;
import com.emidomi.emidomi_cliente.dto.ConfirmarPedidoResponse;
import com.emidomi.emidomi_cliente.dto.PedidoRequest;
import com.emidomi.emidomi_cliente.model.Pedido;
import com.emidomi.emidomi_cliente.service.CheckoutService;
import com.emidomi.emidomi_cliente.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private CheckoutService checkoutService;

    /**
     * Confirma el carrito: un pedido por restaurante, todo o nada.
     * El idCliente sale del token JWT (JwtAuthFilter lo guarda en auth.getDetails()).
     */
    @PostMapping("/confirmar")
    public ResponseEntity<ConfirmarPedidoResponse> confirmar(@RequestBody ConfirmarPedidoRequest peticion,
                                                             Authentication auth) {
        Integer idCliente = obtenerIdClienteDelToken(auth);
        ConfirmarPedidoResponse respuesta = checkoutService.confirmar(idCliente, peticion);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    private Integer obtenerIdClienteDelToken(Authentication auth) {
        if (auth == null || !(auth.getDetails() instanceof Integer idCliente)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token sin idCliente válido.");
        }
        return idCliente;
    }

    @GetMapping
    public List<Pedido> listarTodos() {
        return pedidoService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> buscarPorId(@PathVariable Integer id) {
        Optional<Pedido> pedido = pedidoService.buscarPorId(id);
        return pedido.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/cliente/{idCliente}")
    public List<Pedido> buscarPorCliente(@PathVariable Integer idCliente) {
        return pedidoService.buscarPorCliente(idCliente);
    }

    @PostMapping
    public Pedido crear(@RequestBody PedidoRequest peticion) {
        return pedidoService.crearConDetalle(peticion);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pedido> actualizar(@PathVariable Integer id, @RequestBody Pedido pedido) {
        if (pedidoService.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Pedido actualizado = pedidoService.actualizar(id, pedido);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (pedidoService.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        pedidoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}