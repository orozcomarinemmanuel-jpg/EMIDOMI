package com.emidomi.emidomi_cliente.controller;

import com.emidomi.emidomi_cliente.model.Producto;
import com.emidomi.emidomi_cliente.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @PostMapping
    public ResponseEntity<Producto> crear(@RequestBody Producto producto) {
        return ResponseEntity.ok(productoService.guardar(producto));
    }

    @GetMapping
    public ResponseEntity<List<Producto>> listar() {
        return ResponseEntity.ok(productoService.listar());
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<Producto>> listarDisponibles() {
        return ResponseEntity.ok(productoService.listarDisponibles());
    }

    @GetMapping("/restaurante/{idRestaurante}")
    public ResponseEntity<List<Producto>> listarPorRestaurante(@PathVariable Integer idRestaurante) {
        return ResponseEntity.ok(productoService.listarPorRestaurante(idRestaurante));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> buscarPorId(@PathVariable Integer id) {
        return productoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizar(@PathVariable Integer id, @RequestBody Producto producto) {
        producto.setIdProducto(id);
        return ResponseEntity.ok(productoService.guardar(producto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}