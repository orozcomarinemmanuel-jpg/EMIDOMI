<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Lista de Clientes - EMIDOMI</title>
</head>
<body>
    <h1>Clientes Registrados</h1>

    <table border="1" cellpadding="8" cellspacing="0">
        <tr>
            <th>ID</th>
            <th>Nombre</th>
            <th>Apellido</th>
            <th>Correo</th>
            <th>Telefono</th>
            <th>Ciudad</th>
        </tr>

        <c:forEach var="cliente" items="${clientes}">
            <tr>
                <td>${cliente.idCliente}</td>
                <td>${cliente.nombre}</td>
                <td>${cliente.apellido}</td>
                <td>${cliente.correo}</td>
                <td>${cliente.telefono}</td>
                <td>${cliente.ciudad}</td>
            </tr>
        </c:forEach>
    </table>

    <br>
    <a href="registroCliente.html">Registrar nuevo cliente</a>

</body>
</html>