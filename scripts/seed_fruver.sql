-- ============================================================
-- seed_fruver.sql
-- Inserta 2 restaurantes de Fruver (categoria_general = 'fruver')
-- con 14 productos cada uno.
--
-- Seguro de repetir: cada INSERT usa WHERE NOT EXISTS (por correo y por
-- restaurante + nombre de producto). No hay ALTER TABLE: la columna
-- categoria_general ya existe.
-- ============================================================

USE db_domicilios;
SET NAMES utf8mb4;

START TRANSACTION;

-- 1) Restaurantes nuevos de Fruver (nombres inventados)
INSERT INTO restaurantes (nombre, direccion, ciudad, telefono, correo, categoria, calificacion, estado, categoria_general)
SELECT 'Frutería El Huerto Alegre', 'Calle 21 #14-30', 'Armenia', '3145550011',
       'elhuertoalegre@ejemplo.com', 'Frutas y verduras', 4.40, 1, 'fruver'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM restaurantes WHERE correo = 'elhuertoalegre@ejemplo.com');

INSERT INTO restaurantes (nombre, direccion, ciudad, telefono, correo, categoria, calificacion, estado, categoria_general)
SELECT 'Mercado Verde Fresco', 'Carrera 9 #6-12', 'Santa Rosa de Cabal', '3166660022',
       'mercadoverdefresco@ejemplo.com', 'Mercado de frutas y verduras', 4.30, 1, 'fruver'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM restaurantes WHERE correo = 'mercadoverdefresco@ejemplo.com');

-- Ids reales de los restaurantes (no se suponen)
SELECT id_restaurante INTO @rest_huerto FROM restaurantes WHERE correo = 'elhuertoalegre@ejemplo.com';
SELECT id_restaurante INTO @rest_mercado FROM restaurantes WHERE correo = 'mercadoverdefresco@ejemplo.com';

-- 2a) Productos de Frutería El Huerto Alegre (14: 5 + 2 + 3 + 3 + 1)
INSERT INTO productos (id_restaurante, nombre, descripcion, precio, categoria, disponible, imagen_url, emoji)
SELECT v.id_restaurante, v.nombre, v.descripcion, v.precio, v.categoria, 1, NULL, v.emoji
FROM (
  SELECT @rest_huerto AS id_restaurante, 'Canasta Frutal Familiar' AS nombre, 'Surtido de frutas de temporada para la semana (4 kg aprox.).' AS descripcion, 35000.00 AS precio, 'Platos principales' AS categoria, '🧺' AS emoji
  UNION ALL SELECT @rest_huerto, 'Combo Verduras de la Semana', 'Lechuga, zanahoria, espinaca y cebolla larga.', 28000.00, 'Platos principales', '🥦'
  UNION ALL SELECT @rest_huerto, 'Mix de Frutas Picadas', 'Frutas frescas picadas listas para llevar.', 15000.00, 'Platos principales', '🍉'
  UNION ALL SELECT @rest_huerto, 'Kilo de Aguacate Hass', 'Aguacate Hass maduro, por kilo.', 12000.00, 'Platos principales', '🥑'
  UNION ALL SELECT @rest_huerto, 'Kilo de Tomate Chonto', 'Tomate chonto fresco, por kilo.', 4500.00, 'Platos principales', '🍅'
  UNION ALL SELECT @rest_huerto, 'Ensalada de Frutas Picada', 'Porción de frutas picadas con limón.', 6000.00, 'Entradas', '🍓'
  UNION ALL SELECT @rest_huerto, 'Pepino en Rodajas con Limón', 'Pepino fresco en rodajas con limón y sal.', 3500.00, 'Entradas', '🥒'
  UNION ALL SELECT @rest_huerto, 'Miel de Abejas', 'Frasco pequeño de miel de abejas pura.', 7000.00, 'Adicionales', '🍯'
  UNION ALL SELECT @rest_huerto, 'Granola Casera', 'Bolsa de granola horneada en casa.', 6000.00, 'Adicionales', '🥣'
  UNION ALL SELECT @rest_huerto, 'Hierbas Aromáticas', 'Manojo de cilantro, perejil o hierbabuena.', 2000.00, 'Adicionales', '🌿'
  UNION ALL SELECT @rest_huerto, 'Jugo de Naranja Recién Exprimido', 'Naranjas exprimidas al momento.', 5000.00, 'Bebidas', '🍊'
  UNION ALL SELECT @rest_huerto, 'Agua de Panela con Limón', 'Agua de panela fría con limón.', 4000.00, 'Bebidas', '🍋'
  UNION ALL SELECT @rest_huerto, 'Coco Frío', 'Coco natural bien frío.', 6000.00, 'Bebidas', '🥥'
  UNION ALL SELECT @rest_huerto, 'Bocadillo de Guayaba con Queso', 'Bocadillo de guayaba con queso campesino.', 4500.00, 'Postres', '🍬'
) AS v
WHERE NOT EXISTS (
  SELECT 1 FROM productos p
  WHERE p.id_restaurante = v.id_restaurante AND p.nombre = v.nombre
);

