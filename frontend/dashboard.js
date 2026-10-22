// Verificar sesión
const tokenDash = localStorage.getItem('emidomi_token');
if (!tokenDash) {
  window.location.href = 'index.html';
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
      card.innerHTML = `
        <img src="https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=120&h=100&fit=crop" alt="${rest.nombre || 'Restaurante'}">
        <div class="restaurant-info">
          <div class="restaurant-header">
            <strong>${rest.nombre || ''}</strong>
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
