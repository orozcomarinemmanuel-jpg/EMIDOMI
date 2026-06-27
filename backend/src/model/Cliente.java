package model;

/**
 * Clase que representa un Cliente en el sistema de domicilios.
 */
public class Cliente {

    // Atributos
    private int    idCliente;
    private String nombre;
    private String apellido;
    private String correo;
    private String telefono;
    private String direccion;
    private String ciudad;
    private int    estado;

    // Constructor vacío
    public Cliente() {}

    // Constructor completo
    public Cliente(int idCliente, String nombre, String apellido,
                   String correo, String telefono, String direccion,
                   String ciudad, int estado) {
        this.idCliente = idCliente;
        this.nombre    = nombre;
        this.apellido  = apellido;
        this.correo    = correo;
        this.telefono  = telefono;
        this.direccion = direccion;
        this.ciudad    = ciudad;
        this.estado    = estado;
    }

    // Getters y Setters
    public int getIdCliente()            { return idCliente; }
    public void setIdCliente(int id)     { this.idCliente = id; }

    public String getNombre()            { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido()              { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getCorreo()              { return correo; }
    public void setCorreo(String correo)   { this.correo = correo; }

    public String getTelefono()                { return telefono; }
    public void setTelefono(String telefono)   { this.telefono = telefono; }

    public String getDireccion()               { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getCiudad()              { return ciudad; }
    public void setCiudad(String ciudad)   { this.ciudad = ciudad; }

    public int getEstado()               { return estado; }
    public void setEstado(int estado)    { this.estado = estado; }

    @Override
    public String toString() {
        return "Cliente{" +
               "idCliente=" + idCliente +
               ", nombre='" + nombre + '\'' +
               ", apellido='" + apellido + '\'' +
               ", correo='" + correo + '\'' +
               ", telefono='" + telefono + '\'' +
               ", direccion='" + direccion + '\'' +
               ", ciudad='" + ciudad + '\'' +
               ", estado=" + estado +
               '}';
    }
}