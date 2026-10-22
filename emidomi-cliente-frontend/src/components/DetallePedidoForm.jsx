// DetallePedidoForm.jsx
// -----------------------------------------------------------------------
// Formulario reutilizable para CREAR un detalle de pedido nuevo o EDITAR
// uno existente. Los campos idPedido e idProducto son <select> poblados con
// los datos reales (props pedidos/productos).
//
// El backend valida (Bean Validation): cantidad @Min(1), precioUnitario y
// subtotal @Positive. Este formulario:
//   - Usa validación HTML (min/step) y una validación JS que impide enviar
//     cantidad < 1 o precios <= 0 (mostrando un mensaje claro).
//   - Recalcula y muestra el subtotal automáticamente (cantidad x precio
//     unitario), garantizando que siempre sea > 0.
// -----------------------------------------------------------------------

import { useEffect, useState } from "react";

// Estructura inicial de un detalle de pedido vacío.
const DETALLE_VACIO = {
  idPedido: "",
  idProducto: "",
  cantidad: 1,
  precioUnitario: "",
  subtotal: "",
};

/**
 * @param {Object|null} detalleEnEdicion - Detalle a editar (null si se va a crear uno nuevo).
 * @param {Array} pedidos - Lista de pedidos para poblar el select de idPedido.
 * @param {Array} productos - Lista de productos para poblar el select de idProducto.
 * @param {Function} onGuardar - Función que se ejecuta al enviar el formulario.
 * @param {Function} onCancelar - Función para cancelar la edición en curso.
 * @param {boolean} cargando - Indica si hay una petición HTTP en curso.
 */
function DetallePedidoForm({
  detalleEnEdicion,
  pedidos = [],
  productos = [],
  onGuardar,
  onCancelar,
  cargando = false,
}) {
  const [formulario, setFormulario] = useState(DETALLE_VACIO);
  const [mensajeValidacion, setMensajeValidacion] = useState("");

  // Al montar o al cambiar el detalle a editar, se precargan los campos.
  useEffect(() => {
    if (detalleEnEdicion) {
      setFormulario({
        ...detalleEnEdicion,
        cantidad: detalleEnEdicion.cantidad ?? 1,
        precioUnitario: detalleEnEdicion.precioUnitario ?? "",
        subtotal: detalleEnEdicion.subtotal ?? "",
      });
    } else {
      setFormulario({
        ...DETALLE_VACIO,
        idPedido: pedidos.length > 0 ? pedidos[0].idPedido : "",
        idProducto: productos.length > 0 ? productos[0].idProducto : "",
      });
    }
    setMensajeValidacion("");
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [detalleEnEdicion, pedidos.length, productos.length]);

  // Maneja el cambio de cualquier campo de forma genérica.
  function manejarCambio(evento) {
    const { name, value } = evento.target;
    setFormulario((valoresPrevios) => ({
      ...valoresPrevios,
      [name]: value,
    }));
    setMensajeValidacion("");
  }

  // Valida los campos antes de enviar. Devuelve true si es válido.
  function validar() {
    const cantidad = Number(formulario.cantidad);
    const precio = Number(formulario.precioUnitario);

    if (!Number.isInteger(cantidad) || cantidad < 1) {
      setMensajeValidacion("La cantidad debe ser un número entero de al menos 1.");
      return false;
    }
    if (!(precio > 0)) {
      setMensajeValidacion("El precio unitario debe ser un valor mayor que 0.");
      return false;
    }
    return true;
  }

  // Envía el formulario: convierte ids y montos a número y recalcula el subtotal.
  function manejarEnvio(evento) {
    evento.preventDefault();

    if (!validar()) return;

    const cantidad = Number(formulario.cantidad);
    const precioUnitario = Number(formulario.precioUnitario);

    // Recalcula el subtotal para garantizar que sea > 0 (cumple @Positive).
    const subtotal = cantidad * precioUnitario;

    onGuardar({
      idPedido: Number(formulario.idPedido),
      idProducto: Number(formulario.idProducto),
      cantidad,
      precioUnitario,
      subtotal,
    });
  }

  // Subtotal mostrado (en vivo) a partir de los valores actuales.
  const subtotalVivo =
    Number(formulario.cantidad) > 0 && Number(formulario.precioUnitario) > 0
      ? (Number(formulario.cantidad) * Number(formulario.precioUnitario)).toFixed(2)
      : "";

  return (
    <form className="detalle-form" onSubmit={manejarEnvio}>
      <h2>
        {detalleEnEdicion ? "Editar detalle de pedido" : "Registrar nuevo detalle de pedido"}
      </h2>

      <label htmlFor="idPedido">Pedido</label>
      <select
        id="idPedido"
        name="idPedido"
        value={formulario.idPedido}
        onChange={manejarCambio}
        required
      >
        <option value="" disabled>
          Selecciona un pedido
        </option>
        {pedidos.map((pedido) => (
          <option key={pedido.idPedido} value={pedido.idPedido}>
            Pedido #{pedido.idPedido}
          </option>
        ))}
      </select>

      <label htmlFor="idProducto">Producto</label>
      <select
        id="idProducto"
        name="idProducto"
        value={formulario.idProducto}
        onChange={manejarCambio}
        required
      >
        <option value="" disabled>
          Selecciona un producto
        </option>
        {productos.map((producto) => (
          <option key={producto.idProducto} value={producto.idProducto}>
            {producto.nombre}
          </option>
        ))}
      </select>

      <label htmlFor="cantidad">Cantidad</label>
      <input
        id="cantidad"
        name="cantidad"
        type="number"
        min="1"
        step="1"
        value={formulario.cantidad}
        onChange={manejarCambio}
        required
      />

      <label htmlFor="precioUnitario">Precio unitario</label>
      <input
        id="precioUnitario"
        name="precioUnitario"
        type="number"
        min="0.01"
        step="0.01"
        value={formulario.precioUnitario}
        onChange={manejarCambio}
        required
      />

      <label htmlFor="subtotal">Subtotal</label>
      <input
        id="subtotal"
        name="subtotal"
        type="number"
        step="0.01"
        min="0.01"
        value={subtotalVivo}
        readOnly
      />

      {mensajeValidacion && (
        <p className="detalle-form__validacion">{mensajeValidacion}</p>
      )}

      <div className="detalle-form__acciones">
        <button type="submit" disabled={cargando}>
          {cargando ? "Guardando..." : detalleEnEdicion ? "Guardar cambios" : "Registrar"}
        </button>

        {detalleEnEdicion && (
          <button type="button" onClick={onCancelar} disabled={cargando}>
            Cancelar
          </button>
        )}
      </div>
    </form>
  );
}

export default DetallePedidoForm;
