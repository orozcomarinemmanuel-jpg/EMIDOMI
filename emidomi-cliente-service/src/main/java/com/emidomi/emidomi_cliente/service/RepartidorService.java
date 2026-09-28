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

    public Optional<Repartidor> actualizar(Integer id, Repartidor datos) {
        return repartidorRepository.findById(id).map(existente -> {
            if (datos.getNombre() != null) existente.setNombre(datos.getNombre());
            if (datos.getApellido() != null) existente.setApellido(datos.getApellido());
            if (datos.getCorreo() != null) existente.setCorreo(datos.getCorreo());
            if (datos.getTelefono() != null) existente.setTelefono(datos.getTelefono());
            if (datos.getNumeroLicencia() != null) existente.setNumeroLicencia(datos.getNumeroLicencia());
            if (datos.getVehiculo() != null) existente.setVehiculo(datos.getVehiculo());
            if (datos.getPlaca() != null) existente.setPlaca(datos.getPlaca());
            if (datos.getDisponible() != null) existente.setDisponible(datos.getDisponible());
            if (datos.getEstado() != null) existente.setEstado(datos.getEstado());
            return repartidorRepository.save(existente);
        });
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