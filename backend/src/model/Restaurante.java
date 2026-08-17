package model;

/**
 * Clase que representa un Restaurante en el sistema de domicilios.
 */
public class Restaurante {

    // Atributos
    private int    idRestaurante;
    private String nombre;
    private String direccion;
    private String ciudad;
    private String telefono;
    private String correo;
    private String categoria;
    private double calificacion;
    private int    estado;

    // Constructor vacío
    public Restaurante() {}

    // Constructor completo
    public Restaurante(int idRestaurante, String nombre, String direccion,
                        String ciudad, String telefono, String correo,
                        String categoria, double calificacion, int estado) {
        this.idRestaurante = idRestaurante;
        this.nombre        = nombre;
        this.direccion     = direccion;
        this.ciudad        = ciudad;
        this.telefono      = telefono;
        this.correo        = correo;
        this.categoria     = categoria;
        this.calificacion  = calificacion;
        this.estado        = estado;
    }

    // Getters y Setters
    public int getIdRestaurante()                  { return idRestaurante; }
    public void setIdRestaurante(int idRestaurante) { this.idRestaurante = idRestaurante; }

    public String getNombre()               { return nombre; }
    public void setNombre(String nombre)    { this.nombre = nombre; }

    public String getDireccion()                { return direccion; }
    public void setDireccion(String direccion)  { this.direccion = direccion; }

    public String getCiudad()             { return ciudad; }
    public void setCiudad(String ciudad)  { this.ciudad = ciudad; }

    public String getTelefono()               { return telefono; }
    public void setTelefono(String telefono)  { this.telefono = telefono; }

    public String getCorreo()             { return correo; }
    public void setCorreo(String correo)  { this.correo = correo; }

    public String getCategoria()                { return categoria; }
    public void setCategoria(String categoria)  { this.categoria = categoria; }

    public double getCalificacion()                   { return calificacion; }
    public void setCalificacion(double calificacion)  { this.calificacion = calificacion; }

    public int getEstado()            { return estado; }
    public void setEstado(int estado) { this.estado = estado; }

    @Override
    public String toString() {
        return "Restaurante{" +
               "idRestaurante=" + idRestaurante +
               ", nombre='" + nombre + '\'' +
               ", direccion='" + direccion + '\'' +
               ", ciudad='" + ciudad + '\'' +
               ", telefono='" + telefono + '\'' +
               ", correo='" + correo + '\'' +
               ", categoria='" + categoria + '\'' +
               ", calificacion=" + calificacion +
               ", estado=" + estado +
               '}';
    }
}
