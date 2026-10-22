// pages/DetallePedidoPage.jsx
// -----------------------------------------------------------------------
// Página del módulo DetallePedido de EMIDOMI.
// Encapsula el CRUD completo de detalles de pedido:
//   1. Carga la lista de detalles y la de pedidos y productos (para los
//      <select>) al montar la página.
//   2. Coordina el formulario (DetallePedidoForm) y la tabla (DetallePedidoList).
//   3. Llama al servicio HTTP (detallePedidoService) para crear, actualizar
//      y eliminar detalles contra el backend en Spring Boot.
// -----------------------------------------------------------------------

import { useEffect, useState } from "react";
import DetallePedidoForm from "../components/DetallePedidoForm";
import DetallePedidoList from "../components/DetallePedidoList";
import {
  actualizarDetallePedido,
  crearDetallePedido,
  eliminarDetallePedido,
  listarDetallesPedido,
} from "../services/detallePedidoService";
import { listarPedidos } from "../services/pedidoService";
import { listarProductos } from "../services/productoService";
import "../App.css";

/**
 * Extrae el mensaje legible del error devuelto por el backend.
 * El backend (GlobalExceptionHandler / controllers) responde en texto plano,
 * pero por robustez también se soporta el caso JSON { message: "..." }.
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

function DetallePedidoPage() {
  // Lista de detalles de pedido que se muestra en pantalla.
  const [detalles, setDetalles] = useState([]);

  // Listas de referencia para poblar los selects del formulario.
  const [pedidos, setPedidos] = useState([]);
  const [productos, setProductos] = useState([]);

  // Detalle que se está editando actualmente (null cuando no hay edición en curso).
  const [detalleEnEdicion, setDetalleEnEdicion] = useState(null);

  // Mensaje de error para mostrarle algo útil al usuario si falla una petición.
  const [mensajeError, setMensajeError] = useState("");

  // Indica si hay una petición HTTP en curso (listar, guardar o eliminar).
  const [cargando, setCargando] = useState(false);

  // Al montar, carga los detalles y las listas de referencia para los selects.
  useEffect(() => {
    cargarDetalles();
    cargarReferencias();
  }, []);

  // Consulta todos los detalles en el backend y actualiza el estado local.
  async function cargarDetalles() {
    setCargando(true);
    try {
      const datos = await listarDetallesPedido();
      setDetalles(datos);
      setMensajeError("");
    } catch (error) {
      console.error("Error al cargar los detalles de pedido:", error);
      setMensajeError(extraerMensajeError(error));
    } finally {
      setCargando(false);
    }
  }

  // Carga pedidos y productos para los selects del formulario.
  async function cargarReferencias() {
    try {
      const [pedidosDatos, productosDatos] = await Promise.all([
        listarPedidos(),
        listarProductos(),
      ]);
      setPedidos(pedidosDatos);
      setProductos(productosDatos);
    } catch (error) {
      console.error("Error al cargar las listas de referencia:", error);
    }
  }

  // Decide si debe CREAR un detalle nuevo o ACTUALIZAR uno existente.
  async function manejarGuardado(datosDetalle) {
    setCargando(true);
    try {
      if (detalleEnEdicion) {
        await actualizarDetallePedido(detalleEnEdicion.idDetalle, datosDetalle);
      } else {
        await crearDetallePedido(datosDetalle);
      }
      setDetalleEnEdicion(null);
      setMensajeError("");
      await cargarDetalles(); // Refresca la tabla con los datos actualizados
    } catch (error) {
      console.error("Error al guardar el detalle de pedido:", error);
      setMensajeError(extraerMensajeError(error));
    } finally {
      setCargando(false);
    }
  }

  // Prepara el formulario para editar el detalle seleccionado en la tabla.
  function manejarEdicion(detalle) {
    setDetalleEnEdicion(detalle);
  }

  // Cancela la edición en curso y limpia el formulario.
  function manejarCancelacion() {
    setDetalleEnEdicion(null);
  }

  // Elimina un detalle, pidiendo confirmación antes para evitar borrados accidentales.
  async function manejarEliminacion(idDetalle) {
    const confirmado = window.confirm("¿Seguro que deseas eliminar este detalle de pedido?");
    if (!confirmado) return;

    setCargando(true);
    try {
      await eliminarDetallePedido(idDetalle);
      setMensajeError("");
      await cargarDetalles();
    } catch (error) {
      console.error("Error al eliminar el detalle de pedido:", error);
      setMensajeError(extraerMensajeError(error));
    } finally {
      setCargando(false);
    }
  }

  return (
    <div className="app-container">
      <header>
        <h1>EMIDOMI · Módulo Detalle Pedido</h1>
        <p>Gestión de detalles de pedido: registro, edición y eliminación.</p>
      </header>

      {cargando && (
        <div className="app-container__cargando" role="status" aria-live="polite">
          Cargando...
        </div>
      )}

      {mensajeError && <div className="app-container__error">{mensajeError}</div>}

      <DetallePedidoForm
        detalleEnEdicion={detalleEnEdicion}
        pedidos={pedidos}
        productos={productos}
        onGuardar={manejarGuardado}
        onCancelar={manejarCancelacion}
        cargando={cargando}
      />

      <DetallePedidoList
        detalles={detalles}
        productos={productos}
        onEditar={manejarEdicion}
        onEliminar={manejarEliminacion}
        cargando={cargando}
      />
    </div>
  );
}

export default DetallePedidoPage;
