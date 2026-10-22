// PedidoList.jsx
// -----------------------------------------------------------------------
// Muestra en una tabla los pedidos registrados y ofrece, por cada fila,
// las acciones de editar y eliminar. No conoce Axios ni el backend:
// solo recibe datos y funciones por props. Resuelve los nombres de cliente,
// restaurante y repartidor a partir de sus ids.
// -----------------------------------------------------------------------

/**
 * @param {Array} pedidos - Lista de pedidos a mostrar.
 * @param {Array} clientes - Lista de clientes para mostrar el nombre en la tabla.
 * @param {Array} restaurantes - Lista de restaurantes para mostrar el nombre en la tabla.
 * @param {Array} repartidores - Lista de repartidores para mostrar el nombre en la tabla.
 * @param {Function} onEditar - Se ejecuta al dar clic en "Editar", recibe el pedido seleccionado.
 * @param {Function} onEliminar - Se ejecuta al dar clic en "Eliminar", recibe el id del pedido.
 * @param {boolean} cargando - Indica si hay una petición HTTP en curso.
 */
function PedidoList({
  pedidos,
  clientes = [],
  restaurantes = [],
  repartidores = [],
  onEditar,
  onEliminar,
  cargando = false,
}) {
  function nombreCliente(idCliente) {
    const encontrado = clientes.find((c) => c.idCliente == idCliente);
    return encontrado ? `${encontrado.nombre} ${encontrado.apellido}` : idCliente;
  }

  function nombreRestaurante(idRestaurante) {
    const encontrado = restaurantes.find((r) => r.idRestaurante == idRestaurante);
    return encontrado ? encontrado.nombre : idRestaurante;
  }

  function nombreRepartidor(idRepartidor) {
    if (idRepartidor === null || idRepartidor === undefined || idRepartidor === "") {
      return "Sin asignar";
    }
    const encontrado = repartidores.find((r) => r.idRepartidor == idRepartidor);
    return encontrado ? `${encontrado.nombre} ${encontrado.apellido}` : idRepartidor;
  }

  if (pedidos.length === 0) {
    return <p className="pedido-list__vacio">No hay pedidos registrados todavía.</p>;
  }

  return (
    <table className="pedido-list">
      <thead>
        <tr>
          <th>Cliente</th>
          <th>Restaurante</th>
          <th>Repartidor</th>
          <th>Dirección</th>
          <th>Ciudad</th>
          <th>Estado</th>
          <th>Subtotal</th>
          <th>Domicilio</th>
          <th>Total</th>
          <th>Acciones</th>
        </tr>
      </thead>
      <tbody>
        {pedidos.map((pedido) => (
          <tr key={pedido.idPedido}>
            <td>{nombreCliente(pedido.idCliente)}</td>
            <td>{nombreRestaurante(pedido.idRestaurante)}</td>
            <td>{nombreRepartidor(pedido.idRepartidor)}</td>
            <td>{pedido.direccionEntrega}</td>
            <td>{pedido.ciudadEntrega}</td>
            <td>{pedido.estadoPedido?.replace(/_/g, " ")}</td>
            <td>{pedido.subtotal}</td>
            <td>{pedido.costoDomicilio}</td>
            <td>{pedido.total}</td>
            <td>
              <button onClick={() => onEditar(pedido)} disabled={cargando}>
                Editar
              </button>
              <button
                onClick={() => onEliminar(pedido.idPedido)}
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

export default PedidoList;
