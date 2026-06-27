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
