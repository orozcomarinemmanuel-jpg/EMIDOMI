const tokenPerfil = localStorage.getItem('emidomi_token');
if (!tokenPerfil) {
  window.location.href = 'index.html';
}

async function cargarPerfil() {
  const msgEl = document.getElementById('perfilMsg');
  const idCliente = localStorage.getItem('emidomi_cliente_id');
  try {
    if (msgEl) msgEl.textContent = 'Cargando perfil...';
    let cliente = null;
    if (idCliente && typeof apiFetch === 'function') {
      const resp = await apiFetch(`${API_BASE}/api/clientes/${idCliente}`);
      if (resp.ok) {
        cliente = await resp.json();
      }
    }
    if (!cliente && typeof apiFetch === 'function') {
      const resp = await apiFetch(`${API_BASE}/api/clientes`);
      if (resp.ok) {
        const lista = await resp.json();
        if (Array.isArray(lista) && lista.length > 0) {
          cliente = lista[0];
        }
      }
    }
    if (cliente) {
      const nombre = `${cliente.nombre || ''} ${cliente.apellido || ''}`.trim();
      const pn = document.getElementById('perfilNombre');
      if (pn) pn.textContent = nombre || 'Sin nombre';
      const pc = document.getElementById('perfilCorreo');
      if (pc) pc.textContent = cliente.correo || '-';
      const pc2 = document.getElementById('perfilCorreo2');
      if (pc2) pc2.textContent = cliente.correo || '-';
      const pt = document.getElementById('perfilTelefono');
      if (pt) pt.textContent = cliente.telefono || '-';
      const pd = document.getElementById('perfilDireccion');
      if (pd) pd.textContent = cliente.direccion || '-';
      const pci = document.getElementById('perfilCiudad');
      if (pci) pci.textContent = cliente.ciudad || '-';
    }
    if (msgEl) msgEl.textContent = '';
  } catch (err) {
    if (msgEl) {
      msgEl.style.color = 'red';
      msgEl.textContent = 'No se pudo cargar el perfil.';
    }
  }
}

const btnEdit = document.querySelector('.btn-edit');
if (btnEdit) {
  btnEdit.addEventListener('click', () => {
    alert('Funcionalidad de edición pendiente. Puedes editar desde el backend o implementar PUT /api/clientes/{id}.');
  });
}

const btnCerrar = document.getElementById('btnCerrarSesion');
if (btnCerrar) {
  btnCerrar.addEventListener('click', () => {
    localStorage.removeItem('emidomi_token');
    localStorage.removeItem('emidomi_cliente_id');
    window.location.href = 'index.html';
  });
}

document.addEventListener('DOMContentLoaded', cargarPerfil);

// ── Navegación de opciones ──
const opciones = document.querySelectorAll('.opcion-item');
opciones.forEach(item => {
  item.addEventListener('click', () => {
    const texto = item.querySelector('span')?.textContent || '';
    console.log(`Navegando a: ${texto}`);
  });
});
