-- ============================================================
-- seed_postres.sql
-- Inserta 2 restaurantes de Postres (categoria_general = 'postres')
-- con 14 productos cada uno.
--
-- Seguro de repetir: cada INSERT usa WHERE NOT EXISTS (por correo y por
-- restaurante + nombre de producto). No hay ALTER TABLE: la columna
-- categoria_general ya existe.
-- ============================================================

USE db_domicilios;
SET NAMES utf8mb4;

START TRANSACTION;

-- 1) Restaurantes nuevos de Postres (nombres inventados)
INSERT INTO restaurantes (nombre, direccion, ciudad, telefono, correo, categoria, calificacion, estado, categoria_general)
SELECT 'Heladería Brisa Dulce', 'Avenida Circunvalar #12-40', 'Pereira', '3107771234',
       'heladeriabrisadulce@ejemplo.com', 'Heladería', 4.70, 1, 'postres'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM restaurantes WHERE correo = 'heladeriabrisadulce@ejemplo.com');

INSERT INTO restaurantes (nombre, direccion, ciudad, telefono, correo, categoria, calificacion, estado, categoria_general)
SELECT 'Dulce Esquina Pastelería', 'Carrera 23 #45-10', 'Manizales', '3128889900',
       'dulceesquinapasteleria@ejemplo.com', 'Pastelería', 4.50, 1, 'postres'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM restaurantes WHERE correo = 'dulceesquinapasteleria@ejemplo.com');

-- Ids reales de los restaurantes (no se suponen)
SELECT id_restaurante INTO @rest_helados FROM restaurantes WHERE correo = 'heladeriabrisadulce@ejemplo.com';
SELECT id_restaurante INTO @rest_pasteles FROM restaurantes WHERE correo = 'dulceesquinapasteleria@ejemplo.com';

-- 2a) Productos de Heladería Brisa Dulce (14: 5 + 2 + 3 + 3 + 1)
INSERT INTO productos (id_restaurante, nombre, descripcion, precio, categoria, disponible, imagen_url, emoji)
SELECT v.id_restaurante, v.nombre, v.descripcion, v.precio, v.categoria, 1, NULL, v.emoji
FROM (
  SELECT @rest_helados AS id_restaurante, 'Copa Tres Sabores' AS nombre, 'Tres bolas de helado a elección con topping.' AS descripcion, 14000.00 AS precio, 'Platos principales' AS categoria, '🍨' AS emoji
  UNION ALL SELECT @rest_helados, 'Sundae de Caramelo', 'Helado de vainilla con salsa de caramelo y galleta.', 13000.00, 'Platos principales', '🍮'
  UNION ALL SELECT @rest_helados, 'Banana Split', 'Banano, tres sabores de helado, crema y frutos secos.', 16000.00, 'Platos principales', '🍌'
  UNION ALL SELECT @rest_helados, 'Cono Doble', 'Dos bolas de helado en cono de barquillo.', 7000.00, 'Platos principales', '🍦'
  UNION ALL SELECT @rest_helados, 'Helado de Chocolate Belga', 'Helado cremoso de chocolate oscuro.', 6000.00, 'Platos principales', '🍫'
  UNION ALL SELECT @rest_helados, 'Paleta de Coco', 'Paleta helada de coco con trozos de fruta seca.', 4000.00, 'Entradas', '🥥'
  UNION ALL SELECT @rest_helados, 'Sándwich de Helado', 'Dos galletas de avena con helado de vainilla.', 6500.00, 'Entradas', '🍪'
  UNION ALL SELECT @rest_helados, 'Topping de Chispas', 'Porción de chispas de colores.', 1500.00, 'Adicionales', '🌈'
  UNION ALL SELECT @rest_helados, 'Salsa de Chocolate', 'Porción de salsa de chocolate caliente.', 2000.00, 'Adicionales', '🍫'
  UNION ALL SELECT @rest_helados, 'Crema Chantilly', 'Porción de crema chantilly.', 1500.00, 'Adicionales', '🍦'
  UNION ALL SELECT @rest_helados, 'Granizado de Mora', 'Granizado frío de mora con hielo triturado.', 5000.00, 'Bebidas', '🍇'
  UNION ALL SELECT @rest_helados, 'Batido de Helado de Fresa', 'Batido cremoso de fresa con helado.', 9000.00, 'Bebidas', '🍓'
  UNION ALL SELECT @rest_helados, 'Café Frío con Helado', 'Café frío con una bola de helado de vainilla.', 8500.00, 'Bebidas', '🧊'
  UNION ALL SELECT @rest_helados, 'Torta Helada de Tres Leches', 'Porción de torta helada de tres leches.', 12000.00, 'Postres', '🍰'
) AS v
WHERE NOT EXISTS (
  SELECT 1 FROM productos p
  WHERE p.id_restaurante = v.id_restaurante AND p.nombre = v.nombre
);

