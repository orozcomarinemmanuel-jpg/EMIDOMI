-- ============================================================
-- rollback_fix_nombre_toscana.sql
-- Revierte fix_nombre_toscana.sql:
--   "Pizzería La Toscana" -> "Pizzería La Toscana Actualizada"
--
-- Cambia SOLO el nombre del restaurante id 10.
-- Es idempotente: si el nombre ya es el anterior, no hace nada.
-- ============================================================

SET NAMES utf8mb4;

START TRANSACTION;

UPDATE restaurantes
SET nombre = 'Pizzería La Toscana Actualizada'
WHERE id_restaurante = 10
  AND nombre = 'Pizzería La Toscana';

COMMIT;

-- Verificación (debe devolver "Pizzería La Toscana Actualizada"):
-- SELECT id_restaurante, nombre FROM restaurantes WHERE id_restaurante = 10;
