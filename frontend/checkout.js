// Helpers compartidos por finalizar.html y pedidos.html.
// No declara API_BASE ni apiFetch: esos vienen de script.js.

const CARRITO_KEY_CHECKOUT = 'emidomi_carrito';

// Solo referencia visual. El valor real del domicilio lo calcula y cobra el backend.
const DOMICILIO_REFERENCIA = 5000;

const ETIQUETAS_METODO_PAGO = {
  efectivo: 'Efectivo',
  tarjeta_credito: 'Tarjeta de crédito',
  tarjeta_debito: 'Tarjeta débito',
  transferencia: 'Transferencia',
  nequi: 'Nequi',
  daviplata: 'Daviplata'
};

const ETIQUETAS_ESTADO_PAGO = {
  pendiente: 'Pendiente',
  aprobado: 'Aprobado',
  rechazado: 'Rechazado',
  reembolsado: 'Reembolsado'
};

const ETIQUETAS_ESTADO_PEDIDO = {
  PENDIENTE: 'Pendiente',
  CONFIRMADO: 'Confirmado',
  EN_PREPARACION: 'En preparación',
  EN_CAMINO: 'En camino',
  ENTREGADO: 'Entregado',
  CANCELADO: 'Cancelado'
};

function formatearPesos(valor) {
  const numero = Number(valor);
  if (!isFinite(numero)) return '$ 0';
  return '$ ' + numero.toLocaleString('es-CO', { maximumFractionDigits: 0 });
}

function escaparHtml(texto) {
  return String(texto ?? '').replace(/[&<>"']/g, c => ({
    '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'
  }[c]));
}

function leerCarritoGuardado() {
  try {
    const datos = JSON.parse(localStorage.getItem(CARRITO_KEY_CHECKOUT) || '[]');
    if (!Array.isArray(datos)) return [];
    return datos.filter(item => item && item.idProducto != null && Number(item.cantidad) > 0);
  } catch (err) {
    return [];
  }
}

function agruparPorRestaurante(items) {
  const grupos = new Map();
  items.forEach(item => {
    const clave = item.idRestaurante != null ? String(item.idRestaurante) : 'sin-restaurante';
    if (!grupos.has(clave)) {
      grupos.set(clave, {
        idRestaurante: item.idRestaurante,
        nombre: item.nombreRestaurante
          || (item.idRestaurante != null ? `Restaurante #${item.idRestaurante}` : 'Sin restaurante'),
        items: []
      });
    }
    grupos.get(clave).items.push(item);
  });
  return Array.from(grupos.values());
}

// fechaPedido llega como timestamp (número) o como texto ISO; new Date acepta ambos.
function formatearFecha(valor) {
  if (valor == null || valor === '') return '-';
  const fecha = new Date(valor);
  return isNaN(fecha) ? String(valor) : fecha.toLocaleString('es-CO', { dateStyle: 'medium', timeStyle: 'short' });
}
