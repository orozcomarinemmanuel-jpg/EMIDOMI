package com.emidomi.emidomi_cliente.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "detalle_pedido")
public class DetallePedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")
    private Integer idDetalle;

    @Column(name = "id_pedido")
    private Integer idPedido;

    @Column(name = "id_producto")
    private Integer idProducto;

    private Integer cantidad;

    @Column(name = "precio_unitario")
    private BigDecimal precioUnitario;

    private BigDecimal subtotal;

    // Getters y setters
    public Integer getIdDetalle() { return idDetalle; }
    public void setIdDetalle(Integer idDetalle) { this.idDetalle = idDetalle; }

    public Integer getIdPedido() { return idPedido; }
    public void setIdPedido(Integer idPedido) { this.idPedido = idPedido; }

    public Integer getIdProducto() { return idProducto; }
    public void setIdProducto(Integer idProducto) { this.idProducto = idProducto; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
}