package com.emidomi.emidomi_cliente.service;

import com.emidomi.emidomi_cliente.model.DetallePedido;
import com.emidomi.emidomi_cliente.repository.DetallePedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DetallePedidoService {

    @Autowired
    private DetallePedidoRepository detallePedidoRepository;

    public DetallePedido guardar(DetallePedido detalle) {
        return detallePedidoRepository.save(detalle);
    }

    public List<DetallePedido> listar() {
        return detallePedidoRepository.findAll();
    }

    public List<DetallePedido> listarPorPedido(Integer idPedido) {
        return detallePedidoRepository.findByIdPedido(idPedido);
    }

    public Optional<DetallePedido> buscarPorId(Integer id) {
        return detallePedidoRepository.findById(id);
    }

    public void eliminar(Integer id) {
        detallePedidoRepository.deleteById(id);
    }
}