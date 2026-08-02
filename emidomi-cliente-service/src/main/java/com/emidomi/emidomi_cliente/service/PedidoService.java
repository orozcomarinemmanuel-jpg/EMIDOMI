package com.emidomi.emidomi_cliente.service;

import com.emidomi.emidomi_cliente.model.Pedido;
import com.emidomi.emidomi_cliente.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    public List<Pedido> listarTodos() {
        return pedidoRepository.findAll();
    }

    public Optional<Pedido> buscarPorId(Integer id) {
        return pedidoRepository.findById(id);
    }

    public Pedido guardar(Pedido pedido) {
        return pedidoRepository.save(pedido);
    }

    public Pedido actualizar(Integer id, Pedido datosNuevos) {
        datosNuevos.setIdPedido(id);
        return pedidoRepository.save(datosNuevos);
    }

    public void eliminar(Integer id) {
        pedidoRepository.deleteById(id);
    }
}