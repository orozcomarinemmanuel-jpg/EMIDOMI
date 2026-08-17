package com.emidomi.emidomi_cliente.model;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "repartidores")
public class Repartidor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_repartidor")
    private Integer idRepartidor;

    private String nombre;
    private String apellido;
    private String correo;
    private String telefono;

    @Column(name = "numero_licencia")
    private String numeroLicencia;

    private String vehiculo;
    private String placa;
    private Boolean disponible;
    private Boolean estado;

    @Column(name = "fecha_registro")
    private Timestamp fechaRegistro;

    // Getters y setters
    public Integer getIdRepartidor() { return idRepartidor; }
    public void setIdRepartidor(Integer idRepartidor) { this.idRepartidor = idRepartidor; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getNumeroLicencia() { return numeroLicencia; }
    public void setNumeroLicencia(String numeroLicencia) { this.numeroLicencia = numeroLicencia; }

    public String getVehiculo() { return vehiculo; }
    public void setVehiculo(String vehiculo) { this.vehiculo = vehiculo; }

    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }

    public Boolean getDisponible() { return disponible; }
    public void setDisponible(Boolean disponible) { this.disponible = disponible; }

    public Boolean getEstado() { return estado; }
    public void setEstado(Boolean estado) { this.estado = estado; }

    public Timestamp getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(Timestamp fechaRegistro) { this.fechaRegistro = fechaRegistro; }
}