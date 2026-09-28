package com.emidomi.emidomi_cliente.service;

import com.emidomi.emidomi_cliente.dto.DetallePedidoRequest;
import com.emidomi.emidomi_cliente.dto.PedidoRequest;
import com.emidomi.emidomi_cliente.model.DetallePedido;
import com.emidomi.emidomi_cliente.model.Pedido;
import com.emidomi.emidomi_cliente.model.Producto;
import com.emidomi.emidomi_cliente.repository.DetallePedidoRepository;
import com.emidomi.emidomi_cliente.repository.PedidoRepository;
import com.emidomi.emidomi_cliente.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private DetallePedidoRepository detallePedidoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    public List<Pedido> listarTodos() {
        return pedidoRepository.findAll();
    }

    public Optional<Pedido> buscarPorId(Integer id) {
        return pedidoRepository.findById(id);
    }

    public List<Pedido> buscarPorCliente(Integer idCliente) {
        return pedidoRepository.findByIdCliente(idCliente);
    }

    public Pedido guardar(Pedido pedido) {
        return pedidoRepository.save(pedido);
    }

    /**
     * Crea el pedido (cabecera) y todos sus detalles en una única transacción.
     *
     * Snapshot del precio: el precio unitario de cada línea se copia de
     * productos.precio en el momento de la creación; si el precio cambia más
     * tarde, las líneas conservan el valor de la compra (FR-001/edge case).
     *
     * Transaccional/rollback: @Transactional agrupa cabecera + líneas; si
     * cualquier producto no existe (orElseThrow) o una línea es inválida se
     * lanza una RuntimeException que revierte la cabecera ya guardada, sin
     * dejar pedidos ni líneas a medias (FR-004/FR-005).
     */
    @Transactional
    public Pedido crearConDetalle(PedidoRequest peticion) {
        if (peticion.detallePedido() == null || peticion.detallePedido().isEmpty()) {
            throw new IllegalArgumentException("El pedido debe incluir al menos una línea de productos.");
        }

        Pedido pedido = new Pedido();
        pedido.setIdCliente(peticion.idCliente());
        pedido.setDireccionEntrega(peticion.direccionEntrega());
        pedido.setEstadoPedido(Pedido.EstadoPedido.PENDIENTE);
        pedido.setSubtotal(peticion.subtotal() != null ? peticion.subtotal() : new BigDecimal("0.00"));
        pedido.setCostoDomicilio(new BigDecimal("0.00"));
        pedido.setTotal(peticion.total() != null ? peticion.total() : new BigDecimal("0.00"));

        Pedido cabeceraGuardada = pedidoRepository.save(pedido);

        List<DetallePedido> lineas = new ArrayList<>();
        for (DetallePedidoRequest item : peticion.detallePedido()) {
            if (item.idProducto() == null) {
                throw new IllegalArgumentException("El identificador del producto no puede ser nulo.");
            }
            if (item.cantidad() == null || item.cantidad() <= 0) {
                throw new IllegalArgumentException("La cantidad de cada producto debe ser mayor que 0.");
            }
            Producto producto = productoRepository.findById(item.idProducto())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Producto con id " + item.idProducto() + " no encontrado."));

            DetallePedido detalle = new DetallePedido();
            detalle.setIdPedido(cabeceraGuardada.getIdPedido());
            detalle.setIdProducto(producto.getIdProducto());
            detalle.setCantidad(item.cantidad());
            detalle.setPrecioUnitario(producto.getPrecio());
            detalle.setSubtotal(producto.getPrecio().multiply(BigDecimal.valueOf(item.cantidad())));
            lineas.add(detalle);
        }

        detallePedidoRepository.saveAll(lineas);
        return cabeceraGuardada;
    }

    public Pedido actualizar(Integer id, Pedido datosNuevos) {
        datosNuevos.setIdPedido(id);
        return pedidoRepository.save(datosNuevos);
    }

    public void eliminar(Integer id) {
        pedidoRepository.deleteById(id);
    }
}