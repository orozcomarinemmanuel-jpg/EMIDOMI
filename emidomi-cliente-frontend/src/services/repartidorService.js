// repartidorService.js
// -----------------------------------------------------------------------
// Capa de acceso a datos del módulo Repartidor.
// Centraliza todas las llamadas HTTP hacia el backend (emidomi-cliente-service,
// Spring Boot) para que los componentes de React no necesiten conocer
// las URLs ni la configuración de Axios directamente.
// -----------------------------------------------------------------------

import axios from "axios";

// URL base del backend para el módulo Repartidor.
const API_BASE_URL = "http://localhost:8080/api/repartidores";

// Instancia de Axios reutilizable con la configuración común.
const repartidorApi = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

/**
 * Obtiene la lista completa de repartidores registrados.
 * Corresponde al endpoint GET /api/repartidores.
 * @returns {Promise<Array>} Arreglo de objetos repartidor.
 */
export async function listarRepartidores() {
  const respuesta = await repartidorApi.get("");
  return respuesta.data;
}

/**
 * Obtiene un repartidor específico por su id.
 * Corresponde al endpoint GET /api/repartidores/{id}.
 * @param {number} idRepartidor - Identificador del repartidor.
 * @returns {Promise<Object>} Objeto repartidor encontrado.
 */
export async function obtenerRepartidorPorId(idRepartidor) {
  const respuesta = await repartidorApi.get(`/${idRepartidor}`);
  return respuesta.data;
}

/**
 * Registra un nuevo repartidor.
 * Corresponde al endpoint POST /api/repartidores.
 * @param {Object} datosRepartidor - Datos del repartidor a crear (sin id).
 * @returns {Promise<Object>} Repartidor creado.
 */
export async function crearRepartidor(datosRepartidor) {
  const respuesta = await repartidorApi.post("", datosRepartidor);
  return respuesta.data;
}

/**
 * Actualiza los datos de un repartidor existente.
 * Corresponde al endpoint PUT /api/repartidores/{id}.
 * @param {number} idRepartidor - Id del repartidor a actualizar.
 * @param {Object} datosRepartidor - Nuevos datos del repartidor.
 * @returns {Promise<Object>} Repartidor actualizado.
 */
export async function actualizarRepartidor(idRepartidor, datosRepartidor) {
  const respuesta = await repartidorApi.put(`/${idRepartidor}`, datosRepartidor);
  return respuesta.data;
}

/**
 * Elimina un repartidor por su id.
 * Corresponde al endpoint DELETE /api/repartidores/{id}.
 * @param {number} idRepartidor - Id del repartidor a eliminar.
 */
export async function eliminarRepartidor(idRepartidor) {
  await repartidorApi.delete(`/${idRepartidor}`);
}
