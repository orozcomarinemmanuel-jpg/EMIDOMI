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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String nombre    = request.getParameter("nombre");
        String apellido  = request.getParameter("apellido");
        String correo    = request.getParameter("correo");
        String telefono  = request.getParameter("telefono");
        String direccion = request.getParameter("direccion");
        String ciudad    = request.getParameter("ciudad");

        Cliente cliente = new Cliente();
        cliente.setNombre(nombre);
        cliente.setApellido(apellido);
        cliente.setCorreo(correo);
        cliente.setTelefono(telefono);
        cliente.setDireccion(direccion);
        cliente.setCiudad(ciudad);

        ClienteDAO dao = new ClienteDAO();
        boolean exito = dao.insertar(cliente);

        request.setAttribute("exito", exito);
        request.setAttribute("cliente", cliente);
        request.getRequestDispatcher("resultado.jsp").forward(request, response);
    }
}