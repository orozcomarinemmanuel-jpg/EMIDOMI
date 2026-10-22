// pages/ClientePage.jsx
// -----------------------------------------------------------------------
// Página del módulo Cliente de EMIDOMI.
// Encapsula el CRUD completo de clientes:
//   1. Carga la lista de clientes al montar la página.
//   2. Coordina el formulario (ClienteForm) y la tabla (ClienteList).
//   3. Llama al servicio HTTP (clienteService) para crear, actualizar
//      y eliminar clientes contra el backend en Spring Boot.
// Es el mismo componente que antes era "App.jsx", pero ahora vive bajo
// pages/ porque App.jsx solo define el layout y las rutas de la aplicación.
// -----------------------------------------------------------------------

import { useEffect, useState } from "react";
import ClienteForm from "../components/ClienteForm";
import ClienteList from "../components/ClienteList";
import {
  actualizarCliente,
  crearCliente,
  eliminarCliente,
  listarClientes,
} from "../services/clienteService";
import "../App.css";

/**
 * Extrae el mensaje legible del error devuelto por el backend.
 * El backend (GlobalExceptionHandler / controllers) responde en texto plano
 * (p. ej. "Ya existe un cliente registrado con ese correo."), pero por robustez
 * también se soporta el caso JSON { message: "..." }.
 * @param {Error} error - Error capturado por Axios.
 * @returns {string} Mensaje legible para mostrar al usuario.
 */
function extraerMensajeError(error) {
  const respuesta = error?.response;

  // Error de red / sin respuesta del servidor (backend apagado).
  if (!respuesta) {
    return "No se pudo conectar con el servidor. Verifica que el backend esté encendido.";
  }

  const datos = respuesta.data;

  // Cuerpo en texto plano (el caso típico de este backend): p. ej. "Recurso no encontrado."
  if (typeof datos === "string" && datos.trim() !== "") {
    return datos.trim();
  }

  // Cuerpo JSON con campo "message" o "mensaje".
  if (datos && typeof datos === "object") {
    const mensaje = datos.message || datos.mensaje;
    if (typeof mensaje === "string" && mensaje.trim() !== "") {
      return mensaje.trim();
    }
  }

  // Fallback genérico según el código de estado HTTP.
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

function ClientePage() {
  // Lista de clientes que se muestra en pantalla.
  const [clientes, setClientes] = useState([]);

  // Cliente que se está editando actualmente (null cuando no hay edición en curso).
  const [clienteEnEdicion, setClienteEnEdicion] = useState(null);

  // Mensaje de error para mostrarle algo útil al usuario si falla una petición,
  // en lugar de dejar la pantalla en blanco o solo un error en consola.
  const [mensajeError, setMensajeError] = useState("");

  // Indica si hay una petición HTTP en curso (listar, guardar o eliminar)
  // para poder mostrar un estado de carga en la interfaz.
  const [cargando, setCargando] = useState(false);

  // Se ejecuta una sola vez, al montar el componente, para cargar
  // los clientes existentes desde el backend.
  useEffect(() => {
    cargarClientes();
  }, []);

  // Consulta todos los clientes en el backend y actualiza el estado local.
  async function cargarClientes() {
    setCargando(true);
    try {
      const datos = await listarClientes();
      setClientes(datos);
      setMensajeError("");
    } catch (error) {
      console.error("Error al cargar los clientes:", error);
      setMensajeError(extraerMensajeError(error));
    } finally {
      setCargando(false);
    }
  }

  // Decide si debe CREAR un cliente nuevo o ACTUALIZAR uno existente,
  // según si clienteEnEdicion tiene valor o no.
  async function manejarGuardado(datosCliente) {
    setCargando(true);
    try {
      if (clienteEnEdicion) {
        await actualizarCliente(clienteEnEdicion.idCliente, datosCliente);
      } else {
        await crearCliente(datosCliente);
      }
      setClienteEnEdicion(null);
      setMensajeError("");
      await cargarClientes(); // Refresca la tabla con los datos actualizados
    } catch (error) {
      console.error("Error al guardar el cliente:", error);
      setMensajeError(extraerMensajeError(error));
    } finally {
      setCargando(false);
    }
  }

  // Prepara el formulario para editar el cliente seleccionado en la tabla.
  function manejarEdicion(cliente) {
    setClienteEnEdicion(cliente);
  }

  // Cancela la edición en curso y limpia el formulario.
  function manejarCancelacion() {
    setClienteEnEdicion(null);
  }

  // Elimina un cliente, pidiendo confirmación antes para evitar borrados accidentales.
  async function manejarEliminacion(idCliente) {
    const confirmado = window.confirm("¿Seguro que deseas eliminar este cliente?");
    if (!confirmado) return;

    setCargando(true);
    try {
      await eliminarCliente(idCliente);
      setMensajeError("");
      await cargarClientes();
    } catch (error) {
      console.error("Error al eliminar el cliente:", error);
      setMensajeError(extraerMensajeError(error));
    } finally {
      setCargando(false);
    }
  }

  return (
    <div className="app-container">
      <header>
        <h1>EMIDOMI · Módulo Cliente</h1>
        <p>Gestión de clientes: registro, edición y eliminación.</p>
      </header>

      {cargando && (
        <div className="app-container__cargando" role="status" aria-live="polite">
          Cargando...
        </div>
      )}

      {mensajeError && <div className="app-container__error">{mensajeError}</div>}

      <ClienteForm
        clienteEnEdicion={clienteEnEdicion}
        onGuardar={manejarGuardado}
        onCancelar={manejarCancelacion}
        cargando={cargando}
      />

      <ClienteList
        clientes={clientes}
        onEditar={manejarEdicion}
        onEliminar={manejarEliminacion}
        cargando={cargando}
      />
    </div>
  );
}

export default ClientePage;
