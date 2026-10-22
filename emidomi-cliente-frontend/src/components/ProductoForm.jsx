// ProductoForm.jsx
// -----------------------------------------------------------------------
// Formulario reutilizable para CREAR un producto nuevo o EDITAR uno
// existente. El mismo componente sirve para ambos casos: si recibe un
// "productoEnEdicion" por props, precarga los campos; si no, inicia vacío.
// El restaurante al que pertenece el producto se elige desde un <select>
// poblado con los restaurantes existentes (prop "restaurantes").
// -----------------------------------------------------------------------

import { useEffect, useState } from "react";

// Estructura inicial de un producto vacío.
const PRODUCTO_VACIO = {
  idRestaurante: "",
  nombre: "",
  descripcion: "",
  precio: "",
  categoria: "",
  disponible: true,
  imagenUrl: "",
};

/**
 * @param {Object|null} productoEnEdicion - Producto a editar (null si se va a crear uno nuevo).
 * @param {Array} restaurantes - Lista de restaurantes para poblar el select de idRestaurante.
 * @param {Function} onGuardar - Función que se ejecuta al enviar el formulario.
 * @param {Function} onCancelar - Función para cancelar la edición en curso.
 * @param {boolean} cargando - Indica si hay una petición HTTP en curso (deshabilita los botones).
 */
function ProductoForm({
  productoEnEdicion,
  restaurantes = [],
  onGuardar,
  onCancelar,
  cargando = false,
}) {
  const [formulario, setFormulario] = useState(PRODUCTO_VACIO);

  // Cada vez que cambia el producto que se está editando, se actualizan
  // los valores del formulario con los datos de ese producto.
  useEffect(() => {
    if (productoEnEdicion) {
      setFormulario({
        ...productoEnEdicion,
        precio: productoEnEdicion.precio ?? "",
      });
    } else {
      // Al crear, se selecciona el primer restaurante por defecto (si existe).
      setFormulario({
        ...PRODUCTO_VACIO,
        idRestaurante: restaurantes.length > 0 ? restaurantes[0].idRestaurante : "",
      });
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [productoEnEdicion, restaurantes.length]);

  // Maneja el cambio de cualquier campo de forma genérica.
  function manejarCambio(evento) {
    const { name, value } = evento.target;
    setFormulario((valoresPrevios) => ({
      ...valoresPrevios,
      [name]: value,
    }));
  }

  // Envía el formulario: convierte "precio" a número e "idRestaurante" a entero.
  function manejarEnvio(evento) {
    evento.preventDefault();

    const datosAPagar = { ...formulario };
    datosAPagar.idRestaurante = Number(datosAPagar.idRestaurante);
    datosAPagar.precio = datosAPagar.precio === "" ? null : Number(datosAPagar.precio);

    onGuardar(datosAPagar);
  }

  return (
    <form className="producto-form" onSubmit={manejarEnvio}>
      <h2>{productoEnEdicion ? "Editar producto" : "Registrar nuevo producto"}</h2>

      <label htmlFor="idRestaurante">Restaurante</label>
      <select
        id="idRestaurante"
        name="idRestaurante"
        value={formulario.idRestaurante}
        onChange={manejarCambio}
        required
      >
        <option value="" disabled>
          Selecciona un restaurante
        </option>
        {restaurantes.map((restaurante) => (
          <option key={restaurante.idRestaurante} value={restaurante.idRestaurante}>
            {restaurante.nombre}
          </option>
        ))}
      </select>

      <label htmlFor="nombre">Nombre</label>
      <input
        id="nombre"
        name="nombre"
        type="text"
        value={formulario.nombre}
        onChange={manejarCambio}
        required
      />

      <label htmlFor="descripcion">Descripción</label>
      <input
        id="descripcion"
        name="descripcion"
        type="text"
        value={formulario.descripcion}
        onChange={manejarCambio}
      />

      <label htmlFor="precio">Precio</label>
      <input
        id="precio"
        name="precio"
        type="number"
        step="0.01"
        min="0"
        value={formulario.precio}
        onChange={manejarCambio}
        required
      />

      <label htmlFor="categoria">Categoría</label>
      <input
        id="categoria"
        name="categoria"
        type="text"
        value={formulario.categoria}
        onChange={manejarCambio}
        required
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

      <label htmlFor="imagenUrl">URL de imagen</label>
      <input
        id="imagenUrl"
        name="imagenUrl"
        type="url"
        value={formulario.imagenUrl}
        onChange={manejarCambio}
      />

      <div className="producto-form__acciones">
        <button type="submit" disabled={cargando}>
          {cargando ? "Guardando..." : productoEnEdicion ? "Guardar cambios" : "Registrar"}
        </button>

        {productoEnEdicion && (
          <button type="button" onClick={onCancelar} disabled={cargando}>
            Cancelar
          </button>
        )}
      </div>
    </form>
  );
}

export default ProductoForm;
