// pages/PagoPage.jsx
// -----------------------------------------------------------------------
// Página del módulo Pago de EMIDOMI.
// Encapsula el CRUD completo de pagos:
//   1. Carga la lista de pagos y la de pedidos (para el <select>) al montar.
//   2. Coordina el formulario (PagoForm) y la tabla (PagoList).
//   3. Llama al servicio HTTP (pagoService) para crear, actualizar
//      y eliminar pagos contra el backend en Spring Boot.
// El backend valida que idPedido exista (400 con el mensaje real si no);
// ese error se muestra tal cual al usuario a través de extraerMensajeError.
// -----------------------------------------------------------------------

import { useEffect, useState } from "react";
import PagoForm from "../components/PagoForm";
import PagoList from "../components/PagoList";
import {
  actualizarPago,
  crearPago,
  eliminarPago,
  listarPagos,
} from "../services/pagoService";
import { listarPedidos } from "../services/pedidoService";
import "../App.css";

/**
 * Extrae el mensaje legible del error devuelto por el backend.
 * El backend (GlobalExceptionHandler / controllers) responde en texto plano
 * (p. ej. "No existe un pedido con id: 999"), pero por robustez también se
 * soporta el caso JSON { message: "..." }.
 * @param {Error} error - Error capturado por Axios.
 * @returns {string} Mensaje legible para mostrar al usuario.
 */
function extraerMensajeError(error) {
  const respuesta = error?.response;

  if (!respuesta) {
    return "No se pudo conectar con el servidor. Verifica que el backend esté encendido.";
  }

  const datos = respuesta.data;

  if (typeof datos === "string" && datos.trim() !== "") {
    return datos.trim();
  }

  if (datos && typeof datos === "object") {
    const mensaje = datos.message || datos.mensaje;
    if (typeof mensaje === "string" && mensaje.trim() !== "") {
      return mensaje.trim();
    }
  }

  switch (respuesta.status) {
    case 400:
      return "No se pudo guardar: los datos ingresados no son válidos.";
    case 404:
      return "El recurso solicitado no existe o ya fue eliminado.";
    case 500:
      return "Ocurrió un error interno en el servidor. Inténtalo de nuevo.";
    default:
      return `Ocurrió un error inesperado (HTTP ${respuesta.status}).`;
  }
}

function PagoPage() {
  // Lista de pagos que se muestra en pantalla.
  const [pagos, setPagos] = useState([]);

  // Lista de pedidos para poblar el select de idPedido del formulario.
  const [pedidos, setPedidos] = useState([]);

  // Pago que se está editando actualmente (null cuando no hay edición en curso).
  const [pagoEnEdicion, setPagoEnEdicion] = useState(null);

  // Mensaje de error para mostrarle algo útil al usuario si falla una petición.
  const [mensajeError, setMensajeError] = useState("");

  // Indica si hay una petición HTTP en curso (listar, guardar o eliminar).
  const [cargando, setCargando] = useState(false);

  // Al montar, carga los pagos y los pedidos para el select.
  useEffect(() => {
    cargarPagos();
    cargarPedidos();
  }, []);

  // Consulta todos los pagos en el backend y actualiza el estado local.
  async function cargarPagos() {
    setCargando(true);
    try {
      const datos = await listarPagos();
      setPagos(datos);
      setMensajeError("");
    } catch (error) {
      console.error("Error al cargar los pagos:", error);
      setMensajeError(extraerMensajeError(error));
    } finally {
      setCargando(false);
    }
  }

  // Consulta los pedidos (para el select de idPedido del formulario).
  async function cargarPedidos() {
    try {
      const datos = await listarPedidos();
      setPedidos(datos);
    } catch (error) {
      console.error("Error al cargar los pedidos:", error);
    }
  }

  // Decide si debe CREAR un pago nuevo o ACTUALIZAR uno existente.
  async function manejarGuardado(datosPago) {
    setCargando(true);
    try {
      if (pagoEnEdicion) {
        await actualizarPago(pagoEnEdicion.idPago, datosPago);
      } else {
        await crearPago(datosPago);
      }
      setPagoEnEdicion(null);
      setMensajeError("");
      await cargarPagos(); // Refresca la tabla con los datos actualizados
    } catch (error) {
      console.error("Error al guardar el pago:", error);
      // El backend devuelve "No existe un pedido con id: X" con 400 si el
      // pedido no existe; extraerMensajeError lo muestra tal cual.
      setMensajeError(extraerMensajeError(error));
    } finally {
      setCargando(false);
    }
  }

  // Prepara el formulario para editar el pago seleccionado en la tabla.
  function manejarEdicion(pago) {
    setPagoEnEdicion(pago);
  }

  // Cancela la edición en curso y limpia el formulario.
  function manejarCancelacion() {
    setPagoEnEdicion(null);
  }

  // Elimina un pago, pidiendo confirmación antes para evitar borrados accidentales.
  async function manejarEliminacion(idPago) {
    const confirmado = window.confirm("¿Seguro que deseas eliminar este pago?");
    if (!confirmado) return;

    setCargando(true);
    try {
      await eliminarPago(idPago);
      setMensajeError("");
      await cargarPagos();
    } catch (error) {
      console.error("Error al eliminar el pago:", error);
      setMensajeError(extraerMensajeError(error));
    } finally {
      setCargando(false);
    }
  }

  return (
    <div className="app-container">
      <header>
        <h1>EMIDOMI · Módulo Pago</h1>
        <p>Gestión de pagos: registro, edición y eliminación.</p>
      </header>

      {cargando && (
        <div className="app-container__cargando" role="status" aria-live="polite">
          Cargando...
        </div>
      )}

      {mensajeError && <div className="app-container__error">{mensajeError}</div>}

      <PagoForm
        pagoEnEdicion={pagoEnEdicion}
        pedidos={pedidos}
        onGuardar={manejarGuardado}
        onCancelar={manejarCancelacion}
        cargando={cargando}
      />

      <PagoList
        pagos={pagos}
        onEditar={manejarEdicion}
        onEliminar={manejarEliminacion}
        cargando={cargando}
      />
    </div>
  );
}

export default PagoPage;
