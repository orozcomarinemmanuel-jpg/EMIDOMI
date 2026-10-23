-- ============================================================
-- fix_nombre_toscana.sql
-- Cambia SOLO el nombre del restaurante id 10:
--   "Pizzería La Toscana Actualizada" -> "Pizzería La Toscana"
--
-- No toca ninguna otra fila ni tabla.
-- Es idempotente: si el nombre ya es el nuevo, no hace nada.
--
-- NO usa la tabla productos: el seed identifica al restaurante
-- por id_restaurante = 10, así que puedes ejecutar este archivo
-- antes o después de seed_productos.sql.
-- ============================================================

SET NAMES utf8mb4;

START TRANSACTION;

UPDATE restaurantes
SET nombre = 'Pizzería La Toscana'
WHERE id_restaurante = 10
  AND nombre = 'Pizzería La Toscana Actualizada';

COMMIT;

-- Verificación (debe devolver "Pizzería La Toscana"):
-- SELECT id_restaurante, nombre FROM restaurantes WHERE id_restaurante = 10;