-- 2b) Productos de Dulce Esquina Pastelería (14: 5 + 2 + 3 + 3 + 1)
INSERT INTO productos (id_restaurante, nombre, descripcion, precio, categoria, disponible, imagen_url, emoji)
SELECT v.id_restaurante, v.nombre, v.descripcion, v.precio, v.categoria, 1, NULL, v.emoji
FROM (
  SELECT @rest_pasteles AS id_restaurante, 'Torta de Chocolate' AS nombre, 'Porción de torta de chocolate con ganache.' AS descripcion, 9000.00 AS precio, 'Platos principales' AS categoria, '🎂' AS emoji
  UNION ALL SELECT @rest_pasteles, 'Torta de Zanahoria con Queso', 'Porción de torta de zanahoria con crema de queso.', 9500.00, 'Platos principales', '🥕'
  UNION ALL SELECT @rest_pasteles, 'Cheesecake de Maracuyá', 'Cheesecake horneado con cobertura de maracuyá.', 10000.00, 'Platos principales', '🍰'
  UNION ALL SELECT @rest_pasteles, 'Waffle con Frutas', 'Waffle tibio con frutas de temporada y miel.', 12000.00, 'Platos principales', '🧇'
  UNION ALL SELECT @rest_pasteles, 'Crepe de Nutella', 'Crepe relleno de avellanas y chocolate.', 11000.00, 'Platos principales', '🥞'
  UNION ALL SELECT @rest_pasteles, 'Galletas Artesanales', 'Tres galletas de mantequilla recién horneadas.', 6000.00, 'Entradas', '🍪'
  UNION ALL SELECT @rest_pasteles, 'Mini Cupcake', 'Cupcake pequeño de vainilla con crema.', 4500.00, 'Entradas', '🧁'
  UNION ALL SELECT @rest_pasteles, 'Topping de Frutas', 'Porción de frutas frescas para decorar.', 2500.00, 'Adicionales', '🍓'
  UNION ALL SELECT @rest_pasteles, 'Crema de Avellana Extra', 'Porción adicional de crema de avellana.', 2000.00, 'Adicionales', '🌰'
  UNION ALL SELECT @rest_pasteles, 'Bola de Helado de Vainilla', 'Una bola de helado de vainilla.', 3500.00, 'Adicionales', '🍨'
  UNION ALL SELECT @rest_pasteles, 'Chocolate Caliente con Malvavisco', 'Chocolate caliente con malvaviscos.', 6500.00, 'Bebidas', '☕'
  UNION ALL SELECT @rest_pasteles, 'Té de Frutos Rojos', 'Infusión de frutos rojos servida caliente.', 5000.00, 'Bebidas', '🍵'
  UNION ALL SELECT @rest_pasteles, 'Jugo de Mora', 'Jugo natural de mora con azúcar al gusto.', 4500.00, 'Bebidas', '🍇'
  UNION ALL SELECT @rest_pasteles, 'Tarta de Limón', 'Porción de tarta de limón con merengue.', 8500.00, 'Postres', '🍋'
) AS v
WHERE NOT EXISTS (
  SELECT 1 FROM productos p
  WHERE p.id_restaurante = v.id_restaurante AND p.nombre = v.nombre
);

COMMIT;

-- ============================================================
-- VERIFICACIÓN (ejecutar después del script)
-- ============================================================

-- Restaurantes con su categoría de la barra. Esperado: los dos nuevos -> postres.
SELECT id_restaurante, nombre, categoria, categoria_general, calificacion, estado
FROM restaurantes
ORDER BY id_restaurante;

-- Conteo de productos de los dos restaurantes de postres. Esperado: 14 y 14.
SELECT r.id_restaurante, r.nombre, COUNT(p.id_producto) AS productos
FROM restaurantes r
LEFT JOIN productos p ON p.id_restaurante = r.id_restaurante
WHERE r.categoria_general = 'postres'
GROUP BY r.id_restaurante, r.nombre;
