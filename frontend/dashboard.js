// Verificar sesión
const tokenDash = localStorage.getItem('emidomi_token');
if (!tokenDash) {
  window.location.href = 'index.html';
}

// Mapa de imágenes por restaurante (ignora mayúsculas, tildes y espacios)
const RESTAURANT_IMAGES = [
  { key: 'pizzapalace', img: 'img/palacepizza.png' },
  { key: 'saborcasero', img: 'img/saborcasero.png' },
  { key: 'saboresdecasa', img: 'img/saboresdecasa.png' },
  { key: 'pizzeriadonmarco', img: 'img/pizzeriadonmarco.png' },
  { key: 'pizzerialatoscana', img: 'img/latoscana.png' },
  { key: 'latoscana', img: 'img/latoscana.png' },
  { key: 'sushisakura', img: 'img/sushisakura.png' }
];

function normalizarNombre(nombre) {
  if (!nombre) return '';
  return nombre
    .toString()
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
    .toLowerCase()
    .replace(/[^a-z0-9]/g, '');
}

function obtenerImagenRestaurante(nombre) {
  const key = normalizarNombre(nombre);
  for (let i = 0; i < RESTAURANT_IMAGES.length; i++) {
    const item = RESTAURANT_IMAGES[i];
    if (key.includes(item.key) || item.key.includes(key)) {
      return item.img;
    }
  }
  return null;
}

// ── Carrito ──
const CARRITO_KEY = 'emidomi_carrito';
const ORDEN_CATEGORIAS = ['platosprincipales', 'entradas', 'adicionales', 'bebidas', 'postres'];

let idRestauranteActual = null;
let productosVisibles = new Map();
const restaurantesPorId = new Map();

function formatearPrecio(valor) {
  const numero = Number(valor);
  if (!isFinite(numero)) return '$ 0';
  return '$ ' + numero.toLocaleString('es-CO', { maximumFractionDigits: 0 });
}

function leerCarrito() {
  try {
    const crudo = localStorage.getItem(CARRITO_KEY);
    if (!crudo) return [];
    const datos = JSON.parse(crudo);
    if (!Array.isArray(datos)) return [];
    return datos
      .filter(item => item && item.idProducto != null && Number(item.cantidad) > 0)
      .map(item => {
        const idRest = item.idRestaurante != null ? item.idRestaurante : item.id_restaurante;
        const nombreRest = item.nombreRestaurante || item.nombre_restaurante || (idRest != null ? (restaurantesPorId.get(String(idRest)) || '') : '');
        return {
          idProducto: item.idProducto,
          nombre: item.nombre || 'Producto',
          precio: Number(item.precio) || 0,
          cantidad: Math.max(1, Math.floor(Number(item.cantidad))),
          idRestaurante: idRest,
          nombreRestaurante: nombreRest || ''
        };
      });
  } catch (err) {
    return [];
  }
}

function guardarCarrito(carrito) {
  const normalizado = carrito
    .filter(item => item && item.idProducto != null && Number(item.cantidad) > 0)
    .map(item => ({
      idProducto: item.idProducto,
      nombre: item.nombre || 'Producto',
      precio: Number(item.precio) || 0,
      cantidad: Math.max(1, Math.floor(Number(item.cantidad))),
      idRestaurante: item.idRestaurante,
      nombreRestaurante: item.nombreRestaurante || (item.idRestaurante != null ? (restaurantesPorId.get(String(item.idRestaurante)) || '') : '')
    }));
  localStorage.setItem(CARRITO_KEY, JSON.stringify(normalizado));
  renderCarritoUI();
}

function agregarProducto(prod) {
  const idRest = prod.idRestaurante != null ? prod.idRestaurante : idRestauranteActual;
  let nombreRest = prod.nombreRestaurante || '';
  if (!nombreRest && idRest != null) {
    nombreRest = restaurantesPorId.get(String(idRest)) || '';
  }
  if (!nombreRest) {
    const titulo = document.getElementById('productsTitle')?.textContent || '';
    if (titulo.indexOf('Productos - ') === 0) {
      nombreRest = titulo.replace('Productos - ', '').trim();
    }
  }
  const carrito = leerCarrito();
  const existente = carrito.find(item => String(item.idProducto) === String(prod.idProducto));
  if (existente) {
    existente.cantidad += 1;
    if (!existente.nombreRestaurante && nombreRest) existente.nombreRestaurante = nombreRest;
    if (existente.idRestaurante == null && idRest != null) existente.idRestaurante = idRest;
  } else {
    carrito.push({
      idProducto: prod.idProducto,
      nombre: prod.nombre || 'Producto',
      precio: Number(prod.precio) || 0,
      cantidad: 1,
      idRestaurante: idRest,
      nombreRestaurante: nombreRest
    });
  }
  guardarCarrito(carrito);
}

