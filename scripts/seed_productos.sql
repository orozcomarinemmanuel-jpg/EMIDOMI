-- ============================================================
-- seed_productos.sql
-- Menús completos (10 a 14 productos) para los restaurantes de
-- db_domicilios. SOLO INSERT: no borra ni modifica filas existentes.
--
-- Seguro de ejecutar varias veces: cada fila se inserta solo si
-- no existe ya una con el mismo (id_restaurante, nombre).
--
-- Ejecutar SIEMPRE con utf8mb4 (por los emojis):
--   mysql --default-character-set=utf8mb4 -u root -p db_domicilios < seed_productos.sql
--
-- Reversa: rollback_seed_productos.sql
-- ============================================================

SET NAMES utf8mb4;

START TRANSACTION;

-- ------------------------------------------------------------
-- 1. Burger House - Comida rápida - Pereira
-- ------------------------------------------------------------
INSERT INTO productos (id_restaurante, nombre, descripcion, precio, categoria, disponible, imagen_url, emoji)
SELECT v.id_restaurante, v.nombre, v.descripcion, v.precio, v.categoria, 1, NULL, v.emoji
FROM (
  SELECT 1 AS id_restaurante, 'Hamburguesa Doble Carne' AS nombre, 'Dos carnes de res, queso cheddar, lechuga y tomate.' AS descripcion, 28000.00 AS precio, 'Platos principales' AS categoria, '🍔' AS emoji
  UNION ALL SELECT 1, 'Hamburguesa de Pollo Crispy', 'Filete de pollo crocante con lechuga y salsa de la casa.', 24000.00, 'Platos principales', '🍗'
  UNION ALL SELECT 1, 'Combo Burger Personal', 'Hamburguesa clásica con papas a la francesa y gaseosa personal.', 32000.00, 'Platos principales', '🍟'
  UNION ALL SELECT 1, 'Perro Caliente Especial', 'Salchicha, queso, papas ripio y salsas al gusto.', 15000.00, 'Platos principales', '🌭'
  UNION ALL SELECT 1, 'Alitas BBQ (8 uds)', 'Alitas bañadas en salsa BBQ con papas a la francesa.', 26000.00, 'Platos principales', '🍗'
  UNION ALL SELECT 1, 'Nachos con Queso', 'Totopos de maíz con queso fundido y pico de gallo.', 14000.00, 'Entradas', '🧀'
  UNION ALL SELECT 1, 'Aros de Cebolla', 'Aros de cebolla empanizados y crocantes.', 11000.00, 'Entradas', '🧅'
  UNION ALL SELECT 1, 'Papas Rústicas', 'Porción de papas cortadas en cascos con sal marina.', 9000.00, 'Adicionales', '🍟'
  UNION ALL SELECT 1, 'Extra Queso Cheddar', 'Porción adicional de queso cheddar fundido.', 3500.00, 'Adicionales', '🧀'
  UNION ALL SELECT 1, 'Tocineta Ahumada', 'Porción de tocineta crocante ahumada.', 4000.00, 'Adicionales', '🥓'
  UNION ALL SELECT 1, 'Limonada Natural', 'Limonada fresca preparada al momento.', 5000.00, 'Bebidas', '🍋'
  UNION ALL SELECT 1, 'Malteada de Chocolate', 'Malteada cremosa de chocolate con crema.', 12000.00, 'Bebidas', '🥤'
  UNION ALL SELECT 1, 'Agua sin Gas 600ml', 'Botella de agua sin gas.', 3000.00, 'Bebidas', '💧'
  UNION ALL SELECT 1, 'Brownie con Helado', 'Brownie de chocolate con helado de vainilla.', 11000.00, 'Postres', '🍰'
) AS v
WHERE NOT EXISTS (
  SELECT 1 FROM productos p
  WHERE p.id_restaurante = v.id_restaurante AND p.nombre = v.nombre
);

