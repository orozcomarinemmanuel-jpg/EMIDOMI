<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.emidomi.web.model.Cliente" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Resultado del Registro</title>
</head>
<body>
    <div class="container">
        <% 
            Boolean exito = (Boolean) request.getAttribute("exito");
            Cliente cliente = (Cliente) request.getAttribute("cliente");
            
            if (exito != null && exito) { 
        %>
            <h2 style="color: green;">¡Cliente registrado con éxito!</h2>
            <p><strong>Bienvenido:</strong> <%= cliente.getNombre() %> <%= cliente.getApellido() %></p>
            <p>Los datos han sido almacenados correctamente en el sistema de domicilios Emidomi.</p>
        <% } else { %>
            <h2 style="color: red;">Error en el registro</h2>
            <p>Hubo un problema al guardar los datos en la base de datos. Por favor, verifica la conexión.</p>
        <% } %>
        
        <br>
        <a href="registroCliente.jsp">Volver al formulario</a>
    </div>
</body>
</html>