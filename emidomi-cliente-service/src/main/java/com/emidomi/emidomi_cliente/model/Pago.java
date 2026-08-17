package com.emidomi.emidomi_cliente.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "pagos")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago")
    private Integer idPago;

    @Column(name = "id_pedido", nullable = false)
    private Integer idPedido;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago", nullable = false)
    private MetodoPago metodoPago;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_pago", nullable = false)
    private EstadoPago estadoPago;

    @Column(precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(length = 100)
    private String referencia;

    @Column(name = "fecha_pago", insertable = false, updatable = false)
    private Timestamp fechaPago;

    // Enums que reflejan los valores del ENUM en MySQL
    public enum MetodoPago {
        efectivo, tarjeta_credito, tarjeta_debito, transferencia, nequi, daviplata
    }

    public enum EstadoPago {
        pendiente, aprobado, rechazado, reembolsado
    }

    // Getters y setters
    public Integer getIdPago() { return idPago; }
    public void setIdPago(Integer idPago) { this.idPago = idPago; }

    public Integer getIdPedido() { return idPedido; }
    public void setIdPedido(Integer idPedido) { this.idPedido = idPedido; }

    public MetodoPago getMetodoPago() { return metodoPago; }
    public void setMetodoPago(MetodoPago metodoPago) { this.metodoPago = metodoPago; }

    public EstadoPago getEstadoPago() { return estadoPago; }
    public void setEstadoPago(EstadoPago estadoPago) { this.estadoPago = estadoPago; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }

    public String getReferencia() { return referencia; }
    public void setReferencia(String referencia) { this.referencia = referencia; }

    public Timestamp getFechaPago() { return fechaPago; }
}