-- ------------------------------------------------------------
-- 2. Pizza Palace - Pizzas - Manizales
-- ------------------------------------------------------------
INSERT INTO productos (id_restaurante, nombre, descripcion, precio, categoria, disponible, imagen_url, emoji)
SELECT v.id_restaurante, v.nombre, v.descripcion, v.precio, v.categoria, 1, NULL, v.emoji
FROM (
  SELECT 2 AS id_restaurante, 'Pizza Hawaiana Mediana' AS nombre, 'Jamón, piña y queso mozzarella.' AS descripcion, 38000.00 AS precio, 'Platos principales' AS categoria, '🍍' AS emoji
  UNION ALL SELECT 2, 'Pizza Cuatro Quesos Mediana', 'Mozzarella, parmesano, provolone y queso azul.', 40000.00, 'Platos principales', '🧀'
  UNION ALL SELECT 2, 'Pizza Vegetariana Mediana', 'Champiñones, pimentón, cebolla y aceitunas.', 36000.00, 'Platos principales', '🥬'
  UNION ALL SELECT 2, 'Pizza Familiar de Carnes', 'Res, pollo, tocineta y pepperoni.', 45000.00, 'Platos principales', '🍕'
  UNION ALL SELECT 2, 'Calzone de Jamón y Queso', 'Masa horneada rellena de jamón y mozzarella.', 26000.00, 'Platos principales', '🥟'
  UNION ALL SELECT 2, 'Pan de Ajo con Queso', 'Pan horneado con mantequilla de ajo y queso.', 10000.00, 'Entradas', '🧄'
  UNION ALL SELECT 2, 'Dedos de Mozzarella (6 uds)', 'Bastones de queso empanizados con salsa marinara.', 15000.00, 'Entradas', '🧀'
  UNION ALL SELECT 2, 'Extra Queso Mozzarella', 'Porción adicional de queso mozzarella.', 5000.00, 'Adicionales', '🧀'
  UNION ALL SELECT 2, 'Borde Relleno de Queso', 'Borde de la pizza relleno de queso.', 7000.00, 'Adicionales', '🍕'
  UNION ALL SELECT 2, 'Gaseosa 1.5 L', 'Botella familiar de gaseosa.', 8000.00, 'Bebidas', '🥤'
  UNION ALL SELECT 2, 'Jugo de Naranja Natural', 'Jugo de naranja recién preparado.', 7000.00, 'Bebidas', '🍊'
  UNION ALL SELECT 2, 'Agua con Gas 600ml', 'Botella de agua con gas.', 3000.00, 'Bebidas', '💧'
  UNION ALL SELECT 2, 'Tiramisú', 'Postre italiano con café y mascarpone.', 12000.00, 'Postres', '🍰'
  UNION ALL SELECT 2, 'Helado de Vainilla', 'Dos bolas de helado de vainilla.', 8000.00, 'Postres', '🍨'
) AS v
WHERE NOT EXISTS (
  SELECT 1 FROM productos p
  WHERE p.id_restaurante = v.id_restaurante AND p.nombre = v.nombre
);

-- ------------------------------------------------------------
-- 3. Sabor Casero - Comida típica - Armenia
-- ------------------------------------------------------------
INSERT INTO productos (id_restaurante, nombre, descripcion, precio, categoria, disponible, imagen_url, emoji)
SELECT v.id_restaurante, v.nombre, v.descripcion, v.precio, v.categoria, 1, NULL, v.emoji
FROM (
  SELECT 3 AS id_restaurante, 'Sancocho de Gallina' AS nombre, 'Sopa tradicional con gallina, yuca, papa y mazorca.' AS descripcion, 22000.00 AS precio, 'Platos principales' AS categoria, '🍲' AS emoji
  UNION ALL SELECT 3, 'Ajiaco Santafereño', 'Sopa de pollo con papa criolla, guasca y aguacate.', 24000.00, 'Platos principales', '🍲'
  UNION ALL SELECT 3, 'Mondongo con Arroz', 'Sopa de mondongo con verduras y arroz aparte.', 23000.00, 'Platos principales', '🍲'
  UNION ALL SELECT 3, 'Arroz con Pollo Casero', 'Arroz con pollo desmechado y ensalada.', 20000.00, 'Platos principales', '🍛'
  UNION ALL SELECT 3, 'Chuleta de Cerdo a la Plancha', 'Acompañada de arroz, ensalada y plátano maduro.', 27000.00, 'Platos principales', '🥩'
  UNION ALL SELECT 3, 'Arepa con Queso', 'Arepa asada con queso fresco.', 7000.00, 'Entradas', '🫓'
  UNION ALL SELECT 3, 'Empanadas de Carne (3 uds)', 'Empanadas fritas rellenas de carne y papa.', 9000.00, 'Entradas', '🥟'
  UNION ALL SELECT 3, 'Porción de Arroz', 'Porción adicional de arroz blanco.', 4000.00, 'Adicionales', '🍚'
  UNION ALL SELECT 3, 'Porción de Aguacate', 'Aguacate fresco en rodajas.', 5000.00, 'Adicionales', '🥑'
  UNION ALL SELECT 3, 'Hogao Casero', 'Salsa de tomate y cebolla larga.', 3000.00, 'Adicionales', '🥫'
  UNION ALL SELECT 3, 'Limonada de Panela', 'Limonada preparada con panela.', 5000.00, 'Bebidas', '🍋'
  UNION ALL SELECT 3, 'Chocolate con Queso', 'Chocolate caliente con queso campesino.', 7000.00, 'Bebidas', '🍫'
  UNION ALL SELECT 3, 'Agua de Panela', 'Bebida tradicional de panela.', 4000.00, 'Bebidas', '🥤'
  UNION ALL SELECT 3, 'Postre de Natas', 'Postre tradicional de natas con arequipe.', 9000.00, 'Postres', '🍮'
) AS v
WHERE NOT EXISTS (
  SELECT 1 FROM productos p
  WHERE p.id_restaurante = v.id_restaurante AND p.nombre = v.nombre
);

