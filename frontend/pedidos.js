// Pantalla Mis pedidos: lista GET /api/pedidos/mios (solo pedidos del token).
// Se refresca cada 20 s mientras la pestaña esté visible.

const tokenPedidos = localStorage.getItem('emidomi_token');
if (!tokenPedidos) {
  window.location.href = 'index.html';
}

const INTERVALO_REFRESCO_MS = 20000;
let temporizadorRefresco = null; // único temporizador: nunca se crean dos
let cargandoPedidos = false;
let ultimoContenido = null;      // para no repintar si nada cambió

const pedidosMsg = document.getElementById('pedidosMsg');
const listaPedidos = document.getElementById('listaPedidos');

function mostrarMensajePedidos(texto, esError = false) {
  pedidosMsg.style.color = esError ? '#dc3545' : '#666';
  pedidosMsg.textContent = texto;
}

function renderPedidos(pedidos) {
  if (!Array.isArray(pedidos) || pedidos.length === 0) {
    listaPedidos.innerHTML = '<p class="pedidos-vacio">Aún no tienes pedidos.</p>';
    return;
  }

  listaPedidos.innerHTML = pedidos.map(p => {
    const estado = p.estadoPedido || '';
    const etiquetaEstado = ETIQUETAS_ESTADO_PEDIDO[estado] || estado || 'Sin estado';

    const productos = (p.productos || []).length > 0
      ? p.productos.map(prod => `
          <li><span>${escaparHtml(prod.cantidad)} × ${escaparHtml(prod.nombre)}</span>
          <span>${formatearPesos(prod.subtotal)}</span></li>`).join('')
      : '<li><span>Sin productos registrados</span></li>';

    const pago = p.pago;
    const pagoHtml = pago
      ? `<p class="pedido-pago">Pago: ${escaparHtml(ETIQUETAS_METODO_PAGO[pago.metodoPago] || pago.metodoPago || '-')}
           · <span class="pago-badge pago-${escaparHtml(pago.estadoPago)}">${escaparHtml(ETIQUETAS_ESTADO_PAGO[pago.estadoPago] || pago.estadoPago || '-')}</span>
           <span class="checkout-hint">Ref. ${escaparHtml(pago.referencia || '-')}</span></p>`
      : '<p class="pedido-pago">Pago: sin registro</p>';

    const entrega = `${escaparHtml(p.direccionEntrega || '-')}${p.ciudadEntrega ? ', ' + escaparHtml(p.ciudadEntrega) : ''}`;

    return `
      <article class="pedido-card">
        <div class="pedido-cabecera">
          <div>
            <div class="pedido-titulo">Pedido #${escaparHtml(p.idPedido)} · ${escaparHtml(p.nombreRestaurante)}</div>
            <div class="pedido-fecha">${formatearFecha(p.fechaPedido)}</div>
          </div>
          <span class="pedido-badge estado-${escaparHtml(estado)}">${escaparHtml(etiquetaEstado)}</span>
        </div>
        <ul class="pedido-productos">${productos}</ul>
        <div class="pedido-resumen">
          <span>Subtotal <strong>${formatearPesos(p.subtotal)}</strong></span>
          <span>Domicilio <strong>${formatearPesos(p.costoDomicilio)}</strong></span>
          <span>Total <strong>${formatearPesos(p.total)}</strong></span>
          <span>Entrega <strong>${entrega}</strong></span>
        </div>
        ${p.notas ? `<p class="checkout-hint">Notas: ${escaparHtml(p.notas)}</p>` : ''}
        ${pagoHtml}
      </article>`;
  }).join('');
}

async function cargarPedidos() {
  if (cargandoPedidos) return;
  cargandoPedidos = true;
  try {
    const resp = await apiFetch(`${API_BASE}/api/pedidos/mios`);
    if (!resp.ok) {
      mostrarMensajePedidos(`No se pudieron cargar tus pedidos (HTTP ${resp.status}).`, true);
      return;
    }
    const texto = await resp.text();
    if (texto !== ultimoContenido) {
      ultimoContenido = texto;
      renderPedidos(JSON.parse(texto));
    }
    mostrarMensajePedidos('');
    document.getElementById('ultimaActualizacion').textContent =
      'Actualizado ' + new Date().toLocaleTimeString('es-CO');
  } catch (err) {
    mostrarMensajePedidos('No se pudo conectar con el servidor. Se reintentará automáticamente.', true);
  } finally {
    cargandoPedidos = false;
  }
}

function iniciarRefresco() {
  if (temporizadorRefresco !== null) return;
  temporizadorRefresco = setInterval(cargarPedidos, INTERVALO_REFRESCO_MS);
}

function detenerRefresco() {
  if (temporizadorRefresco === null) return;
  clearInterval(temporizadorRefresco);
  temporizadorRefresco = null;
}

// Pausa el refresco cuando la pestaña se oculta y lo reanuda al volver.
document.addEventListener('visibilitychange', () => {
  if (document.hidden) {
    detenerRefresco();
  } else {
    cargarPedidos();
    iniciarRefresco();
  }
});

document.getElementById('btnActualizar').addEventListener('click', cargarPedidos);

cargarPedidos();
iniciarRefresco();
