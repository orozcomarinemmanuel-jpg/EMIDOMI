// Navbar.jsx
// -----------------------------------------------------------------------
// Barra de navegación superior de EMIDOMI.
// Muestra un enlace por cada módulo del sistema (Cliente + los 6 restantes).
// El enlace correspondiente a la ruta activa se resalta con la clase "active".
// -----------------------------------------------------------------------

import { NavLink } from "react-router-dom";

// Definición de los módulos del sistema. Cada entrada tiene el texto a
// mostrar y la ruta a la que navega. Mantener esta lista aquí centraliza
// los enlaces para que agregar o quitar un módulo sea un solo cambio.
const MODULOS = [
  { nombre: "Cliente", ruta: "/clientes" },
  { nombre: "Restaurante", ruta: "/restaurantes" },
  { nombre: "Pedido", ruta: "/pedidos" },
  { nombre: "Pago", ruta: "/pagos" },
  { nombre: "Repartidor", ruta: "/repartidores" },
  { nombre: "Producto", ruta: "/productos" },
  { nombre: "Detalle Pedido", ruta: "/detalle-pedido" },
];

function Navbar() {
  return (
    <nav className="navbar">
      <span className="navbar__marca">EMIDOMI</span>
      <ul className="navbar__enlaces">
        {MODULOS.map((modulo) => (
          <li key={modulo.ruta}>
            <NavLink
              to={modulo.ruta}
              className={({ isActive }) => (isActive ? "navbar__enlace active" : "navbar__enlace")}
            >
              {modulo.nombre}
            </NavLink>
          </li>
        ))}
      </ul>
    </nav>
  );
}

export default Navbar;
