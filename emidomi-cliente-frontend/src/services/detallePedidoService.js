// detallePedidoService.js
// -----------------------------------------------------------------------
// Capa de acceso a datos del módulo DetallePedido.
// Centraliza todas las llamadas HTTP hacia el backend (emidomi-cliente-service,
// Spring Boot) para que los componentes de React no necesiten conocer
// las URLs ni la configuración de Axios directamente.
// -----------------------------------------------------------------------

import axios from "axios";

// URL base del backend para el módulo DetallePedido.
const API_BASE_URL = "http://localhost:8080/api/detalle-pedido";

// Instancia de Axios reutilizable con la configuración común.
const detallePedidoApi = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

/**
 * Obtiene la lista completa de detalles de pedido registrados.
 * Corresponde al endpoint GET /api/detalle-pedido.
 * @returns {Promise<Array>} Arreglo de objetos detalle de pedido.
 */
export async function listarDetallesPedido() {
  const respuesta = await detallePedidoApi.get("");
  return respuesta.data;
}

/**
 * Obtiene los detalles de un pedido específico.
 * Corresponde al endpoint GET /api/detalle-pedido/pedido/{idPedido}.
 * @param {number} idPedido - Identificador del pedido.
 * @returns {Promise<Array>} Arreglo de detalles del pedido.
 */
export async function listarDetallesPorPedido(idPedido) {
  const respuesta = await detallePedidoApi.get(`/pedido/${idPedido}`);
  return respuesta.data;
}

/**
 * Obtiene un detalle de pedido específico por su id.
 * Corresponde al endpoint GET /api/detalle-pedido/{id}.
 * @param {number} idDetalle - Identificador del detalle de pedido.
 * @returns {Promise<Object>} Objeto detalle de pedido encontrado.
 */
export async function obtenerDetallePedidoPorId(idDetalle) {
  const respuesta = await detallePedidoApi.get(`/${idDetalle}`);
  return respuesta.data;
}

/**
 * Registra un nuevo detalle de pedido.
 * Corresponde al endpoint POST /api/detalle-pedido.
 * @param {Object} datosDetalle - Datos del detalle a crear (sin id).
 * @returns {Promise<Object>} Detalle de pedido creado.
 */
export async function crearDetallePedido(datosDetalle) {
  const respuesta = await detallePedidoApi.post("", datosDetalle);
  return respuesta.data;
}

/**
 * Actualiza los datos de un detalle de pedido existente.
 * Corresponde al endpoint PUT /api/detalle-pedido/{id}.
 * @param {number} idDetalle - Id del detalle a actualizar.
 * @param {Object} datosDetalle - Nuevos datos del detalle.
 * @returns {Promise<Object>} Detalle de pedido actualizado.
 */
export async function actualizarDetallePedido(idDetalle, datosDetalle) {
  const respuesta = await detallePedidoApi.put(`/${idDetalle}`, datosDetalle);
  return respuesta.data;
}

/**
 * Elimina un detalle de pedido por su id.
 * Corresponde al endpoint DELETE /api/detalle-pedido/{id}.
 * @param {number} idDetalle - Id del detalle a eliminar.
 */
export async function eliminarDetallePedido(idDetalle) {
  await detallePedidoApi.delete(`/${idDetalle}`);
}