-- ------------------------------------------------------------
-- 4. Sabores de Casa - Comida casera - Santa Rosa de Cabal
-- ------------------------------------------------------------
INSERT INTO productos (id_restaurante, nombre, descripcion, precio, categoria, disponible, imagen_url, emoji)
SELECT v.id_restaurante, v.nombre, v.descripcion, v.precio, v.categoria, 1, NULL, v.emoji
FROM (
  SELECT 4 AS id_restaurante, 'Almuerzo Corriente del Día' AS nombre, 'Sopa, principio, proteína, arroz y ensalada.' AS descripcion, 18000.00 AS precio, 'Platos principales' AS categoria, '🍛' AS emoji
  UNION ALL SELECT 4, 'Pollo Guisado con Arroz', 'Presa de pollo guisada con arroz y ensalada.', 19000.00, 'Platos principales', '🍗'
  UNION ALL SELECT 4, 'Carne Asada con Papas', 'Carne de res a la plancha con papas y ensalada.', 21000.00, 'Platos principales', '🥩'
  UNION ALL SELECT 4, 'Trucha al Ajillo', 'Trucha fresca con papas y ensalada verde.', 24000.00, 'Platos principales', '🐟'
  UNION ALL SELECT 4, 'Pasta con Pollo en Salsa Blanca', 'Penne en salsa blanca con pollo.', 20000.00, 'Platos principales', '🍝'
  UNION ALL SELECT 4, 'Sopa del Día', 'Sopa casera preparada del día.', 6000.00, 'Entradas', '🍲'
  UNION ALL SELECT 4, 'Patacones con Hogao (4 uds)', 'Patacones de plátano verde con hogao.', 8000.00, 'Entradas', '🍌'
  UNION ALL SELECT 4, 'Porción de Arroz', 'Porción adicional de arroz blanco.', 4000.00, 'Adicionales', '🍚'
  UNION ALL SELECT 4, 'Ensalada Mixta', 'Lechuga, tomate, zanahoria y vinagreta.', 5000.00, 'Adicionales', '🥗'
  UNION ALL SELECT 4, 'Jugo Natural en Leche', 'Jugo de fruta natural preparado en leche.', 7000.00, 'Bebidas', '🥛'
  UNION ALL SELECT 4, 'Limonada Natural', 'Limonada fresca preparada al momento.', 5000.00, 'Bebidas', '🍋'
  UNION ALL SELECT 4, 'Gaseosa Personal', 'Gaseosa personal de 400 ml.', 4000.00, 'Bebidas', '🥤'
  UNION ALL SELECT 4, 'Agua sin Gas 600ml', 'Botella de agua sin gas.', 3000.00, 'Bebidas', '💧'
  UNION ALL SELECT 4, 'Flan de Caramelo', 'Flan casero bañado en caramelo.', 8000.00, 'Postres', '🍮'
) AS v
WHERE NOT EXISTS (
  SELECT 1 FROM productos p
  WHERE p.id_restaurante = v.id_restaurante AND p.nombre = v.nombre
);

