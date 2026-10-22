// ProductoList.jsx
// -----------------------------------------------------------------------
// Muestra en una tabla los productos registrados y ofrece, por cada fila,
// las acciones de editar y eliminar. No conoce Axios ni el backend:
// solo recibe datos y funciones por props. Resuelve el nombre del
// restaurante a partir de su id usando la lista de restaurantes.
// -----------------------------------------------------------------------

/**
 * @param {Array} productos - Lista de productos a mostrar.
 * @param {Array} restaurantes - Lista de restaurantes para mostrar el nombre en la tabla.
 * @param {Function} onEditar - Se ejecuta al dar clic en "Editar", recibe el producto seleccionado.
 * @param {Function} onEliminar - Se ejecuta al dar clic en "Eliminar", recibe el id del producto.
 * @param {boolean} cargando - Indica si hay una petición HTTP en curso.
 */
function ProductoList({
  productos,
  restaurantes = [],
  onEditar,
  onEliminar,
  cargando = false,
}) {
  // Devuelve el nombre del restaurante dado su id, o el propio id si no se encuentra.
  function nombreRestaurante(idRestaurante) {
    const encontrado = restaurantes.find(
      (r) => r.idRestaurante === idRestaurante
    );
    return encontrado ? encontrado.nombre : idRestaurante;
  }

  if (productos.length === 0) {
    return <p className="producto-list__vacio">No hay productos registrados todavía.</p>;
  }

  return (
    <table className="producto-list">
      <thead>
        <tr>
          <th>Restaurante</th>
          <th>Nombre</th>
          <th>Descripción</th>
          <th>Precio</th>
          <th>Categoría</th>
          <th>Disponible</th>
          <th>Acciones</th>
        </tr>
      </thead>
      <tbody>
        {productos.map((producto) => (
          <tr key={producto.idProducto}>
            <td>{nombreRestaurante(producto.idRestaurante)}</td>
            <td>{producto.nombre}</td>
            <td>{producto.descripcion}</td>
            <td>{producto.precio}</td>
            <td>{producto.categoria}</td>
            <td>{producto.disponible ? "Sí" : "No"}</td>
            <td>
              <button onClick={() => onEditar(producto)} disabled={cargando}>
                Editar
              </button>
              <button
                onClick={() => onEliminar(producto.idProducto)}
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

export default ProductoList;
