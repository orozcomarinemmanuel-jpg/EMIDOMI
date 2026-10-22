// pages/PedidoPage.jsx
// -----------------------------------------------------------------------
// Página del módulo Pedido de EMIDOMI.
// Encapsula el CRUD completo de pedidos:
//   1. Carga la lista de pedidos y la de clientes, restaurantes y
//      repartidores (para los <select>) al montar la página.
//   2. Coordina el formulario (PedidoForm) y la tabla (PedidoList).
//   3. Llama al servicio HTTP (pedidoService) para crear, actualizar
//      y eliminar pedidos contra el backend en Spring Boot.
// -----------------------------------------------------------------------

import { useEffect, useState } from "react";
import PedidoForm from "../components/PedidoForm";
import PedidoList from "../components/PedidoList";
import {
  actualizarPedido,
  crearPedido,
  eliminarPedido,
  listarPedidos,
} from "../services/pedidoService";
import { listarClientes } from "../services/clienteService";
import { listarRestaurantes } from "../services/restauranteService";
import { listarRepartidores } from "../services/repartidorService";
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

function PedidoPage() {
  // Lista de pedidos que se muestra en pantalla.
  const [pedidos, setPedidos] = useState([]);

  // Listas de referencias para poblar los selects del formulario.
  const [clientes, setClientes] = useState([]);
  const [restaurantes, setRestaurantes] = useState([]);
  const [repartidores, setRepartidores] = useState([]);

  // Pedido que se está editando actualmente (null cuando no hay edición en curso).
  const [pedidoEnEdicion, setPedidoEnEdicion] = useState(null);

  // Mensaje de error para mostrarle algo útil al usuario si falla una petición.
  const [mensajeError, setMensajeError] = useState("");

  // Indica si hay una petición HTTP en curso (listar, guardar o eliminar).
  const [cargando, setCargando] = useState(false);

  // Al montar, carga los pedidos y las listas de referencia para los selects.
  useEffect(() => {
    cargarPedidos();
    cargarReferencias();
  }, []);

  // Consulta todos los pedidos en el backend y actualiza el estado local.
  async function cargarPedidos() {
    setCargando(true);
    try {
      const datos = await listarPedidos();
      setPedidos(datos);
      setMensajeError("");
    } catch (error) {
      console.error("Error al cargar los pedidos:", error);
      setMensajeError(extraerMensajeError(error));
    } finally {
      setCargando(false);
    }
  }

  // Carga clientes, restaurantes y repartidores para los selects del formulario.
  async function cargarReferencias() {
    try {
      const [clientesDatos, restaurantesDatos, repartidoresDatos] = await Promise.all([
        listarClientes(),
        listarRestaurantes(),
        listarRepartidores(),
      ]);
      setClientes(clientesDatos);
      setRestaurantes(restaurantesDatos);
      setRepartidores(repartidoresDatos);
    } catch (error) {
      console.error("Error al cargar las listas de referencia:", error);
    }
  }

  // Decide si debe CREAR un pedido nuevo o ACTUALIZAR uno existente.
  async function manejarGuardado(datosPedido) {
    setCargando(true);
    try {
      if (pedidoEnEdicion) {
        await actualizarPedido(pedidoEnEdicion.idPedido, datosPedido);
      } else {
        await crearPedido(datosPedido);
      }
      setPedidoEnEdicion(null);
      setMensajeError("");
      await cargarPedidos(); // Refresca la tabla con los datos actualizados
    } catch (error) {
      console.error("Error al guardar el pedido:", error);
      setMensajeError(extraerMensajeError(error));
    } finally {
      setCargando(false);
    }
  }

  // Prepara el formulario para editar el pedido seleccionado en la tabla.
  function manejarEdicion(pedido) {
    setPedidoEnEdicion(pedido);
  }

  // Cancela la edición en curso y limpia el formulario.
  function manejarCancelacion() {
    setPedidoEnEdicion(null);
  }

  // Elimina un pedido, pidiendo confirmación antes para evitar borrados accidentales.
  async function manejarEliminacion(idPedido) {
    const confirmado = window.confirm("¿Seguro que deseas eliminar este pedido?");
    if (!confirmado) return;

    setCargando(true);
    try {
      await eliminarPedido(idPedido);
      setMensajeError("");
      await cargarPedidos();
    } catch (error) {
      console.error("Error al eliminar el pedido:", error);
      setMensajeError(extraerMensajeError(error));
    } finally {
      setCargando(false);
    }
  }

  return (
    <div className="app-container">
      <header>
        <h1>EMIDOMI · Módulo Pedido</h1>
        <p>Gestión de pedidos: registro, edición y eliminación.</p>
      </header>

      {cargando && (
        <div className="app-container__cargando" role="status" aria-live="polite">
          Cargando...
        </div>
      )}

      {mensajeError && <div className="app-container__error">{mensajeError}</div>}

      <PedidoForm
        pedidoEnEdicion={pedidoEnEdicion}
        clientes={clientes}
        restaurantes={restaurantes}
        repartidores={repartidores}
        onGuardar={manejarGuardado}
        onCancelar={manejarCancelacion}
        cargando={cargando}
      />

      <PedidoList
        pedidos={pedidos}
        clientes={clientes}
        restaurantes={restaurantes}
        repartidores={repartidores}
        onEditar={manejarEdicion}
        onEliminar={manejarEliminacion}
        cargando={cargando}
      />
    </div>
  );
}

export default PedidoPage;
