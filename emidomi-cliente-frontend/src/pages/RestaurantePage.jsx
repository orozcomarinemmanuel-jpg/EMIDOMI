// pages/RestaurantePage.jsx
// -----------------------------------------------------------------------
// Página del módulo Restaurante de EMIDOMI.
// Encapsula el CRUD completo de restaurantes:
//   1. Carga la lista de restaurantes al montar la página.
//   2. Coordina el formulario (RestauranteForm) y la tabla (RestauranteList).
//   3. Llama al servicio HTTP (restauranteService) para crear, actualizar
//      y eliminar restaurantes contra el backend en Spring Boot.
// -----------------------------------------------------------------------

import { useEffect, useState } from "react";
import RestauranteForm from "../components/RestauranteForm";
import RestauranteList from "../components/RestauranteList";
import {
  actualizarRestaurante,
  crearRestaurante,
  eliminarRestaurante,
  listarRestaurantes,
} from "../services/restauranteService";
import "../App.css";

/**
 * Extrae el mensaje legible del error devuelto por el backend.
 * El backend (GlobalExceptionHandler / controllers) responde en texto plano
 * (p. ej. "Datos inválidos: ..."), pero por robustez también se soporta el
 * caso JSON { message: "..." }.
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

  // Cuerpo en texto plano (el caso típico de este backend).
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

function RestaurantePage() {
  // Lista de restaurantes que se muestra en pantalla.
  const [restaurantes, setRestaurantes] = useState([]);

  // Restaurante que se está editando actualmente (null cuando no hay edición en curso).
  const [restauranteEnEdicion, setRestauranteEnEdicion] = useState(null);

  // Mensaje de error para mostrarle algo útil al usuario si falla una petición.
  const [mensajeError, setMensajeError] = useState("");

  // Indica si hay una petición HTTP en curso (listar, guardar o eliminar).
  const [cargando, setCargando] = useState(false);

  // Se ejecuta una sola vez, al montar el componente, para cargar
  // los restaurantes existentes desde el backend.
  useEffect(() => {
    cargarRestaurantes();
  }, []);

  // Consulta todos los restaurantes en el backend y actualiza el estado local.
  async function cargarRestaurantes() {
    setCargando(true);
    try {
      const datos = await listarRestaurantes();
      setRestaurantes(datos);
      setMensajeError("");
    } catch (error) {
      console.error("Error al cargar los restaurantes:", error);
      setMensajeError(extraerMensajeError(error));
    } finally {
      setCargando(false);
    }
  }

  // Decide si debe CREAR un restaurante nuevo o ACTUALIZAR uno existente,
  // según si restauranteEnEdicion tiene valor o no.
  async function manejarGuardado(datosRestaurante) {
    setCargando(true);
    try {
      if (restauranteEnEdicion) {
        await actualizarRestaurante(restauranteEnEdicion.idRestaurante, datosRestaurante);
      } else {
        await crearRestaurante(datosRestaurante);
      }
      setRestauranteEnEdicion(null);
      setMensajeError("");
      await cargarRestaurantes(); // Refresca la tabla con los datos actualizados
    } catch (error) {
      console.error("Error al guardar el restaurante:", error);
      setMensajeError(extraerMensajeError(error));
    } finally {
      setCargando(false);
    }
  }

  // Prepara el formulario para editar el restaurante seleccionado en la tabla.
  function manejarEdicion(restaurante) {
    setRestauranteEnEdicion(restaurante);
  }

  // Cancela la edición en curso y limpia el formulario.
  function manejarCancelacion() {
    setRestauranteEnEdicion(null);
  }

  // Elimina un restaurante, pidiendo confirmación antes para evitar borrados accidentales.
  async function manejarEliminacion(idRestaurante) {
    const confirmado = window.confirm("¿Seguro que deseas eliminar este restaurante?");
    if (!confirmado) return;

    setCargando(true);
    try {
      await eliminarRestaurante(idRestaurante);
      setMensajeError("");
      await cargarRestaurantes();
    } catch (error) {
      console.error("Error al eliminar el restaurante:", error);
      setMensajeError(extraerMensajeError(error));
    } finally {
      setCargando(false);
    }
  }

  return (
    <div className="app-container">
      <header>
        <h1>EMIDOMI · Módulo Restaurante</h1>
        <p>Gestión de restaurantes: registro, edición y eliminación.</p>
      </header>

      {cargando && (
        <div className="app-container__cargando" role="status" aria-live="polite">
          Cargando...
        </div>
      )}

      {mensajeError && <div className="app-container__error">{mensajeError}</div>}

      <RestauranteForm
        restauranteEnEdicion={restauranteEnEdicion}
        onGuardar={manejarGuardado}
        onCancelar={manejarCancelacion}
        cargando={cargando}
      />

      <RestauranteList
        restaurantes={restaurantes}
        onEditar={manejarEdicion}
        onEliminar={manejarEliminacion}
        cargando={cargando}
      />
    </div>
  );
}

export default RestaurantePage;
