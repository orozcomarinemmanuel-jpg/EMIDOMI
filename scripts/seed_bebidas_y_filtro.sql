-- ============================================================
-- seed_bebidas_y_filtro.sql
-- 1) Agrega categoria_general (filtro de la barra del dashboard)
-- 2) Asigna comidas_rapidas a 4 restaurantes y abre Burger House
-- 3) Inserta 2 restaurantes de Bebidas con 14 productos cada uno
--
-- EJECUTAR UNA SOLA VEZ, y ANTES de reiniciar el backend.
-- Motivo: al arrancar, Hibernate (ddl-auto=update) crea la columna si falta;
-- si el backend arranca primero, el ALTER TABLE de abajo fallará con
-- "Duplicate column name 'categoria_general'". Si pasa eso, no es grave:
-- simplemente omite el ALTER y ejecuta el resto.
-- ============================================================

SET NAMES utf8mb4;

-- 1) DDL: en MySQL no es transaccional, por eso va fuera del START TRANSACTION
ALTER TABLE restaurantes
  ADD COLUMN categoria_general VARCHAR(30) NULL AFTER categoria;

START TRANSACTION;

-- 2) Categoría de la barra para los restaurantes existentes.
--    Sabor Casero (3), Sabores de Casa (4) y Sushi Sakura (12) quedan en NULL.
UPDATE restaurantes
SET categoria_general = 'comidas_rapidas'
WHERE id_restaurante IN (1, 2, 6, 10);

-- Burger House abierto. En la tabla estado = 1 significa abierto y 0 cerrado.
UPDATE restaurantes
SET estado = 1
WHERE id_restaurante = 1
  AND estado = 0;

-- 3) Restaurantes nuevos de Bebidas (nombres inventados).
--    WHERE NOT EXISTS: si el script se ejecuta dos veces, no duplica.
INSERT INTO restaurantes (nombre, direccion, ciudad, telefono, correo, categoria, calificacion, estado, categoria_general)
SELECT 'Batidos del Parque', 'Carrera 6 #14-25', 'Pereira', '3104561122',
       'batidosdelparque@ejemplo.com', 'Jugos y batidos', 4.60, 1, 'bebidas'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM restaurantes WHERE correo = 'batidosdelparque@ejemplo.com');

INSERT INTO restaurantes (nombre, direccion, ciudad, telefono, correo, categoria, calificacion, estado, categoria_general)
SELECT 'Café Aroma Andino', 'Calle 19 #8-40', 'Armenia', '3156789012',
       'cafearomaandino@ejemplo.com', 'Cafetería', 4.80, 1, 'bebidas'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM restaurantes WHERE correo = 'cafearomaandino@ejemplo.com');

-- Guardamos los ids reales (no los suponemos)
SELECT id_restaurante INTO @rest_batidos FROM restaurantes WHERE correo = 'batidosdelparque@ejemplo.com';
SELECT id_restaurante INTO @rest_cafe    FROM restaurantes WHERE correo = 'cafearomaandino@ejemplo.com';

-- 4a) Productos de Batidos del Parque (14: 5 + 2 + 3 + 3 + 1)
INSERT INTO productos (id_restaurante, nombre, descripcion, precio, categoria, disponible, imagen_url, emoji)
SELECT v.id_restaurante, v.nombre, v.descripcion, v.precio, v.categoria, 1, NULL, v.emoji
FROM (
  SELECT @rest_batidos AS id_restaurante, 'Batido de Mango con Leche' AS nombre, 'Mango maduro licuado con leche entera.' AS descripcion, 12000.00 AS precio, 'Platos principales' AS categoria, '🥭' AS emoji
  UNION ALL SELECT @rest_batidos, 'Jugo de Maracuyá', 'Maracuyá natural con un toque de panela.', 10000.00, 'Platos principales', '🍹'
  UNION ALL SELECT @rest_batidos, 'Jugo Verde Detox', 'Espinaca, piña, pepino y limón.', 13000.00, 'Platos principales', '🥬'
  UNION ALL SELECT @rest_batidos, 'Batido de Fresa con Avena', 'Fresa, avena y leche.', 12500.00, 'Platos principales', '🍓'
  UNION ALL SELECT @rest_batidos, 'Jugo de Lulo', 'Lulo fresco con hielo y azúcar al gusto.', 9000.00, 'Platos principales', '🍊'
  UNION ALL SELECT @rest_batidos, 'Ensalada de Frutas', 'Frutas de temporada con un toque de miel.', 8500.00, 'Entradas', '🍉'
  UNION ALL SELECT @rest_batidos, 'Tostada de Aguacate', 'Pan integral con aguacate y sal marina.', 7000.00, 'Entradas', '🥑'
  UNION ALL SELECT @rest_batidos, 'Extra de Chía', 'Porción de semillas de chía.', 1500.00, 'Adicionales', '🌱'
  UNION ALL SELECT @rest_batidos, 'Extra de Proteína', 'Medida de proteína vegetal.', 4000.00, 'Adicionales', '💪'
  UNION ALL SELECT @rest_batidos, 'Leche de Almendra', 'Cambia la leche por leche de almendra.', 2500.00, 'Adicionales', '🥜'
  UNION ALL SELECT @rest_batidos, 'Agua de Coco', 'Coco natural bien frío.', 6000.00, 'Bebidas', '🥥'
  UNION ALL SELECT @rest_batidos, 'Limonada de Hierbabuena', 'Limón, hierbabuena y hielo.', 5500.00, 'Bebidas', '🍋'
  UNION ALL SELECT @rest_batidos, 'Té Helado de Durazno', 'Té negro con durazno.', 6000.00, 'Bebidas', '🍑'
  UNION ALL SELECT @rest_batidos, 'Bowl de Yogur con Granola', 'Yogur natural, granola y frutas.', 9000.00, 'Postres', '🥣'
) AS v
WHERE NOT EXISTS (
  SELECT 1 FROM productos p
  WHERE p.id_restaurante = v.id_restaurante AND p.nombre = v.nombre
);

