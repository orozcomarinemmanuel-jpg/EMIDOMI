// RestauranteForm.jsx
// -----------------------------------------------------------------------
// Formulario reutilizable para CREAR un restaurante nuevo o EDITAR uno
// existente. El mismo componente sirve para ambos casos: si recibe un
// "restauranteEnEdicion" por props, precarga los campos; si no, inicia vacío.
// -----------------------------------------------------------------------

import { useEffect, useState } from "react";

// Estructura inicial de un restaurante vacío. Se usa tanto al montar el
// formulario como al limpiar los campos después de guardar.
const RESTAURANTE_VACIO = {
  nombre: "",
  direccion: "",
  ciudad: "",
  telefono: "",
  correo: "",
  categoria: "",
  calificacion: "",
  estado: true,
};

/**
 * @param {Object|null} restauranteEnEdicion - Restaurante a editar (null si se va a crear uno nuevo).
 * @param {Function} onGuardar - Función que se ejecuta al enviar el formulario, recibe los datos del restaurante.
 * @param {Function} onCancelar - Función para cancelar la edición en curso.
 * @param {boolean} cargando - Indica si hay una petición HTTP en curso (deshabilita los botones).
 */
function RestauranteForm({ restauranteEnEdicion, onGuardar, onCancelar, cargando = false }) {
  // Estado local que guarda los valores actuales del formulario.
  const [formulario, setFormulario] = useState(RESTAURANTE_VACIO);

  // Cada vez que cambia el restaurante que se está editando, se actualizan
  // los valores del formulario con los datos de ese restaurante.
  useEffect(() => {
    if (restauranteEnEdicion) {
      // Edición: se precargan los campos; "calificacion" llega como número.
      setFormulario({
        ...restauranteEnEdicion,
        calificacion: restauranteEnEdicion.calificacion ?? "",
      });
    } else {
      setFormulario(RESTAURANTE_VACIO);
    }
  }, [restauranteEnEdicion]);

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
  // y delega el guardado al componente padre (RestaurantePage.jsx), que es
  // quien sabe si debe llamar a "crearRestaurante" o "actualizarRestaurante".
  function manejarEnvio(evento) {
    evento.preventDefault();

    const datosAPagar = { ...formulario };
    // "calificacion" es un número (BigDecimal) en el backend; se convierte
    // el texto del input a número. "estado" se mantiene como boolean.
    datosAPagar.calificacion =
      datosAPagar.calificacion === "" ? null : Number(datosAPagar.calificacion);

    onGuardar(datosAPagar);
  }

  return (
    <form className="restaurante-form" onSubmit={manejarEnvio}>
      <h2>
        {restauranteEnEdicion ? "Editar restaurante" : "Registrar nuevo restaurante"}
      </h2>

      <label htmlFor="nombre">Nombre</label>
      <input
        id="nombre"
        name="nombre"
        type="text"
        value={formulario.nombre}
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
      />

      <label htmlFor="ciudad">Ciudad</label>
      <input
        id="ciudad"
        name="ciudad"
        type="text"
        value={formulario.ciudad}
        onChange={manejarCambio}
      />

      <label htmlFor="telefono">Teléfono</label>
      <input
        id="telefono"
        name="telefono"
        type="tel"
        value={formulario.telefono}
        onChange={manejarCambio}
      />

      <label htmlFor="correo">Correo</label>
      <input
        id="correo"
        name="correo"
        type="email"
        value={formulario.correo}
        onChange={manejarCambio}
      />

      <label htmlFor="categoria">Categoría</label>
      <input
        id="categoria"
        name="categoria"
        type="text"
        value={formulario.categoria}
        onChange={manejarCambio}
      />

      <label htmlFor="calificacion">Calificación</label>
      <input
        id="calificacion"
        name="calificacion"
        type="number"
        step="0.1"
        min="0"
        max="5"
        value={formulario.calificacion}
        onChange={manejarCambio}
      />

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

      <div className="restaurante-form__acciones">
        <button type="submit" disabled={cargando}>
          {cargando
            ? "Guardando..."
            : restauranteEnEdicion
              ? "Guardar cambios"
              : "Registrar"}
        </button>

        {/* El botón de cancelar solo tiene sentido cuando se está editando */}
        {restauranteEnEdicion && (
          <button type="button" onClick={onCancelar} disabled={cargando}>
            Cancelar
          </button>
        )}
      </div>
    </form>
  );
}

export default RestauranteForm;
