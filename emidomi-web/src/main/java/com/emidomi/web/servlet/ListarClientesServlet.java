package com.emidomi.web.servlet;

import com.emidomi.web.dao.ClienteDAO;
import com.emidomi.web.model.Cliente;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/listarClientes")
public class ListarClientesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        ClienteDAO dao = new ClienteDAO();
        List<Cliente> clientes = dao.listarTodos();

        request.setAttribute("clientes", clientes);

        request.getRequestDispatcher("listaClientes.jsp").forward(request, response);
    }
}