-- 4b) Productos de Café Aroma Andino (14: 5 + 2 + 3 + 3 + 1)
INSERT INTO productos (id_restaurante, nombre, descripcion, precio, categoria, disponible, imagen_url, emoji)
SELECT v.id_restaurante, v.nombre, v.descripcion, v.precio, v.categoria, 1, NULL, v.emoji
FROM (
  SELECT @rest_cafe AS id_restaurante, 'Espresso Doble' AS nombre, 'Café de origen colombiano, doble carga.' AS descripcion, 5000.00 AS precio, 'Platos principales' AS categoria, '☕' AS emoji
  UNION ALL SELECT @rest_cafe, 'Capuchino', 'Espresso con leche vaporizada y espuma.', 7000.00, 'Platos principales', '☕'
  UNION ALL SELECT @rest_cafe, 'Latte de Avena', 'Espresso con leche de avena.', 8000.00, 'Platos principales', '🥛'
  UNION ALL SELECT @rest_cafe, 'Malteada de Café', 'Café frío con helado de vainilla.', 11000.00, 'Platos principales', '🥤'
  UNION ALL SELECT @rest_cafe, 'Cold Brew', 'Café en frío infusionado por 18 horas.', 8500.00, 'Platos principales', '🧊'
  UNION ALL SELECT @rest_cafe, 'Croissant de Mantequilla', 'Horneado cada mañana.', 7000.00, 'Entradas', '🥐'
  UNION ALL SELECT @rest_cafe, 'Sándwich de Jamón y Queso', 'Pan artesanal con jamón y queso.', 12000.00, 'Entradas', '🥪'
  UNION ALL SELECT @rest_cafe, 'Shot Extra de Café', 'Un shot adicional de espresso.', 2500.00, 'Adicionales', '☕'
  UNION ALL SELECT @rest_cafe, 'Crema Batida', 'Porción de crema batida.', 1500.00, 'Adicionales', '🍦'
  UNION ALL SELECT @rest_cafe, 'Jarabe de Vainilla', 'Jarabe natural de vainilla.', 1500.00, 'Adicionales', '🌿'
  UNION ALL SELECT @rest_cafe, 'Chocolate Caliente', 'Chocolate de mesa con leche.', 7500.00, 'Bebidas', '🍫'
  UNION ALL SELECT @rest_cafe, 'Té de Manzanilla', 'Infusión de manzanilla.', 4500.00, 'Bebidas', '🍵'
  UNION ALL SELECT @rest_cafe, 'Limonada de Panela', 'Limón y panela.', 5000.00, 'Bebidas', '🍋'
  UNION ALL SELECT @rest_cafe, 'Torta de Zanahoria', 'Porción de torta con crema de queso.', 8500.00, 'Postres', '🍰'
) AS v
WHERE NOT EXISTS (
  SELECT 1 FROM productos p
  WHERE p.id_restaurante = v.id_restaurante AND p.nombre = v.nombre
);

COMMIT;

-- ============================================================
-- VERIFICACIÓN (ejecutar después del script)
-- ============================================================

-- Restaurantes con su categoría de la barra. Esperado:
-- 1, 2, 6, 10 -> comidas_rapidas; los dos nuevos -> bebidas; 3, 4, 12 -> NULL.
-- Burger House (1) con estado 1.
SELECT id_restaurante, nombre, categoria, categoria_general, calificacion, estado
FROM restaurantes
ORDER BY id_restaurante;

-- Conteo de productos por restaurante de bebidas. Esperado: 14 y 14.
SELECT r.id_restaurante, r.nombre, COUNT(p.id_producto) AS productos
FROM restaurantes r
LEFT JOIN productos p ON p.id_restaurante = r.id_restaurante
WHERE r.categoria_general = 'bebidas'
GROUP BY r.id_restaurante, r.nombre;
