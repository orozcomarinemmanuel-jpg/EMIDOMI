// restauranteService.js
// -----------------------------------------------------------------------
// Capa de acceso a datos del módulo Restaurante.
// Centraliza todas las llamadas HTTP hacia el backend (emidomi-cliente-service,
// Spring Boot) para que los componentes de React no necesiten conocer
// las URLs ni la configuración de Axios directamente.
// -----------------------------------------------------------------------

import axios from "axios";

// URL base del backend para el módulo Restaurante.
const API_BASE_URL = "http://localhost:8080/api/restaurantes";

// Instancia de Axios reutilizable con la configuración común.
const restauranteApi = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

/**
 * Obtiene la lista completa de restaurantes registrados.
 * Corresponde al endpoint GET /api/restaurantes.
 * @returns {Promise<Array>} Arreglo de objetos restaurante.
 */
export async function listarRestaurantes() {
  const respuesta = await restauranteApi.get("");
  return respuesta.data;
}

/**
 * Obtiene un restaurante específico por su id.
 * Corresponde al endpoint GET /api/restaurantes/{id}.
 * @param {number} idRestaurante - Identificador del restaurante.
 * @returns {Promise<Object>} Objeto restaurante encontrado.
 */
export async function obtenerRestaurantePorId(idRestaurante) {
  const respuesta = await restauranteApi.get(`/${idRestaurante}`);
  return respuesta.data;
}

/**
 * Registra un nuevo restaurante.
 * Corresponde al endpoint POST /api/restaurantes.
 * @param {Object} datosRestaurante - Datos del restaurante a crear (sin id).
 * @returns {Promise<Object>} Restaurante creado.
 */
export async function crearRestaurante(datosRestaurante) {
  const respuesta = await restauranteApi.post("", datosRestaurante);
  return respuesta.data;
}

/**
 * Actualiza los datos de un restaurante existente.
 * Corresponde al endpoint PUT /api/restaurantes/{id}.
 * @param {number} idRestaurante - Id del restaurante a actualizar.
 * @param {Object} datosRestaurante - Nuevos datos del restaurante.
 * @returns {Promise<Object>} Restaurante actualizado.
 */
export async function actualizarRestaurante(idRestaurante, datosRestaurante) {
  const respuesta = await restauranteApi.put(`/${idRestaurante}`, datosRestaurante);
  return respuesta.data;
}

/**
 * Elimina un restaurante por su id.
 * Corresponde al endpoint DELETE /api/restaurantes/{id}.
 * @param {number} idRestaurante - Id del restaurante a eliminar.
 */
export async function eliminarRestaurante(idRestaurante) {
  await restauranteApi.delete(`/${idRestaurante}`);
}
