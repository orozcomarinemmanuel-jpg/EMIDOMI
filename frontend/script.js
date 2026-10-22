const API_BASE = 'http://localhost:8080';

// Helper: añade token automáticamente y redirige al login si recibe 401
async function apiFetch(url, options = {}) {
  const token = localStorage.getItem('emidomi_token');
  const headers = options.headers ? { ...options.headers } : {};
  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }
  if (!headers['Content-Type'] && options.body && typeof options.body === 'string') {
    headers['Content-Type'] = 'application/json';
  }
  const resp = await fetch(url, { ...options, headers });
  if (resp.status === 401) {
    localStorage.removeItem('emidomi_token');
    localStorage.removeItem('emidomi_cliente_id');
    window.location.href = 'index.html';
    throw new Error('No autorizado');
  }
  return resp;
}

// ── Login ──
const loginForm = document.getElementById('loginForm');
if (loginForm) {
  const loginMessage = document.getElementById('loginMessage');
  loginForm.addEventListener('submit', async (e) => {
    e.preventDefault();

    const email = document.getElementById('email').value.trim();
    const password = document.getElementById('password').value.trim();

    if (!email || !password) {
      if (loginMessage) {
        loginMessage.style.color = 'red';
        loginMessage.textContent = 'Por favor completa todos los campos.';
      }
      return;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
      if (loginMessage) {
        loginMessage.style.color = 'red';
        loginMessage.textContent = 'Por favor ingresa un correo válido.';
      }
      return;
    }

    try {
      if (loginMessage) {
        loginMessage.style.color = '#333';
        loginMessage.textContent = 'Iniciando sesión...';
      }
      const resp = await fetch(`${API_BASE}/api/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ correo: email, contrasena: password })
      });

      if (resp.status === 200) {
        const token = (await resp.text()).trim();
        if (token) {
          localStorage.setItem('emidomi_token', token);
          // Intentar extraer idCliente del payload JWT (base64url)
          try {
            const payload = JSON.parse(atob(token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/')));
            if (payload.idCliente) localStorage.setItem('emidomi_cliente_id', String(payload.idCliente));
          } catch (err) {
            // Ignorar si no se puede decodificar
          }
          if (loginMessage) {
            loginMessage.style.color = 'green';
            loginMessage.textContent = 'Inicio de sesión exitoso. Redirigiendo...';
          }
          setTimeout(() => {
            window.location.href = 'dashboard.html';
          }, 800);
          return;
        }
      }

      if (loginMessage) {
        loginMessage.style.color = 'red';
        loginMessage.textContent = 'Credenciales inválidas o error al iniciar sesión.';
      }
    } catch (err) {
      if (loginMessage) {
        loginMessage.style.color = 'red';
        loginMessage.textContent = 'No se pudo conectar con el servidor. Verifica que el backend esté en ejecución.';
      }
    }
  });
}

// ── Registro ──
const registroForm = document.getElementById('registroForm');
if (registroForm) {
  registroForm.addEventListener('submit', async (e) => {
    e.preventDefault();

    const nombre = document.getElementById('nombre').value.trim();
    const email = document.getElementById('email').value.trim();
    const password = document.getElementById('password').value.trim();
    const telefono = document.getElementById('telefono').value.trim();
    const direccion = document.getElementById('direccion').value.trim();
    const mensaje = document.getElementById('registroMessage');

    if (!nombre || !email || !password || !telefono || !direccion) {
      if (mensaje) {
        mensaje.style.color = 'red';
        mensaje.textContent = 'Por favor completa todos los campos.';
      }
      return;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
      if (mensaje) {
        mensaje.style.color = 'red';
        mensaje.textContent = 'Por favor ingresa un correo válido.';
      }
      return;
    }

    // Dividir nombre completo en nombre/apellido si es posible
    const partes = nombre.split(/\s+/);
    const nombreCliente = partes[0] || nombre;
    const apellidoCliente = partes.slice(1).join(' ') || partes[0] || nombre;

    const cliente = {
      nombre: nombreCliente,
      apellido: apellidoCliente,
      correo: email,
      contrasena: password,
      telefono: telefono,
      direccion: direccion,
      ciudad: 'Pereira',
      estado: true
    };

    try {
      if (mensaje) {
        mensaje.style.color = '#333';
        mensaje.textContent = 'Registrando...';
      }
      const resp = await fetch(`${API_BASE}/api/auth/registro`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(cliente)
      });

      if (resp.status === 201) {
        if (mensaje) {
          mensaje.style.color = 'green';
          mensaje.textContent = '¡Registro exitoso! Redirigiendo al inicio de sesión...';
        }
        setTimeout(() => {
          window.location.href = 'index.html';
        }, 1500);
        return;
      }

      if (resp.status === 409) {
        if (mensaje) {
          mensaje.style.color = 'red';
          mensaje.textContent = 'Ya existe un cliente registrado con ese correo.';
        }
        return;
      }

      const texto = await resp.text();
      if (mensaje) {
        mensaje.style.color = 'red';
        mensaje.textContent = texto || 'Error al registrar. Intenta nuevamente.';
      }
    } catch (err) {
      if (mensaje) {
        mensaje.style.color = 'red';
        mensaje.textContent = 'No se pudo conectar con el servidor. Verifica que el backend esté en ejecución.';
      }
    }
  });
}