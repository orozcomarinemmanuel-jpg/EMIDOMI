// productoService.js
// -----------------------------------------------------------------------
// Capa de acceso a datos del módulo Producto.
// Centraliza todas las llamadas HTTP hacia el backend (emidomi-cliente-service,
// Spring Boot) para que los componentes de React no necesiten conocer
// las URLs ni la configuración de Axios directamente.
// -----------------------------------------------------------------------

import axios from "axios";

// URL base del backend para el módulo Producto.
const API_BASE_URL = "http://localhost:8080/api/productos";

// Instancia de Axios reutilizable con la configuración común.
const productoApi = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

/**
 * Obtiene la lista completa de productos registrados.
 * Corresponde al endpoint GET /api/productos.
 * @returns {Promise<Array>} Arreglo de objetos producto.
 */
export async function listarProductos() {
  const respuesta = await productoApi.get("");
  return respuesta.data;
}

/**
 * Obtiene los productos de un restaurante específico.
 * Corresponde al endpoint GET /api/productos/restaurante/{idRestaurante}.
 * @param {number} idRestaurante - Identificador del restaurante.
 * @returns {Promise<Array>} Arreglo de productos del restaurante.
 */
export async function listarProductosPorRestaurante(idRestaurante) {
  const respuesta = await productoApi.get(`/restaurante/${idRestaurante}`);
  return respuesta.data;
}

/**
 * Obtiene un producto específico por su id.
 * Corresponde al endpoint GET /api/productos/{id}.
 * @param {number} idProducto - Identificador del producto.
 * @returns {Promise<Object>} Objeto producto encontrado.
 */
export async function obtenerProductoPorId(idProducto) {
  const respuesta = await productoApi.get(`/${idProducto}`);
  return respuesta.data;
}

/**
 * Registra un nuevo producto.
 * Corresponde al endpoint POST /api/productos.
 * @param {Object} datosProducto - Datos del producto a crear (sin id).
 * @returns {Promise<Object>} Producto creado.
 */
export async function crearProducto(datosProducto) {
  const respuesta = await productoApi.post("", datosProducto);
  return respuesta.data;
}

/**
 * Actualiza los datos de un producto existente.
 * Corresponde al endpoint PUT /api/productos/{id}.
 * @param {number} idProducto - Id del producto a actualizar.
 * @param {Object} datosProducto - Nuevos datos del producto.
 * @returns {Promise<Object>} Producto actualizado.
 */
export async function actualizarProducto(idProducto, datosProducto) {
  const respuesta = await productoApi.put(`/${idProducto}`, datosProducto);
  return respuesta.data;
}

/**
 * Elimina un producto por su id.
 * Corresponde al endpoint DELETE /api/productos/{id}.
 * @param {number} idProducto - Id del producto a eliminar.
 */
export async function eliminarProducto(idProducto) {
  await productoApi.delete(`/${idProducto}`);
}