function cambiarCantidad(idProducto, delta) {
  const carrito = leerCarrito();
  const indice = carrito.findIndex(item => String(item.idProducto) === String(idProducto));
  if (indice === -1) return;
  carrito[indice].cantidad += delta;
  const nuevo = carrito[indice].cantidad > 0 ? carrito : carrito.filter((_, i) => i !== indice);
  guardarCarrito(nuevo);
}

function actualizarZonasProductos() {
  const contenedor = document.getElementById('productsList');
  if (!contenedor) return;
  const cantidades = new Map();
  leerCarrito().forEach(item => cantidades.set(String(item.idProducto), item.cantidad));
  contenedor.querySelectorAll('.product-card').forEach(card => {
    const zona = card.querySelector('.product-action');
    if (!zona) return;
    const cantidad = cantidades.get(card.getAttribute('data-id-producto')) || 0;
    if (cantidad > 0) {
      zona.innerHTML = `<div class="qty-control"><button type="button" class="qty-btn" data-action="dec" aria-label="Quitar una unidad">−</button><span class="qty-value">${cantidad}</span><button type="button" class="qty-btn" data-action="inc" aria-label="Agregar una unidad">+</button></div>`;
    } else {
      zona.innerHTML = `<button type="button" class="btn-add" data-action="add">Agregar</button>`;
    }
  });
}

function renderCarritoUI() {
  const carrito = leerCarrito();
  const totalUnidades = carrito.reduce((total, item) => total + item.cantidad, 0);
  const totalGeneral = carrito.reduce((total, item) => total + item.precio * item.cantidad, 0);

  const badge = document.getElementById('cartCount');
  if (badge) {
    badge.textContent = String(totalUnidades);
    badge.hidden = false;
  }

  const gruposContenedor = document.getElementById('cartGroups');
  if (gruposContenedor) {
    gruposContenedor.innerHTML = '';
    if (carrito.length === 0) {
      // vacío
    } else {
      const grupos = new Map();
      carrito.forEach(item => {
        const idRest = item.idRestaurante != null ? String(item.idRestaurante) : 'sin-restaurante';
        const nombreRest = item.nombreRestaurante && item.nombreRestaurante.trim() !== ''
          ? item.nombreRestaurante
          : (item.idRestaurante != null ? (restaurantesPorId.get(String(item.idRestaurante)) || `Restaurante #${item.idRestaurante}`) : 'Sin restaurante');
        if (!grupos.has(idRest)) grupos.set(idRest, { idRest, nombreRest, items: [] });
        grupos.get(idRest).items.push(item);
      });

      Array.from(grupos.values()).forEach(grupo => {
        const grupoDiv = document.createElement('section');
        grupoDiv.className = 'cart-group';
        grupoDiv.setAttribute('data-id-restaurante', grupo.idRest);

        const header = document.createElement('header');
        header.className = 'cart-group-header';
        const titulo = document.createElement('span');
        titulo.textContent = grupo.nombreRest;
        header.appendChild(titulo);
        grupoDiv.appendChild(header);

        const lista = document.createElement('ul');
        lista.className = 'cart-group-items';

        let subtotalGrupo = 0;
        grupo.items.forEach(item => {
          subtotalGrupo += item.precio * item.cantidad;
          const li = document.createElement('li');
          li.className = 'cart-item';
          li.setAttribute('data-id-producto', String(item.idProducto));

          const info = document.createElement('div');
          info.className = 'cart-item-info';
          const nombre = document.createElement('span');
          nombre.className = 'cart-item-name';
          nombre.textContent = item.nombre;
          const precio = document.createElement('span');
          precio.className = 'cart-item-price';
          precio.textContent = `${formatearPrecio(item.precio)} c/u`;
          info.appendChild(nombre);
          info.appendChild(precio);

          const qty = document.createElement('div');
          qty.className = 'qty-control qty-control-sm';
          qty.innerHTML = `<button type="button" class="qty-btn" data-cart-action="dec" aria-label="Quitar una unidad">−</button><span class="qty-value">${item.cantidad}</span><button type="button" class="qty-btn" data-cart-action="inc" aria-label="Agregar una unidad">+</button>`;

          li.appendChild(info);
          li.appendChild(qty);
          lista.appendChild(li);
        });

        grupoDiv.appendChild(lista);

        const subtotalDiv = document.createElement('div');
        subtotalDiv.className = 'cart-group-subtotal';
        const txt = document.createElement('span');
        txt.textContent = 'Subtotal';
        const val = document.createElement('strong');
        val.textContent = formatearPrecio(subtotalGrupo);
        subtotalDiv.appendChild(txt);
        subtotalDiv.appendChild(val);
        grupoDiv.appendChild(subtotalDiv);

        gruposContenedor.appendChild(grupoDiv);
      });
    }
  }

  const vacio = document.getElementById('cartEmpty');
  if (vacio) vacio.hidden = carrito.length > 0;

  const totalEl = document.getElementById('cartTotal');
  if (totalEl) {
    totalEl.textContent = formatearPrecio(totalGeneral);
  }

  const btnContinuar = document.getElementById('btnContinuar');
  if (btnContinuar) {
    btnContinuar.disabled = true;
    btnContinuar.textContent = 'Próximamente';
  }

  actualizarZonasProductos();
}

