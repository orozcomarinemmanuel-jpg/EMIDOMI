package com.emidomi.emidomi_cliente.service;

import com.emidomi.emidomi_cliente.model.Repartidor;
import com.emidomi.emidomi_cliente.repository.RepartidorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RepartidorService {

    @Autowired
    private RepartidorRepository repartidorRepository;

    public Repartidor guardar(Repartidor repartidor) {
        return repartidorRepository.save(repartidor);
    }

    public List<Repartidor> listar() {
        return repartidorRepository.findAll();
    }

    public Optional<Repartidor> buscarPorId(Integer id) {
        return repartidorRepository.findById(id);
    }

    public void eliminar(Integer id) {
        repartidorRepository.deleteById(id);
    }
}