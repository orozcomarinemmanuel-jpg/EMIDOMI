-- ============================================================
-- rollback_seed_productos.sql
-- Borra ÚNICAMENTE las filas insertadas por seed_productos.sql.
--
-- Criterio: coincidencia exacta de (id_restaurante, nombre) para
-- los 98 productos del seed, y además emoji IS NOT NULL (las filas
-- que existían antes del seed tienen emoji NULL y no se tocan).
--
-- No afecta otras tablas ni otros productos.
--
-- Ejecutar SIEMPRE con utf8mb4:
--   mysql --default-character-set=utf8mb4 -u root -p db_domicilios < rollback_seed_productos.sql
-- ============================================================

SET NAMES utf8mb4;

START TRANSACTION;

DELETE FROM productos
WHERE (id_restaurante, nombre) IN (
  -- 1. Burger House
  (1, 'Hamburguesa Doble Carne'),
  (1, 'Hamburguesa de Pollo Crispy'),
  (1, 'Combo Burger Personal'),
  (1, 'Perro Caliente Especial'),
  (1, 'Alitas BBQ (8 uds)'),
  (1, 'Nachos con Queso'),
  (1, 'Aros de Cebolla'),
  (1, 'Papas Rústicas'),
  (1, 'Extra Queso Cheddar'),
  (1, 'Tocineta Ahumada'),
  (1, 'Limonada Natural'),
  (1, 'Malteada de Chocolate'),
  (1, 'Agua sin Gas 600ml'),
  (1, 'Brownie con Helado'),
  -- 2. Pizza Palace
  (2, 'Pizza Hawaiana Mediana'),
  (2, 'Pizza Cuatro Quesos Mediana'),
  (2, 'Pizza Vegetariana Mediana'),
  (2, 'Pizza Familiar de Carnes'),
  (2, 'Calzone de Jamón y Queso'),
  (2, 'Pan de Ajo con Queso'),
  (2, 'Dedos de Mozzarella (6 uds)'),
  (2, 'Extra Queso Mozzarella'),
  (2, 'Borde Relleno de Queso'),
  (2, 'Gaseosa 1.5 L'),
  (2, 'Jugo de Naranja Natural'),
  (2, 'Agua con Gas 600ml'),
  (2, 'Tiramisú'),
  (2, 'Helado de Vainilla'),
  -- 3. Sabor Casero
  (3, 'Sancocho de Gallina'),
  (3, 'Ajiaco Santafereño'),
  (3, 'Mondongo con Arroz'),
  (3, 'Arroz con Pollo Casero'),
  (3, 'Chuleta de Cerdo a la Plancha'),
  (3, 'Arepa con Queso'),
  (3, 'Empanadas de Carne (3 uds)'),
  (3, 'Porción de Arroz'),
  (3, 'Porción de Aguacate'),
  (3, 'Hogao Casero'),
  (3, 'Limonada de Panela'),
  (3, 'Chocolate con Queso'),
  (3, 'Agua de Panela'),
  (3, 'Postre de Natas'),
  -- 4. Sabores de Casa
  (4, 'Almuerzo Corriente del Día'),
  (4, 'Pollo Guisado con Arroz'),
  (4, 'Carne Asada con Papas'),
  (4, 'Trucha al Ajillo'),
  (4, 'Pasta con Pollo en Salsa Blanca'),
  (4, 'Sopa del Día'),
  (4, 'Patacones con Hogao (4 uds)'),
  (4, 'Porción de Arroz'),
  (4, 'Ensalada Mixta'),
  (4, 'Jugo Natural en Leche'),
  (4, 'Limonada Natural'),
  (4, 'Gaseosa Personal'),
  (4, 'Agua sin Gas 600ml'),
  (4, 'Flan de Caramelo'),
  -- 6. Pizzería Don Marco
  (6, 'Pizza Don Marco Especial'),
  (6, 'Pizza BBQ Pollo'),
  (6, 'Pizza Napolitana'),
  (6, 'Pizza Personal de Queso'),
  (6, 'Lasaña de Carne'),
  (6, 'Bruschettas (4 uds)'),
  (6, 'Queso Fundido con Pan'),
  (6, 'Extra Jamón'),
  (6, 'Borde Relleno de Queso'),
  (6, 'Gaseosa 1.5 L'),
  (6, 'Limonada de Coco'),
  (6, 'Agua sin Gas 600ml'),
  (6, 'Panna Cotta'),
  (6, 'Brownie con Helado'),
  -- 10. Pizzería La Toscana Actualizada
  (10, 'Pizza Toscana Familiar'),
  (10, 'Pizza Prosciutto'),
  (10, 'Pasta Carbonara'),
  (10, 'Pasta Alfredo con Pollo'),
  (10, 'Risotto de Champiñones'),
  (10, 'Carpaccio de Res'),
  (10, 'Focaccia con Romero'),
  (10, 'Extra Parmesano'),
  (10, 'Aceitunas Negras'),
  (10, 'Porción de Pan de Ajo'),
  (10, 'Gaseosa 1.5 L'),
  (10, 'Agua con Gas 600ml'),
  (10, 'Limonada Natural'),
  (10, 'Tiramisú de la Casa'),
  -- 12. Sushi Sakura
  (12, 'Roll California (10 uds)'),
  (12, 'Roll Frito de Salmón (10 uds)'),
  (12, 'Roll Tempura de Camarón (10 uds)'),
  (12, 'Combo Sakura 24 Piezas'),
  (12, 'Yakisoba de Pollo'),
  (12, 'Gyozas de Cerdo (5 uds)'),
  (12, 'Edamames al Vapor'),
  (12, 'Sopa Miso'),
  (12, 'Salsa de Soya'),
  (12, 'Porción de Jengibre y Wasabi'),
  (12, 'Té Verde Frío'),
  (12, 'Limonada Natural'),
  (12, 'Agua sin Gas 600ml'),
  (12, 'Mochi de Helado (2 uds)')
)
AND emoji IS NOT NULL;

COMMIT;

-- ------------------------------------------------------------
-- Verificación (opcional): deben quedar solo las filas previas
-- ------------------------------------------------------------
-- SELECT id_restaurante, COUNT(*) AS productos
-- FROM productos
-- GROUP BY id_restaurante
-- ORDER BY id_restaurante;
