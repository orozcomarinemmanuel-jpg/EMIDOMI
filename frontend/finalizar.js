// Pantalla Finalizar pedido: envía el carrito a POST /api/pedidos/confirmar.
// El navegador solo manda idProducto y cantidad; el servidor calcula precios y totales.

const tokenFinalizar = localStorage.getItem('emidomi_token');
if (!tokenFinalizar) {
  window.location.href = 'index.html';
}

const formPedido = document.getElementById('formPedido');
const btnConfirmar = document.getElementById('btnConfirmar');
const finalizarMsg = document.getElementById('finalizarMsg');
let confirmando = false; // evita envíos dobles mientras hay una petición en curso

function mostrarMensaje(texto, esError = true) {
  finalizarMsg.style.color = esError ? '#dc3545' : '#28a745';
  finalizarMsg.textContent = texto;
}

function renderResumen(carrito) {
  const contenedor = document.getElementById('resumenGrupos');
  contenedor.innerHTML = '';
  let totalGeneral = 0;

  agruparPorRestaurante(carrito).forEach(grupo => {
    let subtotal = 0;
    const lineas = grupo.items.map(item => {
      const linea = (Number(item.precio) || 0) * item.cantidad;
      subtotal += linea;
      return `<li><span>${escaparHtml(item.cantidad)} × ${escaparHtml(item.nombre)}</span><span>${formatearPesos(linea)}</span></li>`;
    }).join('');
    const totalRestaurante = subtotal + DOMICILIO_REFERENCIA;
    totalGeneral += totalRestaurante;

    contenedor.insertAdjacentHTML('beforeend', `
      <section class="checkout-grupo">
        <h3>${escaparHtml(grupo.nombre)}</h3>
        <ul class="checkout-lineas">${lineas}</ul>
        <div class="checkout-linea"><span>Subtotal</span><span>${formatearPesos(subtotal)}</span></div>
        <div class="checkout-linea"><span>Domicilio</span><span>${formatearPesos(DOMICILIO_REFERENCIA)}</span></div>
        <div class="checkout-linea checkout-linea-total"><span>Total restaurante</span><span>${formatearPesos(totalRestaurante)}</span></div>
      </section>`);
  });

  document.getElementById('totalGeneral').textContent = formatearPesos(totalGeneral);
}

async function precargarCliente() {
  const idCliente = localStorage.getItem('emidomi_cliente_id');
  if (!idCliente) return;
  try {
    const resp = await apiFetch(`${API_BASE}/api/clientes/${idCliente}`);
    if (!resp.ok) return;
    const cliente = await resp.json();
    const direccion = document.getElementById('direccion');
    const ciudad = document.getElementById('ciudad');
    if (!direccion.value) direccion.value = cliente.direccion || '';
    if (!ciudad.value) ciudad.value = cliente.ciudad || '';
  } catch (err) {
    // Si falla, el cliente escribe la dirección a mano.
  }
}

function construirPeticion(carrito) {
  const grupos = agruparPorRestaurante(carrito).map(grupo => ({
    idRestaurante: grupo.idRestaurante,
    items: grupo.items.map(item => ({ idProducto: item.idProducto, cantidad: item.cantidad }))
  }));
  return {
    direccionEntrega: document.getElementById('direccion').value.trim(),
    ciudadEntrega: document.getElementById('ciudad').value.trim(),
    notas: document.getElementById('notas').value.trim(),
    metodoPago: document.getElementById('metodoPago').value,
    grupos
  };
}

function mostrarConfirmacion(datos) {
  document.getElementById('formSection').hidden = true;

  const lista = document.getElementById('confirmacionLista');
  lista.innerHTML = datos.pedidos.map(p => {
    const metodo = ETIQUETAS_METODO_PAGO[p.pago.metodoPago] || p.pago.metodoPago;
    const estadoPago = ETIQUETAS_ESTADO_PAGO[p.pago.estadoPago] || p.pago.estadoPago;
    return `
      <article class="checkout-grupo">
        <h3>Pedido #${escaparHtml(p.idPedido)} · ${escaparHtml(p.nombreRestaurante)}</h3>
        <p>Total: <strong>${formatearPesos(p.total)}</strong> (incluye domicilio ${formatearPesos(p.costoDomicilio)})</p>
        <p>Pago: ${escaparHtml(metodo)} · ${escaparHtml(estadoPago)}</p>
        <p class="checkout-hint">Referencia: ${escaparHtml(p.pago.referencia)}</p>
      </article>`;
  }).join('');

  document.getElementById('confirmacionTotal').textContent = formatearPesos(datos.totalGeneral);
  document.getElementById('confirmacion').hidden = false;
  window.scrollTo(0, 0);
}

formPedido.addEventListener('submit', async (e) => {
  e.preventDefault();
  if (confirmando) return;

  const carrito = leerCarritoGuardado();
  if (carrito.length === 0) {
    mostrarMensaje('Tu carrito está vacío.');
    return;
  }
  if (carrito.some(item => item.idRestaurante == null)) {
    mostrarMensaje('Hay productos sin restaurante en tu carrito. Vacíalo y vuelve a agregarlos.');
    return;
  }
  if (!document.getElementById('direccion').value.trim()) {
    mostrarMensaje('Escribe la dirección de entrega.');
    return;
  }
  if (!document.getElementById('metodoPago').value) {
    mostrarMensaje('Elige un método de pago.');
    return;
  }

  confirmando = true;
  btnConfirmar.disabled = true;
  btnConfirmar.textContent = 'Confirmando...';
  mostrarMensaje('', false);

  let exito = false;
  try {
    const resp = await apiFetch(`${API_BASE}/api/pedidos/confirmar`, {
      method: 'POST',
      body: JSON.stringify(construirPeticion(carrito))
    });

    if (resp.status === 201) {
      const datos = await resp.json();
      exito = true;
      localStorage.removeItem('emidomi_carrito'); // solo se vacía cuando el servidor confirmó
      mostrarConfirmacion(datos);
      return;
    }

    // El backend responde 400 con un mensaje claro en texto (p. ej. "X está cerrado").
    const texto = await resp.text();
    mostrarMensaje(texto || `No se pudo confirmar el pedido (HTTP ${resp.status}). Tu carrito sigue guardado.`);
  } catch (err) {
    mostrarMensaje('No se pudo conectar con el servidor. Tu carrito sigue guardado.');
  } finally {
    if (!exito) {
      confirmando = false;
      btnConfirmar.disabled = false;
      btnConfirmar.textContent = 'Confirmar pedido';
    }
  }
});

(function iniciar() {
  const carrito = leerCarritoGuardado();
  if (carrito.length === 0) {
    document.getElementById('resumenGrupos').innerHTML = '<p>Tu carrito está vacío.</p>';
    btnConfirmar.disabled = true;
    return;
  }
  renderResumen(carrito);
  precargarCliente();
})();
