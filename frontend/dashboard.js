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
    data.forEach(prod => {
      const card = document.createElement('div');
      card.className = 'product-card';
      const nombreProd = prod.nombre || 'Producto';
      const descProd = prod.descripcion || '';
      const precioProd = prod.precio != null ? `$ ${prod.precio}` : '$ 0';
      const disponible = prod.estado === true;
      const imgProd = prod.imagen && prod.imagen.trim() !== '' ? prod.imagen : null;
      const emojiProd = prod.emoji && prod.emoji.trim() !== '' ? prod.emoji : '🍽️';

      let imgHtml = '';
      if (imgProd) {
        imgHtml = `<img class="product-img" src="${imgProd}" alt="${nombreProd}" onerror="this.outerHTML='<div class=\\'product-img\\'>${emojiProd}</div>'">`;
      } else {
        imgHtml = `<div class="product-img">${emojiProd}</div>`;
      }

      card.innerHTML = `
        ${imgHtml}
        <div class="product-info">
          <div>
            <div class="product-name">${nombreProd}</div>
            <div class="product-desc">${descProd}</div>
          </div>
          <div class="product-footer">
            <span class="product-price">${precioProd}</span>
            <span class="product-status ${disponible ? 'disponible' : 'nodisponible'}">${disponible ? 'Disponible' : 'No disponible'}</span>
          </div>
        </div>
      `;
      const productsList = document.getElementById('productsList');
      if (productsList) productsList.appendChild(card);
    });
  } catch (err) {
    if (productsMsg) {
      productsMsg.style.color = 'red';
      productsMsg.textContent = 'No se pudo conectar con el servidor.';
    }
  }
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
  });
}

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
