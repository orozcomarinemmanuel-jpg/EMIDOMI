// PedidoForm.jsx
// -----------------------------------------------------------------------
// Formulario reutilizable para CREAR un pedido nuevo o EDITAR uno existente.
// Los campos idCliente, idRestaurante e idRepartidor son <select> poblados
// con los datos reales de cada entidad (props clientes/restaurantes/repartidores).
// idRepartidor es NULLABLE: el select permite dejarlo vacío ("Sin asignar"),
// y en ese caso se envía null al backend.
// -----------------------------------------------------------------------

import { useEffect, useState } from "react";

// Valores del enum EstadoPedido del backend (serializado como string).
const ESTADOS_PEDIDO = [
  "pendiente",
  "confirmado",
  "en_preparacion",
  "en_camino",
  "entregado",
  "cancelado",
];

// Estructura inicial de un pedido vacío.
const PEDIDO_VACIO = {
  idCliente: "",
  idRestaurante: "",
  idRepartidor: "",
  direccionEntrega: "",
  ciudadEntrega: "",
  estadoPedido: "pendiente",
  subtotal: "",
  costoDomicilio: "",
  total: "",
  notas: "",
};

/**
 * @param {Object|null} pedidoEnEdicion - Pedido a editar (null si se va a crear uno nuevo).
 * @param {Array} clientes - Lista de clientes para poblar el select de idCliente.
 * @param {Array} restaurantes - Lista de restaurantes para poblar el select de idRestaurante.
 * @param {Array} repartidores - Lista de repartidores para poblar el select de idRepartidor.
 * @param {Function} onGuardar - Función que se ejecuta al enviar el formulario.
 * @param {Function} onCancelar - Función para cancelar la edición en curso.
 * @param {boolean} cargando - Indica si hay una petición HTTP en curso.
 */
function PedidoForm({
  pedidoEnEdicion,
  clientes = [],
  restaurantes = [],
  repartidores = [],
  onGuardar,
  onCancelar,
  cargando = false,
}) {
  const [formulario, setFormulario] = useState(PEDIDO_VACIO);

  // Al montar o al cambiar el pedido a editar, se precargan/normalizan los campos.
  useEffect(() => {
    if (pedidoEnEdicion) {
      setFormulario({
        ...pedidoEnEdicion,
        // idRepartidor es NULLABLE: vacío ("") representa "sin asignar".
        idRepartidor: pedidoEnEdicion.idRepartidor ?? "",
        subtotal: pedidoEnEdicion.subtotal ?? "",
        costoDomicilio: pedidoEnEdicion.costoDomicilio ?? "",
        total: pedidoEnEdicion.total ?? "",
        notas: pedidoEnEdicion.notas ?? "",
      });
    } else {
      // Al crear se preseleccionan el primer cliente y el primer restaurante.
      setFormulario({
        ...PEDIDO_VACIO,
        idCliente: clientes.length > 0 ? clientes[0].idCliente : "",
        idRestaurante: restaurantes.length > 0 ? restaurantes[0].idRestaurante : "",
      });
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [pedidoEnEdicion, clientes.length, restaurantes.length]);

  // Maneja el cambio de cualquier campo de forma genérica.
  function manejarCambio(evento) {
    const { name, value } = evento.target;
    setFormulario((valoresPrevios) => ({
      ...valoresPrevios,
      [name]: value,
    }));
  }

  // Envía el formulario: convierte los ids y montos a número, y el idRepartidor
  // vacío ("Sin asignar") a null para el backend.
  function manejarEnvio(evento) {
    evento.preventDefault();

    const datosAPagar = { ...formulario };
    datosAPagar.idCliente = Number(datosAPagar.idCliente);
    datosAPagar.idRestaurante = Number(datosAPagar.idRestaurante);
    datosAPagar.idRepartidor =
      datosAPagar.idRepartidor === "" ? null : Number(datosAPagar.idRepartidor);
    datosAPagar.subtotal = datosAPagar.subtotal === "" ? null : Number(datosAPagar.subtotal);
    datosAPagar.costoDomicilio =
      datosAPagar.costoDomicilio === "" ? null : Number(datosAPagar.costoDomicilio);
    datosAPagar.total = datosAPagar.total === "" ? null : Number(datosAPagar.total);

    onGuardar(datosAPagar);
  }

  return (
    <form className="pedido-form" onSubmit={manejarEnvio}>
      <h2>{pedidoEnEdicion ? "Editar pedido" : "Registrar nuevo pedido"}</h2>

      <label htmlFor="idCliente">Cliente</label>
      <select
        id="idCliente"
        name="idCliente"
        value={formulario.idCliente}
        onChange={manejarCambio}
        required
      >
        <option value="" disabled>
          Selecciona un cliente
        </option>
        {clientes.map((cliente) => (
          <option key={cliente.idCliente} value={cliente.idCliente}>
            {cliente.nombre} {cliente.apellido}
          </option>
        ))}
      </select>

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

      <label htmlFor="idRepartidor">Repartidor (opcional)</label>
      <select
        id="idRepartidor"
        name="idRepartidor"
        value={formulario.idRepartidor ?? ""}
        onChange={manejarCambio}
      >
        <option value="">Sin asignar</option>
        {repartidores.map((repartidor) => (
          <option key={repartidor.idRepartidor} value={repartidor.idRepartidor}>
            {repartidor.nombre} {repartidor.apellido}
          </option>
        ))}
      </select>

      <label htmlFor="direccionEntrega">Dirección de entrega</label>
      <input
        id="direccionEntrega"
        name="direccionEntrega"
        type="text"
        value={formulario.direccionEntrega}
        onChange={manejarCambio}
      />

      <label htmlFor="ciudadEntrega">Ciudad de entrega</label>
      <input
        id="ciudadEntrega"
        name="ciudadEntrega"
        type="text"
        value={formulario.ciudadEntrega}
        onChange={manejarCambio}
      />

      <label htmlFor="estadoPedido">Estado del pedido</label>
      <select
        id="estadoPedido"
        name="estadoPedido"
        value={formulario.estadoPedido}
        onChange={manejarCambio}
        required
      >
        {ESTADOS_PEDIDO.map((estado) => (
          <option key={estado} value={estado}>
            {estado.replace(/_/g, " ")}
          </option>
        ))}
      </select>

      <label htmlFor="subtotal">Subtotal</label>
      <input
        id="subtotal"
        name="subtotal"
        type="number"
        step="0.01"
        min="0"
        value={formulario.subtotal}
        onChange={manejarCambio}
      />

      <label htmlFor="costoDomicilio">Costo de domicilio</label>
      <input
        id="costoDomicilio"
        name="costoDomicilio"
        type="number"
        step="0.01"
        min="0"
        value={formulario.costoDomicilio}
        onChange={manejarCambio}
      />

      <label htmlFor="total">Total</label>
      <input
        id="total"
        name="total"
        type="number"
        step="0.01"
        min="0"
        value={formulario.total}
        onChange={manejarCambio}
      />

      <label htmlFor="notas">Notas</label>
      <textarea
        id="notas"
        name="notas"
        rows="3"
        value={formulario.notas}
        onChange={manejarCambio}
      ></textarea>

      <div className="pedido-form__acciones">
        <button type="submit" disabled={cargando}>
          {cargando ? "Guardando..." : pedidoEnEdicion ? "Guardar cambios" : "Registrar"}
        </button>

        {pedidoEnEdicion && (
          <button type="button" onClick={onCancelar} disabled={cargando}>
            Cancelar
          </button>
        )}
      </div>
    </form>
  );
}

export default PedidoForm;