-- ------------------------------------------------------------
-- 6. Pizzería Don Marco - Pizzería - Chinchiná
-- ------------------------------------------------------------
INSERT INTO productos (id_restaurante, nombre, descripcion, precio, categoria, disponible, imagen_url, emoji)
SELECT v.id_restaurante, v.nombre, v.descripcion, v.precio, v.categoria, 1, NULL, v.emoji
FROM (
  SELECT 6 AS id_restaurante, 'Pizza Don Marco Especial' AS nombre, 'Pepperoni, jamón, champiñones y mozzarella.' AS descripcion, 42000.00 AS precio, 'Platos principales' AS categoria, '🍕' AS emoji
  UNION ALL SELECT 6, 'Pizza BBQ Pollo', 'Pollo, salsa BBQ, cebolla y mozzarella.', 38000.00, 'Platos principales', '🍕'
  UNION ALL SELECT 6, 'Pizza Napolitana', 'Tomate, albahaca, mozzarella y aceite de oliva.', 34000.00, 'Platos principales', '🍕'
  UNION ALL SELECT 6, 'Pizza Personal de Queso', 'Porción individual con mozzarella y orégano.', 18000.00, 'Platos principales', '🍕'
  UNION ALL SELECT 6, 'Lasaña de Carne', 'Capas de pasta, carne y salsa bechamel.', 27000.00, 'Platos principales', '🍝'
  UNION ALL SELECT 6, 'Bruschettas (4 uds)', 'Pan tostado con tomate, albahaca y aceite de oliva.', 12000.00, 'Entradas', '🍞'
  UNION ALL SELECT 6, 'Queso Fundido con Pan', 'Queso fundido acompañado de pan de la casa.', 13000.00, 'Entradas', '🧀'
  UNION ALL SELECT 6, 'Extra Jamón', 'Porción adicional de jamón.', 5000.00, 'Adicionales', '🍖'
  UNION ALL SELECT 6, 'Borde Relleno de Queso', 'Borde de la pizza relleno de queso.', 7000.00, 'Adicionales', '🧀'
  UNION ALL SELECT 6, 'Gaseosa 1.5 L', 'Botella familiar de gaseosa.', 8000.00, 'Bebidas', '🥤'
  UNION ALL SELECT 6, 'Limonada de Coco', 'Limonada cremosa preparada con coco.', 8000.00, 'Bebidas', '🥥'
  UNION ALL SELECT 6, 'Agua sin Gas 600ml', 'Botella de agua sin gas.', 3000.00, 'Bebidas', '💧'
  UNION ALL SELECT 6, 'Panna Cotta', 'Postre italiano de crema con salsa de frutos rojos.', 11000.00, 'Postres', '🍮'
  UNION ALL SELECT 6, 'Brownie con Helado', 'Brownie de chocolate con helado de vainilla.', 12000.00, 'Postres', '🍰'
) AS v
WHERE NOT EXISTS (
  SELECT 1 FROM productos p
  WHERE p.id_restaurante = v.id_restaurante AND p.nombre = v.nombre
);

-- ------------------------------------------------------------
-- 10. Pizzería La Toscana Actualizada - Italiana - Santa Rosa de Cabal
-- ------------------------------------------------------------
INSERT INTO productos (id_restaurante, nombre, descripcion, precio, categoria, disponible, imagen_url, emoji)
SELECT v.id_restaurante, v.nombre, v.descripcion, v.precio, v.categoria, 1, NULL, v.emoji
FROM (
  SELECT 10 AS id_restaurante, 'Pizza Toscana Familiar' AS nombre, 'Mozzarella, tocineta, champiñones y tomate cherry.' AS descripcion, 44000.00 AS precio, 'Platos principales' AS categoria, '🍕' AS emoji
  UNION ALL SELECT 10, 'Pizza Prosciutto', 'Jamón prosciutto, rúcula y parmesano.', 39000.00, 'Platos principales', '🍕'
  UNION ALL SELECT 10, 'Pasta Carbonara', 'Spaghetti con tocineta, huevo y parmesano.', 28000.00, 'Platos principales', '🍝'
  UNION ALL SELECT 10, 'Pasta Alfredo con Pollo', 'Fettuccine en salsa cremosa con pollo.', 29000.00, 'Platos principales', '🍝'
  UNION ALL SELECT 10, 'Risotto de Champiñones', 'Arroz cremoso con champiñones y parmesano.', 30000.00, 'Platos principales', '🍚'
  UNION ALL SELECT 10, 'Carpaccio de Res', 'Láminas finas de res con parmesano y rúcula.', 18000.00, 'Entradas', '🥩'
  UNION ALL SELECT 10, 'Focaccia con Romero', 'Pan plano horneado con romero y aceite de oliva.', 11000.00, 'Entradas', '🍞'
  UNION ALL SELECT 10, 'Extra Parmesano', 'Porción adicional de parmesano rallado.', 4000.00, 'Adicionales', '🧀'
  UNION ALL SELECT 10, 'Aceitunas Negras', 'Porción de aceitunas negras marinadas.', 4000.00, 'Adicionales', '🫒'
  UNION ALL SELECT 10, 'Porción de Pan de Ajo', 'Pan horneado con mantequilla de ajo.', 6000.00, 'Adicionales', '🧄'
  UNION ALL SELECT 10, 'Gaseosa 1.5 L', 'Botella familiar de gaseosa.', 8000.00, 'Bebidas', '🥤'
  UNION ALL SELECT 10, 'Agua con Gas 600ml', 'Botella de agua con gas.', 3000.00, 'Bebidas', '💧'
  UNION ALL SELECT 10, 'Limonada Natural', 'Limonada fresca preparada al momento.', 5000.00, 'Bebidas', '🍋'
  UNION ALL SELECT 10, 'Tiramisú de la Casa', 'Postre italiano con café y mascarpone.', 13000.00, 'Postres', '🍰'
) AS v
WHERE NOT EXISTS (
  SELECT 1 FROM productos p
  WHERE p.id_restaurante = v.id_restaurante AND p.nombre = v.nombre
);

