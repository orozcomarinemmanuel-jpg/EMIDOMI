// pedidoService.js
// -----------------------------------------------------------------------
// Capa de acceso a datos del módulo Pedido.
// Centraliza todas las llamadas HTTP hacia el backend (emidomi-cliente-service,
// Spring Boot) para que los componentes de React no necesiten conocer
// las URLs ni la configuración de Axios directamente.
// -----------------------------------------------------------------------

import axios from "axios";

// URL base del backend para el módulo Pedido.
const API_BASE_URL = "http://localhost:8080/api/pedidos";

// Instancia de Axios reutilizable con la configuración común.
const pedidoApi = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

/**
 * Obtiene la lista completa de pedidos registrados.
 * Corresponde al endpoint GET /api/pedidos.
 * @returns {Promise<Array>} Arreglo de objetos pedido.
 */
export async function listarPedidos() {
  const respuesta = await pedidoApi.get("");
  return respuesta.data;
}

/**
 * Obtiene un pedido específico por su id.
 * Corresponde al endpoint GET /api/pedidos/{id}.
 * @param {number} idPedido - Identificador del pedido.
 * @returns {Promise<Object>} Objeto pedido encontrado.
 */
export async function obtenerPedidoPorId(idPedido) {
  const respuesta = await pedidoApi.get(`/${idPedido}`);
  return respuesta.data;
}

/**
 * Registra un nuevo pedido.
 * Corresponde al endpoint POST /api/pedidos.
 * @param {Object} datosPedido - Datos del pedido a crear (sin id).
 * @returns {Promise<Object>} Pedido creado.
 */
export async function crearPedido(datosPedido) {
  const respuesta = await pedidoApi.post("", datosPedido);
  return respuesta.data;
}

/**
 * Actualiza los datos de un pedido existente.
 * Corresponde al endpoint PUT /api/pedidos/{id}.
 * @param {number} idPedido - Id del pedido a actualizar.
 * @param {Object} datosPedido - Nuevos datos del pedido.
 * @returns {Promise<Object>} Pedido actualizado.
 */
export async function actualizarPedido(idPedido, datosPedido) {
  const respuesta = await pedidoApi.put(`/${idPedido}`, datosPedido);
  return respuesta.data;
}

/**
 * Elimina un pedido por su id.
 * Corresponde al endpoint DELETE /api/pedidos/{id}.
 * @param {number} idPedido - Id del pedido a eliminar.
 */
export async function eliminarPedido(idPedido) {
  await pedidoApi.delete(`/${idPedido}`);
}
