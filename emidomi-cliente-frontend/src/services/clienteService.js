// clienteService.js
// -----------------------------------------------------------------------
// Capa de acceso a datos del módulo Cliente.
// Centraliza todas las llamadas HTTP hacia el backend (emidomi-cliente-service,
// Spring Boot) para que los componentes de React no necesiten conocer
// las URLs ni la configuración de Axios directamente.
// -----------------------------------------------------------------------

import axios from "axios";

// URL base del backend. Se deja como constante para poder cambiarla
// fácilmente si el puerto o el host de la API cambian (por ejemplo,
// al desplegar en un servidor distinto al local).
const API_BASE_URL = "http://localhost:8080/api/clientes";

// Instancia de Axios reutilizable con la configuración común
// (evita repetir la URL base y las cabeceras en cada petición).
const clienteApi = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

/**
 * Obtiene la lista completa de clientes registrados.
 * Corresponde al endpoint GET /api/clientes del ClienteController.
 * @returns {Promise<Array>} Arreglo de objetos cliente.
 */
export async function listarClientes() {
  const respuesta = await clienteApi.get("");
  return respuesta.data;
}

/**
 * Obtiene un cliente específico por su id.
 * Corresponde al endpoint GET /api/clientes/{id}.
 * @param {number} idCliente - Identificador del cliente a consultar.
 * @returns {Promise<Object>} Objeto cliente encontrado.
 */
export async function obtenerClientePorId(idCliente) {
  const respuesta = await clienteApi.get(`/${idCliente}`);
  return respuesta.data;
}

/**
 * Registra un nuevo cliente.
 * Corresponde al endpoint POST /api/clientes.
 * @param {Object} datosCliente - Datos del cliente a crear (sin id).
 * @returns {Promise<Object>} Cliente creado, tal como lo devuelve el backend.
 */
export async function crearCliente(datosCliente) {
  const respuesta = await clienteApi.post("", datosCliente);
  return respuesta.data;
}

/**
 * Actualiza los datos de un cliente existente.
 * Corresponde al endpoint PUT /api/clientes/{id}.
 * @param {number} idCliente - Id del cliente a actualizar.
 * @param {Object} datosCliente - Nuevos datos del cliente.
 * @returns {Promise<Object>} Cliente actualizado.
 */
export async function actualizarCliente(idCliente, datosCliente) {
  const respuesta = await clienteApi.put(`/${idCliente}`, datosCliente);
  return respuesta.data;
}

/**
 * Elimina un cliente por su id.
 * Corresponde al endpoint DELETE /api/clientes/{id}.
 * @param {number} idCliente - Id del cliente a eliminar.
 */
export async function eliminarCliente(idCliente) {
  await clienteApi.delete(`/${idCliente}`);
}
