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

// ── Foto de perfil (F2) ──
const FOTO_DEFECTO = 'img/avatar-default.svg';   // imagen local si el cliente no tiene foto
const TAMANO_MAX_FOTO = 5 * 1024 * 1024;          // 5 MB; el backend también lo valida
let archivoFotoPendiente = null;                  // imagen elegida, todavía no enviada
let urlVistaPrevia = null;                        // URL temporal para la vista previa
let subiendoFoto = false;

const contenedorPerfil = document.getElementById('perfilContainer');
const btnEditar = document.getElementById('btnEditar');
const btnGuardar = document.getElementById('btnGuardar');
const btnCancelar = document.getElementById('btnCancelar');
const perfilMsg = document.getElementById('perfilMsg');
const avatarWrapper = document.getElementById('avatarWrapper');
const inputFoto = document.getElementById('inputFoto');
const fotoPerfil = document.getElementById('fotoPerfil');
const fotoPreviewBox = document.getElementById('fotoPreviewBox');
const fotoPreview = document.getElementById('fotoPreview');
const btnGuardarFoto = document.getElementById('btnGuardarFoto');
const btnCancelarFoto = document.getElementById('btnCancelarFoto');

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
    mostrarFoto(clienteActual.fotoUrl);
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

// ── Foto de perfil (F2) ──

// Muestra la foto guardada desde el backend, o la imagen por defecto si no hay.
// Si la URL falla al cargar, también cae a la imagen por defecto (nunca una imagen rota).
// El parámetro ?v= evita que el navegador muestre la foto anterior en caché tras subir una nueva.
function mostrarFoto(fotoUrl, recargarCache = false) {
  fotoPerfil.onerror = () => {
    fotoPerfil.onerror = null;
    fotoPerfil.src = FOTO_DEFECTO;
  };
  if (!fotoUrl) {
    fotoPerfil.src = FOTO_DEFECTO;
    return;
  }
  fotoPerfil.src = `${API_BASE}${fotoUrl}` + (recargarCache ? `?v=${Date.now()}` : '');
}

function limpiarSeleccionFoto() {
  inputFoto.value = '';           // permite volver a elegir el mismo archivo
  archivoFotoPendiente = null;
  if (urlVistaPrevia) {
    URL.revokeObjectURL(urlVistaPrevia);
    urlVistaPrevia = null;
  }
  fotoPreviewBox.hidden = true;
}

// Se llama al elegir un archivo. La validación aquí es solo para avisar rápido;
// la validación real (tipo por contenido y tamaño) la hace el backend.
function alElegirFoto() {
  const archivo = inputFoto.files[0];
  if (!archivo) return;

  if (!archivo.type.startsWith('image/')) {
    mostrarMensaje('Elige un archivo de imagen (JPG, PNG o WebP).');
    limpiarSeleccionFoto();
    return;
  }
  if (archivo.size > TAMANO_MAX_FOTO) {
    mostrarMensaje('La imagen supera 5 MB. Elige una más pequeña.');
    limpiarSeleccionFoto();
    return;
  }

  archivoFotoPendiente = archivo;
  if (urlVistaPrevia) URL.revokeObjectURL(urlVistaPrevia);
  urlVistaPrevia = URL.createObjectURL(archivo);
  fotoPreview.src = urlVistaPrevia;
  fotoPreviewBox.hidden = false;
  mostrarMensaje('');
}

// Envía la imagen con POST /api/clientes/{id}/foto.
// FormData arma el cuerpo multipart; NO se pone Content-Type a mano (el navegador
// añade el boundary). apiFetch solo añade JSON cuando el cuerpo es texto.
async function guardarFoto() {
  if (!archivoFotoPendiente || subiendoFoto) return;
  const id = idClienteGuardado();
  if (!id) return;

  const formulario = new FormData();
  formulario.append('foto', archivoFotoPendiente);

  subiendoFoto = true;
  btnGuardarFoto.disabled = true;
  mostrarMensaje('Subiendo foto...', true);
  try {
    const resp = await apiFetch(`${API_BASE}/api/clientes/${id}/foto`, {
      method: 'POST',
      body: formulario
    });
    if (!resp.ok) {
      // Motivo real del backend (tipo no válido, más de 5 MB, etc.)
      mostrarMensaje(await resp.text() || `No se pudo subir la foto (HTTP ${resp.status}).`);
      return;
    }
    const datos = await resp.json();
    clienteActual.fotoUrl = datos.fotoUrl;
    mostrarFoto(datos.fotoUrl, true);
    limpiarSeleccionFoto();
    mostrarMensaje('Foto de perfil actualizada.', true);
  } catch (err) {
    mostrarMensaje('No se pudo conectar con el servidor. La foto no se guardó.');
  } finally {
    subiendoFoto = false;
    btnGuardarFoto.disabled = false;
  }
}

function cancelarFoto() {
  limpiarSeleccionFoto();
  mostrarFoto(clienteActual ? clienteActual.fotoUrl : null);
  mostrarMensaje('');
}

// Tocar la foto o el botón de cámara abre el selector (galería o cámara en el celular)
avatarWrapper.addEventListener('click', () => {
  if (!clienteActual) return; // no cambiar la foto mientras no haya datos cargados
  inputFoto.click();
});
inputFoto.addEventListener('change', alElegirFoto);
btnGuardarFoto.addEventListener('click', guardarFoto);
btnCancelarFoto.addEventListener('click', cancelarFoto);

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
