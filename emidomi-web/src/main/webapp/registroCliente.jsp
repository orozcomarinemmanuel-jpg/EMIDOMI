<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Emidomi - Registrar Cliente</title>
    <link rel="stylesheet" href="css/styles.css"> </head>
<body>
    <div class="container">
        <h2>Registro de Nuevo Cliente</h2>
        <form action="registrarCliente" method="post">
            <div class="form-group">
                <label>Nombre:</label>
                <input type="text" name="nombre" required>
            </div>
            <div class="form-group">
                <label>Apellido:</label>
                <input type="text" name="apellido" required>
            </div>
            <div class="form-group">
                <label>Correo Electrónico:</label>
                <input type="email" name="correo" required>
            </div>
            <div class="form-group">
                <label>Teléfono:</label>
                <input type="text" name="telefono">
            </div>
            <div class="form-group">
                <label>Dirección:</label>
                <input type="text" name="direccion">
            </div>
            <div class="form-group">
                <label>Ciudad:</label>
                <input type="text" name="ciudad">
            </div>
            <button type="submit">Registrar Cliente</button>
        </form>
    </div>
</body>
</html>