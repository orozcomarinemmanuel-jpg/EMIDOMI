// RestauranteList.jsx
// -----------------------------------------------------------------------
// Muestra en una tabla los restaurantes registrados y ofrece, por cada fila,
// las acciones de editar y eliminar. No conoce Axios ni el backend:
// solo recibe datos y funciones por props (principio de responsabilidad única).
// -----------------------------------------------------------------------

/**
 * @param {Array} restaurantes - Lista de restaurantes a mostrar.
 * @param {Function} onEditar - Se ejecuta al dar clic en "Editar", recibe el restaurante seleccionado.
 * @param {Function} onEliminar - Se ejecuta al dar clic en "Eliminar", recibe el id del restaurante.
 * @param {boolean} cargando - Indica si hay una petición HTTP en curso (deshabilita las acciones).
 */
function RestauranteList({ restaurantes, onEditar, onEliminar, cargando = false }) {
  // Caso borde: si todavía no hay restaurantes cargados o la lista está vacía,
  // se muestra un mensaje en lugar de una tabla vacía.
  if (restaurantes.length === 0) {
    return <p className="restaurante-list__vacio">No hay restaurantes registrados todavía.</p>;
  }

  return (
    <table className="restaurante-list">
      <thead>
        <tr>
          <th>Nombre</th>
          <th>Dirección</th>
          <th>Ciudad</th>
          <th>Teléfono</th>
          <th>Correo</th>
          <th>Categoría</th>
          <th>Calificación</th>
          <th>Estado</th>
          <th>Acciones</th>
        </tr>
      </thead>
      <tbody>
        {restaurantes.map((restaurante) => (
          // Se usa el id del restaurante como "key" porque es único y estable.
          <tr key={restaurante.idRestaurante}>
            <td>{restaurante.nombre}</td>
            <td>{restaurante.direccion}</td>
            <td>{restaurante.ciudad}</td>
            <td>{restaurante.telefono}</td>
            <td>{restaurante.correo}</td>
            <td>{restaurante.categoria}</td>
            <td>{restaurante.calificacion}</td>
            <td>
              {restaurante.estado ? "Activo" : "Inactivo"}
            </td>
            <td>
              <button onClick={() => onEditar(restaurante)} disabled={cargando}>
                Editar
              </button>
              <button
                onClick={() => onEliminar(restaurante.idRestaurante)}
                disabled={cargando}
              >
                Eliminar
              </button>
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}

export default RestauranteList;
