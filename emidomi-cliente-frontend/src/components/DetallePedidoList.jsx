// DetallePedidoList.jsx
// -----------------------------------------------------------------------
// Muestra en una tabla los detalles de pedido registrados y ofrece, por cada
// fila, las acciones de editar y eliminar. No conoce Axios ni el backend:
// solo recibe datos y funciones por props. Resuelve el nombre del producto
// a partir de su id.
// -----------------------------------------------------------------------

/**
 * @param {Array} detalles - Lista de detalles de pedido a mostrar.
 * @param {Array} productos - Lista de productos para mostrar el nombre en la tabla.
 * @param {Function} onEditar - Se ejecuta al dar clic en "Editar", recibe el detalle seleccionado.
 * @param {Function} onEliminar - Se ejecuta al dar clic en "Eliminar", recibe el id del detalle.
 * @param {boolean} cargando - Indica si hay una petición HTTP en curso.
 */
function DetallePedidoList({
  detalles,
  productos = [],
  onEditar,
  onEliminar,
  cargando = false,
}) {
  function nombreProducto(idProducto) {
    const encontrado = productos.find((p) => p.idProducto == idProducto);
    return encontrado ? encontrado.nombre : idProducto;
  }

  if (detalles.length === 0) {
    return <p className="detalle-list__vacio">No hay detalles de pedido registrados todavía.</p>;
  }

  return (
    <table className="detalle-list">
      <thead>
        <tr>
          <th>Pedido</th>
          <th>Producto</th>
          <th>Cantidad</th>
          <th>Precio unitario</th>
          <th>Subtotal</th>
          <th>Acciones</th>
        </tr>
      </thead>
      <tbody>
        {detalles.map((detalle) => (
          <tr key={detalle.idDetalle}>
            <td>Pedido #{detalle.idPedido}</td>
            <td>{nombreProducto(detalle.idProducto)}</td>
            <td>{detalle.cantidad}</td>
            <td>{detalle.precioUnitario}</td>
            <td>{detalle.subtotal}</td>
            <td>
              <button onClick={() => onEditar(detalle)} disabled={cargando}>
                Editar
              </button>
              <button
                onClick={() => onEliminar(detalle.idDetalle)}
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

export default DetallePedidoList;
