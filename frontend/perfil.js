// ── Navegación de opciones ──
const opciones = document.querySelectorAll('.opcion-item');
opciones.forEach(item => {
  item.addEventListener('click', () => {
    const texto = item.querySelector('span').textContent;
    console.log(`Navegando a: ${texto}`);
    // Aquí luego agregas la navegación a cada sección
  });
});
