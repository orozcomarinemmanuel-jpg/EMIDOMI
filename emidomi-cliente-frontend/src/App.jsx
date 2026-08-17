// App.jsx
// -----------------------------------------------------------------------
// Componente raíz del módulo Cliente de EMIDOMI.
// Se encarga de:
//   1. Cargar la lista de clientes al iniciar la aplicación.
//   2. Coordinar el formulario (ClienteForm) y la tabla (ClienteList).
//   3. Llamar al servicio HTTP (clienteService) para crear, actualizar
//      y eliminar clientes contra el backend en Spring Boot.
// -----------------------------------------------------------------------

import { useEffect, useState } from "react";
import ClienteForm from "./components/ClienteForm";
import ClienteList from "./components/ClienteList";
import {
  actualizarCliente,
  crearCliente,
  eliminarCliente,
  listarClientes,
} from "./services/clienteService";
import "./App.css";

function App() {
  // Lista de clientes que se muestra en pantalla.
  const [clientes, setClientes] = useState([]);

  // Cliente que se está editando actualmente (null cuando no hay edición en curso).
  const [clienteEnEdicion, setClienteEnEdicion] = useState(null);

  // Mensaje de error para mostrarle algo útil al usuario si falla una petición,
  // en lugar de dejar la pantalla en blanco o solo un error en consola.
  const [mensajeError, setMensajeError] = useState("");

  // Se ejecuta una sola vez, al montar el componente, para cargar
  // los clientes existentes desde el backend.
  useEffect(() => {
    cargarClientes();
  }, []);

  // Consulta todos los clientes en el backend y actualiza el estado local.
  async function cargarClientes() {
    try {
      const datos = await listarClientes();
      setClientes(datos);
      setMensajeError("");
    } catch (error) {
      console.error("Error al cargar los clientes:", error);
      setMensajeError(
        "No se pudo conectar con el servidor. Verifica que el backend esté encendido."
      );
    }
  }

  // Decide si debe CREAR un cliente nuevo o ACTUALIZAR uno existente,
  // según si clienteEnEdicion tiene valor o no.
  async function manejarGuardado(datosCliente) {
    try {
      if (clienteEnEdicion) {
        await actualizarCliente(clienteEnEdicion.idCliente, datosCliente);
      } else {
        await crearCliente(datosCliente);
      }
      setClienteEnEdicion(null);
      await cargarClientes(); // Refresca la tabla con los datos actualizados
    } catch (error) {
      console.error("Error al guardar el cliente:", error);
      setMensajeError("No se pudo guardar el cliente. Revisa los datos ingresados.");
    }
  }

  // Prepara el formulario para editar el cliente seleccionado en la tabla.
  function manejarEdicion(cliente) {
    setClienteEnEdicion(cliente);
  }

  // Cancela la edición en curso y limpia el formulario.
  function manejarCancelacion() {
    setClienteEnEdicion(null);
  }

  // Elimina un cliente, pidiendo confirmación antes para evitar borrados accidentales.
  async function manejarEliminacion(idCliente) {
    const confirmado = window.confirm("¿Seguro que deseas eliminar este cliente?");
    if (!confirmado) return;

    try {
      await eliminarCliente(idCliente);
      await cargarClientes();
    } catch (error) {
      console.error("Error al eliminar el cliente:", error);
      setMensajeError("No se pudo eliminar el cliente.");
    }
  }

  return (
    <div className="app-container">
      <header>
        <h1>EMIDOMI · Módulo Cliente</h1>
        <p>Gestión de clientes: registro, edición y eliminación.</p>
      </header>

      {mensajeError && <div className="app-container__error">{mensajeError}</div>}

      <ClienteForm
        clienteEnEdicion={clienteEnEdicion}
        onGuardar={manejarGuardado}
        onCancelar={manejarCancelacion}
      />

      <ClienteList
        clientes={clientes}
        onEditar={manejarEdicion}
        onEliminar={manejarEliminacion}
      />
    </div>
  );
}

export default App;
