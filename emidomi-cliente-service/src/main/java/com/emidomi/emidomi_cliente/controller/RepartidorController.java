package com.emidomi.emidomi_cliente.controller;

import com.emidomi.emidomi_cliente.model.Repartidor;
import com.emidomi.emidomi_cliente.service.RepartidorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/repartidores")
public class RepartidorController {

    @Autowired
    private RepartidorService repartidorService;

    @PostMapping
    public ResponseEntity<Repartidor> crear(@RequestBody Repartidor repartidor) {
        return ResponseEntity.ok(repartidorService.guardar(repartidor));
    }

    @GetMapping
    public ResponseEntity<List<Repartidor>> listar() {
        return ResponseEntity.ok(repartidorService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Repartidor> buscarPorId(@PathVariable Integer id) {
        return repartidorService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Repartidor> actualizar(@PathVariable Integer id, @RequestBody Repartidor repartidor) {
        return repartidorService.actualizar(id, repartidor)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        repartidorService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}