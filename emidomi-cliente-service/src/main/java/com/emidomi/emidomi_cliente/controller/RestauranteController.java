package com.emidomi.emidomi_cliente.controller;

import com.emidomi.emidomi_cliente.model.Restaurante;
import com.emidomi.emidomi_cliente.service.RestauranteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/api/restaurantes")
public class RestauranteController {

    @Autowired
    private RestauranteService restauranteService;

    // GET /api/restaurantes - listar todos
    @GetMapping
    public List<Restaurante> listarTodos() {
        return restauranteService.listarTodos();
    }

    // GET /api/restaurantes/{id} - obtener uno por id
    @GetMapping("/{id}")
    public ResponseEntity<Restaurante> buscarPorId(@PathVariable Integer id) {
        Optional<Restaurante> restaurante = restauranteService.buscarPorId(id);
        return restaurante.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST /api/restaurantes - crear
    @PostMapping
    public Restaurante crear(@RequestBody Restaurante restaurante) {
        return restauranteService.guardar(restaurante);
    }

    // PUT /api/restaurantes/{id} - actualizar
    @PutMapping("/{id}")
    public ResponseEntity<Restaurante> actualizar(@PathVariable Integer id, @RequestBody Restaurante restaurante) {
        if (restauranteService.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Restaurante actualizado = restauranteService.actualizar(id, restaurante);
        return ResponseEntity.ok(actualizado);
    }

    // DELETE /api/restaurantes/{id} - eliminar
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (restauranteService.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        restauranteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}