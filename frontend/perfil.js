// Pantalla de perfil: ver y editar los datos del cliente con PUT /api/clientes/{id}.
// El token y el id los guardó el login en localStorage. El backend valida todo:
// el id de la ruta debe coincidir con el del token, si no responde 403.

const tokenPerfil = localStorage.getItem('emidomi_token');
if (!tokenPerfil) {
  window.location.href = 'index.html';
}

// Campos editables (la contraseña NO está aquí: no se modifica en esta pantalla)
const CAMPOS_PERFIL = ['nombre', 'apellido', 'correo', 'telefono', 'direccion', 'ciudad'];

let clienteActual = null; // último cliente recibido del backend (base para Cancelar y para detectar cambios)
let guardando = false;

const contenedorPerfil = document.getElementById('perfilContainer');
const btnEditar = document.getElementById('btnEditar');
const btnGuardar = document.getElementById('btnGuardar');
const btnCancelar = document.getElementById('btnCancelar');
const perfilMsg = document.getElementById('perfilMsg');

// El id se valida antes de usarlo: solo dígitos.
function idClienteGuardado() {
  const id = localStorage.getItem('emidomi_cliente_id');
  return id && /^\d+$/.test(id) ? id : null;
}

function mostrarMensaje(texto, esExito = false) {
  perfilMsg.style.color = esExito ? '#28a745' : '#dc3545';
  perfilMsg.textContent = texto;
}

// Pinta los datos en modo lectura y deja los campos de edición con los mismos valores.
function pintarDatos(cliente) {
  const nombreCompleto = `${cliente.nombre || ''} ${cliente.apellido || ''}`.trim();
  document.getElementById('perfilNombre').textContent = nombreCompleto || 'Sin nombre';
  document.getElementById('perfilCorreo').textContent = cliente.correo || '-';

  CAMPOS_PERFIL.forEach(campo => {
    const valor = cliente[campo] || '';
    document.getElementById('valor' + capitalizar(campo)).textContent = valor || '-';
    document.getElementById('input' + capitalizar(campo)).value = valor;
  });
}

function capitalizar(texto) {
  return texto.charAt(0).toUpperCase() + texto.slice(1);
}

function entrarModoEdicion() {
  if (!clienteActual) return; // no editar mientras no haya datos cargados
  // Al entrar, los campos vuelven a los últimos datos guardados
  pintarDatos(clienteActual);
  contenedorPerfil.classList.add('editando');
  document.getElementById('inputNombre').focus();
}

function salirModoEdicion() {
  contenedorPerfil.classList.remove('editando');
  mostrarMensaje('');
}

// Valida los campos antes de enviar. Devuelve el objeto a enviar o null si hay error.
function leerFormulario() {
  const datos = {};
  for (const campo of CAMPOS_PERFIL) {
    const valor = document.getElementById('input' + capitalizar(campo)).value.trim();
    if (!valor) {
      mostrarMensaje(`El campo ${campo} no puede estar vacío.`);
      return null;
    }
    datos[campo] = valor;
  }
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRegex.test(datos.correo)) {
    mostrarMensaje('Ingresa un correo válido.');
    return null;
  }
  return datos;
}

async function cargarPerfil() {
  const id = idClienteGuardado();
  if (!id) {
    mostrarMensaje('Tu sesión no tiene un cliente válido. Vuelve a iniciar sesión.');
    btnEditar.disabled = true;
    return;
  }
  try {
    mostrarMensaje('Cargando perfil...', true);
    const resp = await apiFetch(`${API_BASE}/api/clientes/${id}`);
    if (!resp.ok) {
      mostrarMensaje(await resp.text() || `No se pudo cargar el perfil (HTTP ${resp.status}).`);
      return;
    }
    clienteActual = await resp.json();
    pintarDatos(clienteActual);
    btnEditar.disabled = false;
    mostrarMensaje('');
  } catch (err) {
    mostrarMensaje('No se pudo conectar con el servidor.');
  }
}

async function guardarCambios() {
  if (guardando || !clienteActual) return;
  const datos = leerFormulario();
  if (!datos) return;

  const id = idClienteGuardado();
  // Comparación sin importar mayúsculas: "Ana@x.com" y "ana@x.com" son el mismo correo
  const correoCambio = datos.correo.toLowerCase() !== String(clienteActual.correo || '').toLowerCase();

  guardando = true;
  btnGuardar.disabled = true;
  mostrarMensaje('Guardando...', true);
  try {
    const resp = await apiFetch(`${API_BASE}/api/clientes/${id}`, {
      method: 'PUT',
      body: JSON.stringify(datos)
    });

    if (!resp.ok) {
      // Mensaje real del backend (p. ej. 403 "Solo puedes editar tus propios datos.")
      mostrarMensaje(await resp.text() || `No se pudo guardar (HTTP ${resp.status}).`);
      return;
    }

    if (correoCambio) {
      // El correo es el usuario de login: cerramos sesión y pedimos entrar de nuevo.
      localStorage.removeItem('emidomi_token');
      localStorage.removeItem('emidomi_cliente_id');
      sessionStorage.setItem('emidomi_aviso',
        'Cambiaste tu correo. Inicia sesión de nuevo con tu nuevo correo electrónico.');
      window.location.href = 'index.html';
      return;
    }

    clienteActual = await resp.json();
    pintarDatos(clienteActual);
    salirModoEdicion();
    mostrarMensaje('Datos guardados correctamente.', true);
  } catch (err) {
    mostrarMensaje('No se pudo conectar con el servidor. Tus cambios no se guardaron.');
  } finally {
    guardando = false;
    btnGuardar.disabled = false;
  }
}

btnEditar.addEventListener('click', entrarModoEdicion);
btnCancelar.addEventListener('click', () => {
  if (clienteActual) pintarDatos(clienteActual);
  salirModoEdicion();
});
btnGuardar.addEventListener('click', guardarCambios);

const btnCerrar = document.getElementById('btnCerrarSesion');
if (btnCerrar) {
  btnCerrar.addEventListener('click', () => {
    localStorage.removeItem('emidomi_token');
    localStorage.removeItem('emidomi_cliente_id');
    window.location.href = 'index.html';
  });
}

document.addEventListener('DOMContentLoaded', cargarPerfil);
