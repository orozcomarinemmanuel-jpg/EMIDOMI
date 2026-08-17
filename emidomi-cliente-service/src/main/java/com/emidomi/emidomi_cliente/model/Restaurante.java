package com.emidomi.emidomi_cliente.model;  // ajusta el paquete según tu proyecto

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "restaurantes")
public class Restaurante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_restaurante")
    private Integer idRestaurante;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(length = 255)
    private String direccion;

    @Column(length = 100)
    private String ciudad;

    @Column(length = 20)
    private String telefono;

    @Column(length = 150)
    private String correo;

    @Column(length = 100)
    private String categoria;

    @Column(precision = 3, scale = 2)
    private BigDecimal calificacion;

    private Boolean estado;

    @Column(name = "fecha_registro", insertable = false, updatable = false)
    private Timestamp fechaRegistro;

    // Getters y setters
    public Integer getIdRestaurante() { return idRestaurante; }
    public void setIdRestaurante(Integer idRestaurante) { this.idRestaurante = idRestaurante; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public BigDecimal getCalificacion() { return calificacion; }
    public void setCalificacion(BigDecimal calificacion) { this.calificacion = calificacion; }

    public Boolean getEstado() { return estado; }
    public void setEstado(Boolean estado) { this.estado = estado; }

    public Timestamp getFechaRegistro() { return fechaRegistro; }
}