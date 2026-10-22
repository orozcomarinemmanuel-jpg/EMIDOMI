// pagoService.js
// -----------------------------------------------------------------------
// Capa de acceso a datos del módulo Pago.
// Centraliza todas las llamadas HTTP hacia el backend (emidomi-cliente-service,
// Spring Boot) para que los componentes de React no necesiten conocer
// las URLs ni la configuración de Axios directamente.
// -----------------------------------------------------------------------

import axios from "axios";

// URL base del backend para el módulo Pago.
const API_BASE_URL = "http://localhost:8080/api/pagos";

// Instancia de Axios reutilizable con la configuración común.
const pagoApi = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

/**
 * Obtiene la lista completa de pagos registrados.
 * Corresponde al endpoint GET /api/pagos.
 * @returns {Promise<Array>} Arreglo de objetos pago.
 */
export async function listarPagos() {
  const respuesta = await pagoApi.get("");
  return respuesta.data;
}

/**
 * Obtiene un pago específico por su id.
 * Corresponde al endpoint GET /api/pagos/{id}.
 * @param {number} idPago - Identificador del pago.
 * @returns {Promise<Object>} Objeto pago encontrado.
 */
export async function obtenerPagoPorId(idPago) {
  const respuesta = await pagoApi.get(`/${idPago}`);
  return respuesta.data;
}

/**
 * Registra un nuevo pago.
 * Corresponde al endpoint POST /api/pagos.
 * @param {Object} datosPago - Datos del pago a crear (sin id).
 * @returns {Promise<Object>} Pago creado.
 */
export async function crearPago(datosPago) {
  const respuesta = await pagoApi.post("", datosPago);
  return respuesta.data;
}

/**
 * Actualiza los datos de un pago existente.
 * Corresponde al endpoint PUT /api/pagos/{id}.
 * @param {number} idPago - Id del pago a actualizar.
 * @param {Object} datosPago - Nuevos datos del pago.
 * @returns {Promise<Object>} Pago actualizado.
 */
export async function actualizarPago(idPago, datosPago) {
  const respuesta = await pagoApi.put(`/${idPago}`, datosPago);
  return respuesta.data;
}

/**
 * Elimina un pago por su id.
 * Corresponde al endpoint DELETE /api/pagos/{id}.
 * @param {number} idPago - Id del pago a eliminar.
 */
export async function eliminarPago(idPago) {
  await pagoApi.delete(`/${idPago}`);
}