// Cargar restaurantes reales
async function cargarRestaurantes() {
  const listEl = document.getElementById('restaurantsList');
  const msgEl = document.getElementById('restaurantsMsg');
  if (!listEl) return;
  try {
    if (msgEl) msgEl.textContent = 'Cargando restaurantes...';
    const resp = typeof apiFetch === 'function' ? await apiFetch(`${API_BASE}/api/restaurantes`) : await fetch(`${API_BASE}/api/restaurantes`, { headers: { 'Authorization': `Bearer ${tokenDash}` } });
    if (!resp.ok) {
      if (msgEl) {
        msgEl.style.color = 'red';
        msgEl.textContent = `Error al cargar restaurantes (HTTP ${resp.status})`;
      }
      return;
    }
    const data = await resp.json();
    listEl.innerHTML = '';
    if (msgEl) msgEl.textContent = '';
    if (!Array.isArray(data) || data.length === 0) {
      if (msgEl) {
        msgEl.style.color = '#666';
        msgEl.textContent = 'No hay restaurantes registrados.';
      }
      return;
    }
    data.forEach(rest => {
      const card = document.createElement('div');
      card.className = 'restaurant-card';
      card.setAttribute('data-id-restaurante', rest.idRestaurante || '');
      const nombreRest = rest.nombre || 'Restaurante';
      if (rest.idRestaurante != null) restaurantesPorId.set(String(rest.idRestaurante), nombreRest);
      const imgAsignada = obtenerImagenRestaurante(nombreRest);
      const imgSrc = imgAsignada || 'https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=120&h=100&fit=crop';
      card.innerHTML = `
        <img src="${imgSrc}" alt="${nombreRest}">
        <div class="restaurant-info">
          <div class="restaurant-header">
            <strong>${nombreRest}</strong>
            <span class="rating"><i class="fa-solid fa-star"></i> ${rest.calificacion ?? 'N/A'}</span>
          </div>
          <p class="restaurant-desc">${rest.categoria || ''} ${rest.ciudad ? '- ' + rest.ciudad : ''}</p>
          <div class="restaurant-footer">
            <span class="time">${rest.estado ? 'ABIERTO' : 'CERRADO'}</span>
            <span class="price">${rest.telefono || ''}</span>
          </div>
        </div>
      `;
      listEl.appendChild(card);
    });
    renderCarritoUI();
  } catch (err) {
    if (msgEl) {
      msgEl.style.color = 'red';
      msgEl.textContent = 'No se pudo conectar con el servidor.';
    }
  }
}

// Cargar productos de un restaurante
async function cargarProductos(idRestaurante, nombreRestaurante) {
  const productsSection = document.getElementById('productsSection');
  const restaurantsSection = document.getElementById('restaurantsSection');
  const productsList = document.getElementById('productsList');
  const productsMsg = document.getElementById('productsMsg');
  const productsTitle = document.getElementById('productsTitle');

  idRestauranteActual = idRestaurante;
  productosVisibles = new Map();

  if (restaurantsSection) restaurantsSection.style.display = 'none';
  if (productsSection) productsSection.style.display = 'block';

  if (productsTitle) productsTitle.textContent = nombreRestaurante ? `Productos - ${nombreRestaurante}` : 'Productos';
  if (productsList) productsList.innerHTML = '';
  if (productsMsg) {
    productsMsg.style.color = '#666';
    productsMsg.textContent = 'Cargando productos...';
  }

  try {
    const url = `${API_BASE}/api/productos/restaurante/${idRestaurante}`;
    const resp = typeof apiFetch === 'function' ? await apiFetch(url) : await fetch(url, { headers: { 'Authorization': `Bearer ${localStorage.getItem('emidomi_token')}` } });
    if (!resp.ok) {
      if (productsMsg) {
        productsMsg.style.color = 'red';
        productsMsg.textContent = `Error al cargar productos (HTTP ${resp.status})`;
      }
      return;
    }
    const data = await resp.json();
    if (productsMsg) productsMsg.textContent = '';
    if (!Array.isArray(data) || data.length === 0) {
      if (productsMsg) {
        productsMsg.style.color = '#666';
        productsMsg.textContent = 'Este restaurante no tiene productos registrados.';
      }
      return;
    }
    data.forEach(prod => productosVisibles.set(String(prod.idProducto), prod));
    renderProductosAgrupados(data);
    renderCarritoUI();
  } catch (err) {
    if (productsMsg) {
      productsMsg.style.color = 'red';
      productsMsg.textContent = 'No se pudo conectar con el servidor.';
    }
  }
}

