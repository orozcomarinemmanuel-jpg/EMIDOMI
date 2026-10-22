// pages/ProductoPage.jsx
// -----------------------------------------------------------------------
// Página del módulo Producto de EMIDOMI.
// Encapsula el CRUD completo de productos:
//   1. Carga la lista de productos y la de restaurantes al montar la página.
//   2. Coordina el formulario (ProductoForm) y la tabla (ProductoList).
//   3. Llama al servicio HTTP (productoService) para crear, actualizar
//      y eliminar productos contra el backend en Spring Boot.
// El formulario usa la lista de restaurantes para poblar el <select> de
// idRestaurante (obtenidos con restauranteService.listarRestaurantes).
// -----------------------------------------------------------------------

import { useEffect, useState } from "react";
import ProductoForm from "../components/ProductoForm";
import ProductoList from "../components/ProductoList";
import {
  actualizarProducto,
  crearProducto,
  eliminarProducto,
  listarProductos,
} from "../services/productoService";
import { listarRestaurantes } from "../services/restauranteService";
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

function ProductoPage() {
  // Lista de productos que se muestra en pantalla.
  const [productos, setProductos] = useState([]);

  // Lista de restaurantes, usada en el formulario (select) y en la tabla.
  const [restaurantes, setRestaurantes] = useState([]);

  // Producto que se está editando actualmente (null cuando no hay edición en curso).
  const [productoEnEdicion, setProductoEnEdicion] = useState(null);

  // Mensaje de error para mostrarle algo útil al usuario si falla una petición.
  const [mensajeError, setMensajeError] = useState("");

  // Indica si hay una petición HTTP en curso (listar, guardar o eliminar).
  const [cargando, setCargando] = useState(false);

  // Al montar, carga los productos y los restaurantes para el select.
  useEffect(() => {
    cargarProductos();
    cargarRestaurantes();
  }, []);

  // Consulta todos los productos en el backend y actualiza el estado local.
  async function cargarProductos() {
    setCargando(true);
    try {
      const datos = await listarProductos();
      setProductos(datos);
      setMensajeError("");
    } catch (error) {
      console.error("Error al cargar los productos:", error);
      setMensajeError(extraerMensajeError(error));
    } finally {
      setCargando(false);
    }
  }

  // Consulta los restaurantes (para el select de idRestaurante del formulario).
  async function cargarRestaurantes() {
    try {
      const datos = await listarRestaurantes();
      setRestaurantes(datos);
    } catch (error) {
      console.error("Error al cargar los restaurantes:", error);
    }
  }

  // Decide si debe CREAR un producto nuevo o ACTUALIZAR uno existente.
  async function manejarGuardado(datosProducto) {
    setCargando(true);
    try {
      if (productoEnEdicion) {
        await actualizarProducto(productoEnEdicion.idProducto, datosProducto);
      } else {
        await crearProducto(datosProducto);
      }
      setProductoEnEdicion(null);
      setMensajeError("");
      await cargarProductos(); // Refresca la tabla con los datos actualizados
    } catch (error) {
      console.error("Error al guardar el producto:", error);
      setMensajeError(extraerMensajeError(error));
    } finally {
      setCargando(false);
    }
  }

  // Prepara el formulario para editar el producto seleccionado en la tabla.
  function manejarEdicion(producto) {
    setProductoEnEdicion(producto);
  }

  // Cancela la edición en curso y limpia el formulario.
  function manejarCancelacion() {
    setProductoEnEdicion(null);
  }

  // Elimina un producto, pidiendo confirmación antes para evitar borrados accidentales.
  async function manejarEliminacion(idProducto) {
    const confirmado = window.confirm("¿Seguro que deseas eliminar este producto?");
    if (!confirmado) return;

    setCargando(true);
    try {
      await eliminarProducto(idProducto);
      setMensajeError("");
      await cargarProductos();
    } catch (error) {
      console.error("Error al eliminar el producto:", error);
      setMensajeError(extraerMensajeError(error));
    } finally {
      setCargando(false);
    }
  }

  return (
    <div className="app-container">
      <header>
        <h1>EMIDOMI · Módulo Producto</h1>
        <p>Gestión de productos: registro, edición y eliminación.</p>
      </header>

      {cargando && (
        <div className="app-container__cargando" role="status" aria-live="polite">
          Cargando...
        </div>
      )}

      {mensajeError && <div className="app-container__error">{mensajeError}</div>}

      <ProductoForm
        productoEnEdicion={productoEnEdicion}
        restaurantes={restaurantes}
        onGuardar={manejarGuardado}
        onCancelar={manejarCancelacion}
        cargando={cargando}
      />

      <ProductoList
        productos={productos}
        restaurantes={restaurantes}
        onEditar={manejarEdicion}
        onEliminar={manejarEliminacion}
        cargando={cargando}
      />
    </div>
  );
}

export default ProductoPage;
