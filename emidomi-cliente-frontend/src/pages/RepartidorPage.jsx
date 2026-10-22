// pages/RepartidorPage.jsx
// -----------------------------------------------------------------------
// Página del módulo Repartidor de EMIDOMI.
// Encapsula el CRUD completo de repartidores:
//   1. Carga la lista de repartidores al montar la página.
//   2. Coordina el formulario (RepartidorForm) y la tabla (RepartidorList).
//   3. Llama al servicio HTTP (repartidorService) para crear, actualizar
//      y eliminar repartidores contra el backend en Spring Boot.
// El PUT /api/repartidores/{id} ya fue corregido en el backend para copiar
// los 9 campos; el formulario envía todos los campos en edición.
// -----------------------------------------------------------------------

import { useEffect, useState } from "react";
import RepartidorForm from "../components/RepartidorForm";
import RepartidorList from "../components/RepartidorList";
import {
  actualizarRepartidor,
  crearRepartidor,
  eliminarRepartidor,
  listarRepartidores,
} from "../services/repartidorService";
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

function RepartidorPage() {
  // Lista de repartidores que se muestra en pantalla.
  const [repartidores, setRepartidores] = useState([]);

  // Repartidor que se está editando actualmente (null cuando no hay edición en curso).
  const [repartidorEnEdicion, setRepartidorEnEdicion] = useState(null);

  // Mensaje de error para mostrarle algo útil al usuario si falla una petición.
  const [mensajeError, setMensajeError] = useState("");

  // Indica si hay una petición HTTP en curso (listar, guardar o eliminar).
  const [cargando, setCargando] = useState(false);

  // Al montar, carga los repartidores existentes desde el backend.
  useEffect(() => {
    cargarRepartidores();
  }, []);

  // Consulta todos los repartidores en el backend y actualiza el estado local.
  async function cargarRepartidores() {
    setCargando(true);
    try {
      const datos = await listarRepartidores();
      setRepartidores(datos);
      setMensajeError("");
    } catch (error) {
      console.error("Error al cargar los repartidores:", error);
      setMensajeError(extraerMensajeError(error));
    } finally {
      setCargando(false);
    }
  }

  // Decide si debe CREAR un repartidor nuevo o ACTUALIZAR uno existente.
  async function manejarGuardado(datosRepartidor) {
    setCargando(true);
    try {
      if (repartidorEnEdicion) {
        await actualizarRepartidor(repartidorEnEdicion.idRepartidor, datosRepartidor);
      } else {
        await crearRepartidor(datosRepartidor);
      }
      setRepartidorEnEdicion(null);
      setMensajeError("");
      await cargarRepartidores(); // Refresca la tabla con los datos actualizados
    } catch (error) {
      console.error("Error al guardar el repartidor:", error);
      setMensajeError(extraerMensajeError(error));
    } finally {
      setCargando(false);
    }
  }

  // Prepara el formulario para editar el repartidor seleccionado en la tabla.
  function manejarEdicion(repartidor) {
    setRepartidorEnEdicion(repartidor);
  }

  // Cancela la edición en curso y limpia el formulario.
  function manejarCancelacion() {
    setRepartidorEnEdicion(null);
  }

  // Elimina un repartidor, pidiendo confirmación antes para evitar borrados accidentales.
  async function manejarEliminacion(idRepartidor) {
    const confirmado = window.confirm("¿Seguro que deseas eliminar este repartidor?");
    if (!confirmado) return;

    setCargando(true);
    try {
      await eliminarRepartidor(idRepartidor);
      setMensajeError("");
      await cargarRepartidores();
    } catch (error) {
      console.error("Error al eliminar el repartidor:", error);
      setMensajeError(extraerMensajeError(error));
    } finally {
      setCargando(false);
    }
  }

  return (
    <div className="app-container">
      <header>
        <h1>EMIDOMI · Módulo Repartidor</h1>
        <p>Gestión de repartidores: registro, edición y eliminación.</p>
      </header>

      {cargando && (
        <div className="app-container__cargando" role="status" aria-live="polite">
          Cargando...
        </div>
      )}

      {mensajeError && <div className="app-container__error">{mensajeError}</div>}

      <RepartidorForm
        repartidorEnEdicion={repartidorEnEdicion}
        onGuardar={manejarGuardado}
        onCancelar={manejarCancelacion}
        cargando={cargando}
      />

      <RepartidorList
        repartidores={repartidores}
        onEditar={manejarEdicion}
        onEliminar={manejarEliminacion}
        cargando={cargando}
      />
    </div>
  );
}

export default RepartidorPage;