function ordenCategoria(categoria) {
  const indice = ORDEN_CATEGORIAS.indexOf(normalizarNombre(categoria));
  return indice === -1 ? ORDEN_CATEGORIAS.length : indice;
}

function crearCardProducto(prod) {
  const disponible = prod.estado === true || prod.estado === 1 || prod.disponible === true || prod.disponible === 1;
  const nombreProd = prod.nombre || 'Producto';
  const emojiProd = prod.emoji && String(prod.emoji).trim() !== '' ? String(prod.emoji).trim() : '🍽️';
  const imagenProd = prod.imagen && String(prod.imagen).trim() !== '' ? String(prod.imagen).trim() : null;
  prod.nombreRestaurante = prod.nombreRestaurante || (idRestauranteActual != null ? (restaurantesPorId.get(String(idRestauranteActual)) || '') : '');

  const card = document.createElement('article');
  card.className = 'product-card' + (disponible ? '' : ' no-disponible');
  card.setAttribute('data-id-producto', String(prod.idProducto));

  if (imagenProd) {
    const img = document.createElement('img');
    img.className = 'product-img';
    img.src = imagenProd;
    img.alt = nombreProd;
    img.onerror = () => {
      const reemplazo = document.createElement('div');
      reemplazo.className = 'product-img';
      reemplazo.textContent = emojiProd;
      img.replaceWith(reemplazo);
    };
    card.appendChild(img);
  } else {
    const emojiBox = document.createElement('div');
    emojiBox.className = 'product-img';
    emojiBox.textContent = emojiProd;
    card.appendChild(emojiBox);
  }

  const info = document.createElement('div');
  info.className = 'product-info';

  const superior = document.createElement('div');
  const nombre = document.createElement('div');
  nombre.className = 'product-name';
  nombre.textContent = nombreProd;
  const desc = document.createElement('div');
  desc.className = 'product-desc';
  desc.textContent = prod.descripcion || '';
  superior.appendChild(nombre);
  superior.appendChild(desc);

  const footer = document.createElement('div');
  footer.className = 'product-footer';
  const precio = document.createElement('span');
  precio.className = 'product-price';
  precio.textContent = formatearPrecio(prod.precio);
  const estado = document.createElement('span');
  estado.className = 'product-status ' + (disponible ? 'disponible' : 'nodisponible');
  estado.textContent = disponible ? 'Disponible' : 'No disponible';
  footer.appendChild(precio);
  footer.appendChild(estado);

  info.appendChild(superior);
  info.appendChild(footer);

  if (disponible) {
    const accion = document.createElement('div');
    accion.className = 'product-action';
    info.appendChild(accion);
  }

  card.appendChild(info);
  return card;
}

function renderProductosAgrupados(productos) {
  const contenedor = document.getElementById('productsList');
  if (!contenedor) return;
  contenedor.innerHTML = '';

  const grupos = new Map();
  productos.forEach((prod, indice) => {
    const categoria = (prod.categoria || '').toString().trim() || 'Otros';
    const clave = normalizarNombre(categoria) || 'otros';
    if (!grupos.has(clave)) {
      grupos.set(clave, { titulo: categoria, orden: ordenCategoria(categoria), indice, items: [] });
    }
    grupos.get(clave).items.push(prod);
  });

  const ordenados = Array.from(grupos.values()).sort((a, b) => a.orden - b.orden || a.indice - b.indice);

  ordenados.forEach(grupo => {
    const seccion = document.createElement('section');
    seccion.className = 'product-group';
    const titulo = document.createElement('h3');
    titulo.className = 'product-group-title';
    titulo.textContent = grupo.titulo;
    const grid = document.createElement('div');
    grid.className = 'product-group-grid';
    grupo.items.forEach(prod => grid.appendChild(crearCardProducto(prod)));
    seccion.appendChild(titulo);
    seccion.appendChild(grid);
    contenedor.appendChild(seccion);
  });

  actualizarZonasProductos();
}

