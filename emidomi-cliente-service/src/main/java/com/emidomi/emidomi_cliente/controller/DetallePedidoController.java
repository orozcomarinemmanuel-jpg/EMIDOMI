package com.emidomi.emidomi_cliente.controller;

import com.emidomi.emidomi_cliente.model.DetallePedido;
import com.emidomi.emidomi_cliente.service.DetallePedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/detalle-pedido")
public class DetallePedidoController {

    @Autowired
    private DetallePedidoService detallePedidoService;

    @PostMapping
    public ResponseEntity<DetallePedido> crear(@RequestBody DetallePedido detalle) {
        return ResponseEntity.ok(detallePedidoService.guardar(detalle));
    }

    @GetMapping
    public ResponseEntity<List<DetallePedido>> listar() {
        return ResponseEntity.ok(detallePedidoService.listar());
    }

    @GetMapping("/pedido/{idPedido}")
    public ResponseEntity<List<DetallePedido>> listarPorPedido(@PathVariable Integer idPedido) {
        return ResponseEntity.ok(detallePedidoService.listarPorPedido(idPedido));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetallePedido> buscarPorId(@PathVariable Integer id) {
        return detallePedidoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<DetallePedido> actualizar(@PathVariable Integer id, @RequestBody DetallePedido detalle) {
        detalle.setIdDetalle(id);
        return ResponseEntity.ok(detallePedidoService.guardar(detalle));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        detallePedidoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}