-- 2b) Productos de Mercado Verde Fresco (14: 5 + 2 + 3 + 3 + 1)
INSERT INTO productos (id_restaurante, nombre, descripcion, precio, categoria, disponible, imagen_url, emoji)
SELECT v.id_restaurante, v.nombre, v.descripcion, v.precio, v.categoria, 1, NULL, v.emoji
FROM (
  SELECT @rest_mercado AS id_restaurante, 'Canasta Vegetariana' AS nombre, 'Verduras, hortalizas y frutas para cocinar la semana.' AS descripcion, 32000.00 AS precio, 'Platos principales' AS categoria, '🥗' AS emoji
  UNION ALL SELECT @rest_mercado, 'Bolsa de Fresas Frescas', 'Bolsa de fresas frescas (500 g).', 9000.00, 'Platos principales', '🍓'
  UNION ALL SELECT @rest_mercado, 'Kilo de Mango Tommy', 'Mango Tommy maduro, por kilo.', 7000.00, 'Platos principales', '🥭'
  UNION ALL SELECT @rest_mercado, 'Combo Hortalizas para Sopa', 'Zanahoria, papa, cebolla, arveja y cilantro.', 14000.00, 'Platos principales', '🥕'
  UNION ALL SELECT @rest_mercado, 'Kilo de Papaya Maradol', 'Papaya Maradol madura, por kilo.', 5500.00, 'Platos principales', '🍈'
  UNION ALL SELECT @rest_mercado, 'Ensalada Verde Lista', 'Lechuga, rúcula y espinaca lavadas y listas.', 6500.00, 'Entradas', '🥬'
  UNION ALL SELECT @rest_mercado, 'Zanahoria Baby en Palitos', 'Zanahoria baby cortada en palitos.', 4000.00, 'Entradas', '🥕'
  UNION ALL SELECT @rest_mercado, 'Limón Tahití (bolsa)', 'Bolsa de limones Tahití (aprox. 500 g).', 3000.00, 'Adicionales', '🍋'
  UNION ALL SELECT @rest_mercado, 'Cebolla Larga Manojo', 'Manojo de cebolla larga.', 1500.00, 'Adicionales', '🧅'
  UNION ALL SELECT @rest_mercado, 'Ajo en Cabeza', 'Cabeza de ajo fresco.', 2000.00, 'Adicionales', '🧄'
  UNION ALL SELECT @rest_mercado, 'Jugo Verde de Apio', 'Jugo verde de apio, pepino y manzana.', 6000.00, 'Bebidas', '🥬'
  UNION ALL SELECT @rest_mercado, 'Smoothie de Mango', 'Smoothie cremoso de mango con hielo.', 7000.00, 'Bebidas', '🥭'
  UNION ALL SELECT @rest_mercado, 'Agua de Coco en Botella', 'Botella de agua de coco natural.', 5000.00, 'Bebidas', '🥥'
  UNION ALL SELECT @rest_mercado, 'Mermelada Artesanal de Mora', 'Frasco de mermelada artesanal de mora.', 8000.00, 'Postres', '🍇'
) AS v
WHERE NOT EXISTS (
  SELECT 1 FROM productos p
  WHERE p.id_restaurante = v.id_restaurante AND p.nombre = v.nombre
);

COMMIT;

-- ============================================================
-- VERIFICACIÓN (ejecutar después del script)
-- ============================================================

-- Restaurantes con su categoría de la barra. Esperado: los dos nuevos -> fruver.
SELECT id_restaurante, nombre, categoria, categoria_general, calificacion, estado
FROM restaurantes
ORDER BY id_restaurante;

-- Conteo de productos de los dos restaurantes de fruver. Esperado: 14 y 14.
SELECT r.id_restaurante, r.nombre, COUNT(p.id_producto) AS productos
FROM restaurantes r
LEFT JOIN productos p ON p.id_restaurante = r.id_restaurante
WHERE r.categoria_general = 'fruver'
GROUP BY r.id_restaurante, r.nombre;
