// RepartidorList.jsx
// -----------------------------------------------------------------------
// Muestra en una tabla los repartidores registrados y ofrece, por cada fila,
// las acciones de editar y eliminar. No conoce Axios ni el backend:
// solo recibe datos y funciones por props (principio de responsabilidad única).
// -----------------------------------------------------------------------

/**
 * @param {Array} repartidores - Lista de repartidores a mostrar.
 * @param {Function} onEditar - Se ejecuta al dar clic en "Editar", recibe el repartidor seleccionado.
 * @param {Function} onEliminar - Se ejecuta al dar clic en "Eliminar", recibe el id del repartidor.
 * @param {boolean} cargando - Indica si hay una petición HTTP en curso (deshabilita las acciones).
 */
function RepartidorList({ repartidores, onEditar, onEliminar, cargando = false }) {
  if (repartidores.length === 0) {
    return <p className="repartidor-list__vacio">No hay repartidores registrados todavía.</p>;
  }

  return (
    <table className="repartidor-list">
      <thead>
        <tr>
          <th>Nombre</th>
          <th>Correo</th>
          <th>Teléfono</th>
          <th>Licencia</th>
          <th>Vehículo</th>
          <th>Placa</th>
          <th>Disponible</th>
          <th>Estado</th>
          <th>Acciones</th>
        </tr>
      </thead>
      <tbody>
        {repartidores.map((repartidor) => (
          <tr key={repartidor.idRepartidor}>
            <td>
              {repartidor.nombre} {repartidor.apellido}
            </td>
            <td>{repartidor.correo}</td>
            <td>{repartidor.telefono}</td>
            <td>{repartidor.numeroLicencia}</td>
            <td>{repartidor.vehiculo}</td>
            <td>{repartidor.placa}</td>
            <td>{repartidor.disponible ? "Sí" : "No"}</td>
            <td>{repartidor.estado ? "Activo" : "Inactivo"}</td>
            <td>
              <button onClick={() => onEditar(repartidor)} disabled={cargando}>
                Editar
              </button>
              <button
                onClick={() => onEliminar(repartidor.idRepartidor)}
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

export default RepartidorList;