-- ------------------------------------------------------------
-- 12. Sushi Sakura - Japonesa - Pereira
-- ------------------------------------------------------------
INSERT INTO productos (id_restaurante, nombre, descripcion, precio, categoria, disponible, imagen_url, emoji)
SELECT v.id_restaurante, v.nombre, v.descripcion, v.precio, v.categoria, 1, NULL, v.emoji
FROM (
  SELECT 12 AS id_restaurante, 'Roll California (10 uds)' AS nombre, 'Cangrejo, aguacate y pepino.' AS descripcion, 28000.00 AS precio, 'Platos principales' AS categoria, '🍣' AS emoji
  UNION ALL SELECT 12, 'Roll Frito de Salmón (10 uds)', 'Salmón, queso crema y empanizado.', 32000.00, 'Platos principales', '🍣'
  UNION ALL SELECT 12, 'Roll Tempura de Camarón (10 uds)', 'Camarón tempura y salsa de anguila.', 34000.00, 'Platos principales', '🍤'
  UNION ALL SELECT 12, 'Combo Sakura 24 Piezas', 'Selección de rolls clásicos de la casa.', 45000.00, 'Platos principales', '🍱'
  UNION ALL SELECT 12, 'Yakisoba de Pollo', 'Fideos salteados con pollo y verduras.', 27000.00, 'Platos principales', '🍜'
  UNION ALL SELECT 12, 'Gyozas de Cerdo (5 uds)', 'Empanadillas japonesas rellenas de cerdo.', 14000.00, 'Entradas', '🥟'
  UNION ALL SELECT 12, 'Edamames al Vapor', 'Vainas de soya al vapor con sal.', 9000.00, 'Entradas', '🫛'
  UNION ALL SELECT 12, 'Sopa Miso', 'Sopa japonesa de pasta de soya con tofu y cebollín.', 8000.00, 'Entradas', '🍲'
  UNION ALL SELECT 12, 'Salsa de Soya', 'Porción adicional de salsa de soya.', 2000.00, 'Adicionales', '🥫'
  UNION ALL SELECT 12, 'Porción de Jengibre y Wasabi', 'Jengibre encurtido y wasabi fresco.', 3000.00, 'Adicionales', '🥢'
  UNION ALL SELECT 12, 'Té Verde Frío', 'Té verde servido frío.', 6000.00, 'Bebidas', '🍵'
  UNION ALL SELECT 12, 'Limonada Natural', 'Limonada fresca preparada al momento.', 5000.00, 'Bebidas', '🍋'
  UNION ALL SELECT 12, 'Agua sin Gas 600ml', 'Botella de agua sin gas.', 3000.00, 'Bebidas', '💧'
  UNION ALL SELECT 12, 'Mochi de Helado (2 uds)', 'Dos mochis rellenos de helado.', 10000.00, 'Postres', '🍡'
) AS v
WHERE NOT EXISTS (
  SELECT 1 FROM productos p
  WHERE p.id_restaurante = v.id_restaurante AND p.nombre = v.nombre
);

COMMIT;

-- ------------------------------------------------------------
-- Verificación (opcional): resumen por restaurante y categoría
-- ------------------------------------------------------------
-- SELECT r.id_restaurante, r.nombre, p.categoria, COUNT(*) AS productos
-- FROM productos p
-- JOIN restaurantes r ON r.id_restaurante = p.id_restaurante
-- GROUP BY r.id_restaurante, r.nombre, p.categoria
-- ORDER BY r.id_restaurante, p.categoria;
