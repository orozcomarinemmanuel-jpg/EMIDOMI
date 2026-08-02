package com.emidomi.emidomi_cliente.service;

import com.emidomi.emidomi_cliente.model.Restaurante;
import com.emidomi.emidomi_cliente.repository.RestauranteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RestauranteService {

    @Autowired
    private RestauranteRepository restauranteRepository;

    // Listar todos los restaurantes
    public List<Restaurante> listarTodos() {
        return restauranteRepository.findAll();
    }

    // Buscar un restaurante por id
    public Optional<Restaurante> buscarPorId(Integer id) {
        return restauranteRepository.findById(id);
    }

    // Crear un nuevo restaurante
    public Restaurante guardar(Restaurante restaurante) {
        return restauranteRepository.save(restaurante);
    }

    // Actualizar un restaurante existente
    public Restaurante actualizar(Integer id, Restaurante datosNuevos) {
        datosNuevos.setIdRestaurante(id);
        return restauranteRepository.save(datosNuevos);
    }

    // Eliminar un restaurante
    public void eliminar(Integer id) {
        restauranteRepository.deleteById(id);
    }
}