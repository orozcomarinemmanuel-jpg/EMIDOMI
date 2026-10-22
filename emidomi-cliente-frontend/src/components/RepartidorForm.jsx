// RepartidorForm.jsx
// -----------------------------------------------------------------------
// Formulario reutilizable para CREAR un repartidor nuevo o EDITAR uno
// existente. El mismo componente sirve para ambos casos: si recibe un
// "repartidorEnEdicion" por props, precarga los campos; si no, inicia vacío.
//
// Nota de edición: el endpoint PUT /api/repartidores/{id} (ya corregido en el
// backend) actualiza TODOS los campos del repartidor. Por eso en edición se
// envían los 9 campos completos, garantizando que ninguno se pierda.
// -----------------------------------------------------------------------

import { useEffect, useState } from "react";

// Estructura inicial de un repartidor vacío.
const REPARTIDOR_VACIO = {
  nombre: "",
  apellido: "",
  correo: "",
  telefono: "",
  numeroLicencia: "",
  vehiculo: "",
  placa: "",
  disponible: true,
  estado: true,
};

/**
 * @param {Object|null} repartidorEnEdicion - Repartidor a editar (null si se va a crear uno nuevo).
 * @param {Function} onGuardar - Función que se ejecuta al enviar el formulario.
 * @param {Function} onCancelar - Función para cancelar la edición en curso.
 * @param {boolean} cargando - Indica si hay una petición HTTP en curso (deshabilita los botones).
 */
function RepartidorForm({ repartidorEnEdicion, onGuardar, onCancelar, cargando = false }) {
  const [formulario, setFormulario] = useState(REPARTIDOR_VACIO);

  // Cada vez que cambia el repartidor que se está editando, se actualizan
  // los valores del formulario con los datos de ese repartidor. Si un campo
  // llega sin valor desde la lista, se conserva el valor existente con el
  // default del estado vacío (para no enviar null a un campo obligatorio).
  useEffect(() => {
    if (repartidorEnEdicion) {
      setFormulario({
        ...REPARTIDOR_VACIO,
        ...repartidorEnEdicion,
      });
    } else {
      setFormulario(REPARTIDOR_VACIO);
    }
  }, [repartidorEnEdicion]);

  // Maneja el cambio de cualquier campo de forma genérica.
  function manejarCambio(evento) {
    const { name, value } = evento.target;
    setFormulario((valoresPrevios) => ({
      ...valoresPrevios,
      [name]: value,
    }));
  }

  // Envía el formulario con los 9 campos del repartidor. Los campos booleanos
  // (disponible, estado) se mantienen tal cual vienen del select.
  function manejarEnvio(evento) {
    evento.preventDefault();
    onGuardar({ ...formulario });
  }

  return (
    <form className="repartidor-form" onSubmit={manejarEnvio}>
      <h2>{repartidorEnEdicion ? "Editar repartidor" : "Registrar nuevo repartidor"}</h2>

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
      />

      <label htmlFor="numeroLicencia">Número de licencia</label>
      <input
        id="numeroLicencia"
        name="numeroLicencia"
        type="text"
        value={formulario.numeroLicencia}
        onChange={manejarCambio}
      />

      <label htmlFor="vehiculo">Vehículo</label>
      <input
        id="vehiculo"
        name="vehiculo"
        type="text"
        value={formulario.vehiculo}
        onChange={manejarCambio}
      />

      <label htmlFor="placa">Placa</label>
      <input
        id="placa"
        name="placa"
        type="text"
        value={formulario.placa}
        onChange={manejarCambio}
      />

      <label htmlFor="disponible">Disponible</label>
      <select
        id="disponible"
        name="disponible"
        value={formulario.disponible}
        onChange={manejarCambio}
      >
        <option value={true}>Sí</option>
        <option value={false}>No</option>
      </select>

      <label htmlFor="estado">Estado</label>
      <select
        id="estado"
        name="estado"
        value={formulario.estado}
        onChange={manejarCambio}
      >
        <option value={true}>Activo</option>
        <option value={false}>Inactivo</option>
      </select>

      <div className="repartidor-form__acciones">
        <button type="submit" disabled={cargando}>
          {cargando ? "Guardando..." : repartidorEnEdicion ? "Guardar cambios" : "Registrar"}
        </button>

        {repartidorEnEdicion && (
          <button type="button" onClick={onCancelar} disabled={cargando}>
            Cancelar
          </button>
        )}
      </div>
    </form>
  );
}

export default RepartidorForm;
