package com.emidomi.emidomi_cliente.service;

import com.emidomi.emidomi_cliente.model.Producto;
import com.emidomi.emidomi_cliente.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    public Producto guardar(Producto producto) {
        return productoRepository.save(producto);
    }

    public List<Producto> listar() {
        return productoRepository.findAll();
    }

    public List<Producto> listarDisponibles() {
        return productoRepository.findByEstado(true);
    }

    public List<Producto> listarPorRestaurante(Integer idRestaurante) {
        return productoRepository.findByIdRestaurante(idRestaurante);
    }

    public Optional<Producto> buscarPorId(Integer id) {
        return productoRepository.findById(id);
    }

    public void eliminar(Integer id) {
        productoRepository.deleteById(id);
    }
}