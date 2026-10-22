// ClienteForm.jsx
// -----------------------------------------------------------------------
// Formulario reutilizable para CREAR un cliente nuevo o EDITAR uno
// existente. El mismo componente sirve para ambos casos: si recibe un
// "clienteEnEdicion" por props, precarga los campos; si no, inicia vacío.
// -----------------------------------------------------------------------

import { useEffect, useState } from "react";

// Estructura inicial de un cliente vacío. Se usa tanto al montar el
// formulario como al limpiar los campos después de guardar.
const CLIENTE_VACIO = {
  nombre: "",
  apellido: "",
  correo: "",
  telefono: "",
  direccion: "",
  ciudad: "",
  contrasena: "",
};

/**
 * @param {Object|null} clienteEnEdicion - Cliente a editar (null si se va a crear uno nuevo).
 * @param {Function} onGuardar - Función que se ejecuta al enviar el formulario, recibe los datos del cliente.
 * @param {Function} onCancelar - Función para cancelar la edición en curso.
 * @param {boolean} cargando - Indica si hay una petición HTTP en curso (deshabilita los botones).
 */
function ClienteForm({ clienteEnEdicion, onGuardar, onCancelar, cargando = false }) {
  // Estado local que guarda los valores actuales del formulario.
  const [formulario, setFormulario] = useState(CLIENTE_VACIO);

  // Cada vez que cambia el cliente que se está editando (por ejemplo,
  // el usuario da clic en "Editar" sobre otra fila), se actualizan
  // los valores del formulario con los datos de ese cliente.
  useEffect(() => {
    if (clienteEnEdicion) {
      setFormulario(clienteEnEdicion);
    } else {
      setFormulario(CLIENTE_VACIO);
    }
  }, [clienteEnEdicion]);

  // Maneja el cambio de cualquier campo del formulario de forma genérica,
  // usando el atributo "name" de cada input para saber qué propiedad actualizar.
  function manejarCambio(evento) {
    const { name, value } = evento.target;
    setFormulario((valoresPrevios) => ({
      ...valoresPrevios,
      [name]: value,
    }));
  }

  // Envía el formulario: evita el comportamiento por defecto del navegador
  // (recargar la página) y delega el guardado al componente padre (App.jsx),
  // que es quien sabe si debe llamar a "crearCliente" o "actualizarCliente".
  // Al editar un cliente existente no se envía el campo "contrasena" (el
  // backend lo marcó como WRITE_ONLY y solo debe recibirse al crear).
  function manejarEnvio(evento) {
    evento.preventDefault();

    const datosAPagar = { ...formulario };
    if (clienteEnEdicion) {
      delete datosAPagar.contrasena;
    }

    onGuardar(datosAPagar);
  }

  return (
    <form className="cliente-form" onSubmit={manejarEnvio}>
      <h2>{clienteEnEdicion ? "Editar cliente" : "Registrar nuevo cliente"}</h2>

      <label htmlFor="nombre">Nombre</label>
      <input
        id="nombre"
        name="nombre"
        type="text"
        value={formulario.nombre}
        onChange={manejarCambio}
        required
      />
      <label htmlFor="apellido">Apellido</label>
      <input
        id="apellido"
        name="apellido"
        type="text"
        value={formulario.apellido}
        onChange={manejarCambio}
        required
      />

      <label htmlFor="correo">Correo</label>
      <input
        id="correo"
        name="correo"
        type="email"
        value={formulario.correo}
        onChange={manejarCambio}
        required
      />

      <label htmlFor="telefono">Teléfono</label>
      <input
        id="telefono"
        name="telefono"
        type="tel"
        value={formulario.telefono}
        onChange={manejarCambio}
        required
      />

      <label htmlFor="direccion">Dirección</label>
      <input
        id="direccion"
        name="direccion"
        type="text"
        value={formulario.direccion}
        onChange={manejarCambio}
        required
      />
      <label htmlFor="ciudad">Ciudad</label>
      <input
        id="ciudad"
        name="ciudad"
        type="text"
        value={formulario.ciudad}
        onChange={manejarCambio}
        required
      />

      <label htmlFor="contrasena">Contraseña</label>
      <input
        id="contrasena"
        name="contrasena"
        type="password"
        value={formulario.contrasena}
        onChange={manejarCambio}
        required
      />

      <div className="cliente-form__acciones">
        <button type="submit" disabled={cargando}>
          {cargando ? "Guardando..." : clienteEnEdicion ? "Guardar cambios" : "Registrar"}
        </button>

        {/* El botón de cancelar solo tiene sentido cuando se está editando */}
        {clienteEnEdicion && (
          <button type="button" onClick={onCancelar} disabled={cargando}>
            Cancelar
          </button>
        )}
      </div>
    </form>
  );
}

export default ClienteForm;
