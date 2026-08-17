package com.emidomi.web.servlet;

import com.emidomi.web.dao.ClienteDAO;
import com.emidomi.web.model.Cliente;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/registrarCliente")
public class RegistrarClienteServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Leer los datos que envió el formulario HTML
        String nombre    = request.getParameter("nombre");
        String apellido  = request.getParameter("apellido");
        String correo    = request.getParameter("correo");
        String telefono  = request.getParameter("telefono");
        String direccion = request.getParameter("direccion");
        String ciudad    = request.getParameter("ciudad");

        // 2. Construir el objeto Cliente con esos datos
        Cliente cliente = new Cliente();
        cliente.setNombre(nombre);
        cliente.setApellido(apellido);
        cliente.setCorreo(correo);
        cliente.setTelefono(telefono);
        cliente.setDireccion(direccion);
        cliente.setCiudad(ciudad);

        // 3. Guardarlo en la base de datos usando el DAO
        ClienteDAO dao = new ClienteDAO();
        boolean exito = dao.insertar(cliente);

        // 4. Guardar el resultado como atributo para que la JSP lo muestre
        request.setAttribute("exito", exito);
        request.setAttribute("cliente", cliente);

        // 5. Redirigir (reenviar internamente) a una página JSP con el resultado
        request.getRequestDispatcher("resultado.jsp").forward(request, response);
    }
}