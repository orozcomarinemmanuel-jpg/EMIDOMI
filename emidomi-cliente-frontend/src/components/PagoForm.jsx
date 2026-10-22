// PagoForm.jsx
// -----------------------------------------------------------------------
// Formulario reutilizable para CREAR un pago nuevo o EDITAR uno existente.
// idPedido es un <select> poblado con los pedidos reales (prop "pedidos").
// metodoPago y estadoPago son <select> con los valores reales de los enums
// del backend (serializados como string).
//
// El backend valida que idPedido exista (400 con el mensaje real si no);
// ese error se muestra gracias al manejo de errores de PagoPage.
// -----------------------------------------------------------------------

import { useEffect, useState } from "react";

// Valores de los enums del backend (EnumType.STRING).
const METODOS_PAGO = [
  "efectivo",
  "tarjeta_credito",
  "tarjeta_debito",
  "transferencia",
  "nequi",
  "daviplata",
];

const ESTADOS_PAGO = ["pendiente", "aprobado", "rechazado", "reembolsado"];

// Convierte "tarjeta_credito" -> "Tarjeta crédito" para mostrarlo legible.
function etiquetaLegible(valor) {
  return valor
    .split("_")
    .map((palabra) => palabra.charAt(0).toUpperCase() + palabra.slice(1))
    .join(" ");
}

// Estructura inicial de un pago vacío.
const PAGO_VACIO = {
  idPedido: "",
  metodoPago: "efectivo",
  estadoPago: "pendiente",
  monto: "",
  referencia: "",
};

/**
 * @param {Object|null} pagoEnEdicion - Pago a editar (null si se va a crear uno nuevo).
 * @param {Array} pedidos - Lista de pedidos para poblar el select de idPedido.
 * @param {Function} onGuardar - Función que se ejecuta al enviar el formulario.
 * @param {Function} onCancelar - Función para cancelar la edición en curso.
 * @param {boolean} cargando - Indica si hay una petición HTTP en curso.
 */
function PagoForm({ pagoEnEdicion, pedidos = [], onGuardar, onCancelar, cargando = false }) {
  const [formulario, setFormulario] = useState(PAGO_VACIO);

  // Al montar o al cambiar el pago a editar, se precargan los campos.
  useEffect(() => {
    if (pagoEnEdicion) {
      setFormulario({
        ...pagoEnEdicion,
        monto: pagoEnEdicion.monto ?? "",
        referencia: pagoEnEdicion.referencia ?? "",
      });
    } else {
      setFormulario({
        ...PAGO_VACIO,
        idPedido: pedidos.length > 0 ? pedidos[0].idPedido : "",
      });
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [pagoEnEdicion, pedidos.length]);

  // Maneja el cambio de cualquier campo de forma genérica.
  function manejarCambio(evento) {
    const { name, value } = evento.target;
    setFormulario((valoresPrevios) => ({
      ...valoresPrevios,
      [name]: value,
    }));
  }

  // Envía el formulario: convierte idPedido y monto a número.
  function manejarEnvio(evento) {
    evento.preventDefault();

    const datosAPagar = { ...formulario };
    datosAPagar.idPedido = Number(datosAPagar.idPedido);
    datosAPagar.monto = datosAPagar.monto === "" ? null : Number(datosAPagar.monto);

    onGuardar(datosAPagar);
  }

  return (
    <form className="pago-form" onSubmit={manejarEnvio}>
      <h2>{pagoEnEdicion ? "Editar pago" : "Registrar nuevo pago"}</h2>

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

      <label htmlFor="metodoPago">Método de pago</label>
      <select
        id="metodoPago"
        name="metodoPago"
        value={formulario.metodoPago}
        onChange={manejarCambio}
        required
      >
        {METODOS_PAGO.map((metodo) => (
          <option key={metodo} value={metodo}>
            {etiquetaLegible(metodo)}
          </option>
        ))}
      </select>

      <label htmlFor="estadoPago">Estado del pago</label>
      <select
        id="estadoPago"
        name="estadoPago"
        value={formulario.estadoPago}
        onChange={manejarCambio}
        required
      >
        {ESTADOS_PAGO.map((estado) => (
          <option key={estado} value={estado}>
            {etiquetaLegible(estado)}
          </option>
        ))}
      </select>

      <label htmlFor="monto">Monto</label>
      <input
        id="monto"
        name="monto"
        type="number"
        step="0.01"
        min="0"
        value={formulario.monto}
        onChange={manejarCambio}
      />

      <label htmlFor="referencia">Referencia</label>
      <input
        id="referencia"
        name="referencia"
        type="text"
        value={formulario.referencia}
        onChange={manejarCambio}
      />

      <div className="pago-form__acciones">
        <button type="submit" disabled={cargando}>
          {cargando ? "Guardando..." : pagoEnEdicion ? "Guardar cambios" : "Registrar"}
        </button>

        {pagoEnEdicion && (
          <button type="button" onClick={onCancelar} disabled={cargando}>
            Cancelar
          </button>
        )}
      </div>
    </form>
  );
}

export default PagoForm;
