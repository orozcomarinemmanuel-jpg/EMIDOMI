// PagoList.jsx
// -----------------------------------------------------------------------
// Muestra en una tabla los pagos registrados y ofrece, por cada fila,
// las acciones de editar y eliminar. No conoce Axios ni el backend:
// solo recibe datos y funciones por props.
// -----------------------------------------------------------------------

// Convierte "tarjeta_credito" -> "Tarjeta crédito" para mostrarlo legible.
function etiquetaLegible(valor) {
  if (!valor) return valor;
  return valor
    .split("_")
    .map((palabra) => palabra.charAt(0).toUpperCase() + palabra.slice(1))
    .join(" ");
}

/**
 * @param {Array} pagos - Lista de pagos a mostrar.
 * @param {Function} onEditar - Se ejecuta al dar clic en "Editar", recibe el pago seleccionado.
 * @param {Function} onEliminar - Se ejecuta al dar clic en "Eliminar", recibe el id del pago.
 * @param {boolean} cargando - Indica si hay una petición HTTP en curso.
 */
function PagoList({ pagos, onEditar, onEliminar, cargando = false }) {
  if (pagos.length === 0) {
    return <p className="pago-list__vacio">No hay pagos registrados todavía.</p>;
  }

  return (
    <table className="pago-list">
      <thead>
        <tr>
          <th>Pedido</th>
          <th>Método de pago</th>
          <th>Estado</th>
          <th>Monto</th>
          <th>Referencia</th>
          <th>Acciones</th>
        </tr>
      </thead>
      <tbody>
        {pagos.map((pago) => (
          <tr key={pago.idPago}>
            <td>Pedido #{pago.idPedido}</td>
            <td>{etiquetaLegible(pago.metodoPago)}</td>
            <td>{etiquetaLegible(pago.estadoPago)}</td>
            <td>{pago.monto}</td>
            <td>{pago.referencia}</td>
            <td>
              <button onClick={() => onEditar(pago)} disabled={cargando}>
                Editar
              </button>
              <button onClick={() => onEliminar(pago.idPago)} disabled={cargando}>
                Eliminar
              </button>
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}

export default PagoList;
