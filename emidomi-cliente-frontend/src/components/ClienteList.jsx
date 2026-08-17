// ClienteList.jsx
// -----------------------------------------------------------------------
// Muestra en una tabla los clientes registrados y ofrece, por cada fila,
// las acciones de editar y eliminar. No conoce Axios ni el backend:
// solo recibe datos y funciones por props (principio de responsabilidad única).
// -----------------------------------------------------------------------

/**
 * @param {Array} clientes - Lista de clientes a mostrar.
 * @param {Function} onEditar - Se ejecuta al dar clic en "Editar", recibe el cliente seleccionado.
 * @param {Function} onEliminar - Se ejecuta al dar clic en "Eliminar", recibe el id del cliente.
 */
function ClienteList({ clientes, onEditar, onEliminar }) {
  // Caso borde: si todavía no hay clientes cargados o la lista está vacía,
  // se muestra un mensaje en lugar de una tabla vacía.
  if (clientes.length === 0) {
    return <p className="cliente-list__vacio">No hay clientes registrados todavía.</p>;
  }

  return (
    <table className="cliente-list">
      <thead>
        <tr>
          <th>Nombre</th>
          <th>Correo</th>
          <th>Teléfono</th>
          <th>Dirección</th>
          <th>Acciones</th>
        </tr>
      </thead>
      <tbody>
        {clientes.map((cliente) => (
          // Se usa el id del cliente como "key" porque es único y estable;
          // usar el índice del arreglo aquí podría causar errores de
          // renderizado al eliminar o reordenar filas.
          <tr key={cliente.idCliente}>
            <td>{cliente.nombre}</td>
            <td>{cliente.correo}</td>
            <td>{cliente.telefono}</td>
            <td>{cliente.direccion}</td>
            <td>
              <button onClick={() => onEditar(cliente)}>Editar</button>
              <button onClick={() => onEliminar(cliente.idCliente)}>Eliminar</button>
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}

export default ClienteList;
