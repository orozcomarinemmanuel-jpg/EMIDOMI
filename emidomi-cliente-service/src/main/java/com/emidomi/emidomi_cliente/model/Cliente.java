package com.emidomi.emidomi_cliente.model;

import jakarta.persistence.*;
import java.sql.Timestamp;

/**
 * Entidad JPA que representa la tabla 'clientes' en la base de datos db_domicilios.
 * Cada instancia de esta clase corresponde a una fila de la tabla.
 * Spring Data JPA usa estas anotaciones para generar el SQL automáticamente.
 */
@Entity                        // Marca la clase como una entidad JPA (se mapea a una tabla)
@Table(name = "clientes")      // Indica el nombre exacto de la tabla en MySQL
public class Cliente {

    @Id                                                  // Marca el campo como llave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Auto-incremento, igual que AUTO_INCREMENT en MySQL
    @Column(name = "id_cliente")
    private Integer idCliente;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "apellido", nullable = false, length = 100)
    private String apellido;

    @Column(name = "correo", nullable = false, unique = true, length = 150)
    private String correo;

    @Column(name = "telefono", nullable = false, length = 20)
    private String telefono;

    @Column(name = "direccion", nullable = false, length = 255)
    private String direccion;

    @Column(name = "ciudad", nullable = false, length = 100)
    private String ciudad;

    @Column(name = "fecha_registro", insertable = false, updatable = false)
    private Timestamp fechaRegistro;   // La genera MySQL automáticamente, por eso insertable/updatable = false

    @Column(name = "estado", nullable = false)
    private Boolean estado = true;     // true = activo (1), false = inactivo (0)

    // ----------------------------------------------------------
    // Constructores
    // ----------------------------------------------------------

    public Cliente() {
        // Constructor vacío requerido por JPA
    }

    public Cliente(String nombre, String apellido, String correo, String telefono,
                   String direccion, String ciudad) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.telefono = telefono;
        this.direccion = direccion;
        this.ciudad = ciudad;
    }

    // ----------------------------------------------------------
    // Getters y Setters
    // ----------------------------------------------------------

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public Timestamp getFechaRegistro() {
        return fechaRegistro;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }
}