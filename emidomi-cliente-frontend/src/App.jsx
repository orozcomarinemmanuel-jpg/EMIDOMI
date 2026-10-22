// App.jsx
// -----------------------------------------------------------------------
// Componente raíz / layout general de EMIDOMI.
// Ya NO contiene el CRUD de un módulo concreto (eso vive en pages/).
// Solo se encarga de:
//   1. Montar la barra de navegación (Navbar), común a toda la app.
//   2. Definir las rutas (<Routes>) de los 7 módulos y renderizar
//      la página correspondiente dentro de un <main>.
// Cada módulo expone su propia página bajo pages/.
// -----------------------------------------------------------------------

import { Navigate, Route, Routes } from "react-router-dom";
import Navbar from "./components/Navbar";
import ClientePage from "./pages/ClientePage";
import RestaurantePage from "./pages/RestaurantePage";
import PedidoPage from "./pages/PedidoPage";
import PagoPage from "./pages/PagoPage";
import RepartidorPage from "./pages/RepartidorPage";
import ProductoPage from "./pages/ProductoPage";
import DetallePedidoPage from "./pages/DetallePedidoPage";
import "./App.css";

function App() {
  return (
    <div className="app-layout">
      <Navbar />

      <main className="app-main">
        <Routes>
          {/* Redirige la raíz al módulo Cliente por defecto */}
          <Route path="/" element={<Navigate to="/clientes" replace />} />

          <Route path="/clientes" element={<ClientePage />} />
          <Route path="/restaurantes" element={<RestaurantePage />} />
          <Route path="/pedidos" element={<PedidoPage />} />
          <Route path="/pagos" element={<PagoPage />} />
          <Route path="/repartidores" element={<RepartidorPage />} />
          <Route path="/productos" element={<ProductoPage />} />
          <Route path="/detalle-pedido" element={<DetallePedidoPage />} />

          {/* Cualquier ruta desconocida vuelve al módulo Cliente */}
          <Route path="*" element={<Navigate to="/clientes" replace />} />
        </Routes>
      </main>
    </div>
  );
}

export default App;
