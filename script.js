// ── Login ──
const loginForm = document.getElementById('loginForm');
if (loginForm) {
  const loginMessage = document.getElementById('loginMessage');
  loginForm.addEventListener('submit', (e) => {
    e.preventDefault();

    const email = document.getElementById('email').value.trim();
    const password = document.getElementById('password').value.trim();

    if (!email || !password) {
      loginMessage.style.color = 'red';
      loginMessage.textContent = 'Por favor completa todos los campos.';
      return;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
      loginMessage.style.color = 'red';
      loginMessage.textContent = 'Por favor ingresa un correo válido.';
      return;
    }

    window.location.href = 'dashboard.html';
  });
}

// ── Registro ──
const registroForm = document.getElementById('registroForm');
if (registroForm) {
  registroForm.addEventListener('submit', (e) => {
    e.preventDefault();

    const nombre = document.getElementById('nombre').value.trim();
    const email = document.getElementById('email').value.trim();
    const password = document.getElementById('password').value.trim();
    const telefono = document.getElementById('telefono').value.trim();
    const direccion = document.getElementById('direccion').value.trim();
    const rol = document.getElementById('rol').value;
    const mensaje = document.getElementById('registroMessage');

    if (!nombre || !email || !password || !telefono || !direccion || !rol) {
      mensaje.style.color = 'red';
      mensaje.textContent = 'Por favor completa todos los campos.';
      return;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
      mensaje.style.color = 'red';
      mensaje.textContent = 'Por favor ingresa un correo válido.';
      return;
    }

    mensaje.style.color = 'green';
    mensaje.textContent = '¡Registro exitoso! Redirigiendo...';

    setTimeout(() => {
      window.location.href = 'login.html';
    }, 2000);
  });
}