// Delegación de eventos para ir a productos
const restaurantsListEl = document.getElementById('restaurantsList');
if (restaurantsListEl) {
  restaurantsListEl.addEventListener('click', (e) => {
    const card = e.target.closest('.restaurant-card');
    if (!card) return;
    const idRest = card.getAttribute('data-id-restaurante');
    const nombreRest = card.querySelector('strong')?.textContent || '';
    if (idRest) {
      cargarProductos(idRest, nombreRest);
    }
  });
}

// Botón volver
const btnBack = document.getElementById('btnBackToRestaurants');
if (btnBack) {
  btnBack.addEventListener('click', () => {
    const productsSection = document.getElementById('productsSection');
    const restaurantsSection = document.getElementById('restaurantsSection');
    if (productsSection) productsSection.style.display = 'none';
    if (restaurantsSection) restaurantsSection.style.display = 'block';
    const productsList = document.getElementById('productsList');
    if (productsList) productsList.innerHTML = '';
    const productsMsg = document.getElementById('productsMsg');
    if (productsMsg) productsMsg.textContent = '';
    productosVisibles = new Map();
    idRestauranteActual = null;
  });
}

// Delegación para agregar o quitar unidades en las tarjetas de productos
const productsListEl = document.getElementById('productsList');
if (productsListEl) {
  productsListEl.addEventListener('click', (e) => {
    const boton = e.target.closest('[data-action]');
    if (!boton) return;
    const card = boton.closest('.product-card');
    if (!card) return;
    const idProducto = card.getAttribute('data-id-producto');
    const accion = boton.getAttribute('data-action');
    if (accion === 'add') {
      const prod = productosVisibles.get(String(idProducto));
      if (prod) agregarProducto(Object.assign({}, prod, { idRestaurante: prod.idRestaurante != null ? prod.idRestaurante : idRestauranteActual }));
    } else if (accion === 'inc') {
      cambiarCantidad(idProducto, 1);
    } else if (accion === 'dec') {
      cambiarCantidad(idProducto, -1);
    }
  });
}

// Carrito: abrir/cerrar y delegación de +/− dentro del panel
const cartIconEl = document.getElementById('cartIcon');
const cartPanelEl = document.getElementById('cartPanel');
const cartOverlayEl = document.getElementById('cartOverlay');
const cartCloseEl = document.getElementById('cartClose');

function abrirCarrito() {
  renderCarritoUI();
  if (cartPanelEl) cartPanelEl.hidden = false;
  if (cartOverlayEl) cartOverlayEl.hidden = false;
}

function cerrarCarrito() {
  if (cartPanelEl) cartPanelEl.hidden = true;
  if (cartOverlayEl) cartOverlayEl.hidden = true;
}

if (cartIconEl) {
  cartIconEl.addEventListener('click', abrirCarrito);
  cartIconEl.addEventListener('keydown', (e) => {
    if (e.key === 'Enter' || e.key === ' ') {
      e.preventDefault();
      abrirCarrito();
    }
  });
}
if (cartCloseEl) cartCloseEl.addEventListener('click', cerrarCarrito);
if (cartOverlayEl) cartOverlayEl.addEventListener('click', cerrarCarrito);

if (cartPanelEl) {
  cartPanelEl.addEventListener('click', (e) => {
    const boton = e.target.closest('[data-cart-action]');
    if (!boton) return;
    const item = boton.closest('[data-id-producto]');
    if (!item) return;
    const accion = boton.getAttribute('data-cart-action');
    cambiarCantidad(item.getAttribute('data-id-producto'), accion === 'inc' ? 1 : -1);
  });
}

document.addEventListener('keydown', (e) => {
  if (e.key === 'Escape') cerrarCarrito();
});

renderCarritoUI();

// ── Filtros (TODOS / ABIERTOS / CERRADOS) ──
const filterBtns = document.querySelectorAll('.filter-btn');
filterBtns.forEach(btn => {
  btn.addEventListener('click', () => {
    filterBtns.forEach(b => b.classList.remove('active'));
    btn.classList.add('active');
  });
});

// ── Categorías ──
const categoryItems = document.querySelectorAll('.category-item');
categoryItems.forEach(item => {
  item.addEventListener('click', () => {
    categoryItems.forEach(c => c.classList.remove('active'));
    item.classList.add('active');
  });
});

document.addEventListener('DOMContentLoaded', cargarRestaurantes);
