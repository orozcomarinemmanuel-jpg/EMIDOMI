# EMIDOMI - Módulo Cliente (Front-end)

Evidencia GA7-220501096-AA4-EV03. Codificación del componente front-end
del módulo Cliente del proyecto formativo EMIDOMI, usando React.

## Descripción

Este módulo permite gestionar los clientes de la plataforma EMIDOMI:

- Listar los clientes registrados.
- Registrar un nuevo cliente.
- Editar los datos de un cliente existente.
- Eliminar un cliente.

Consume la API REST construida previamente en Spring Boot
(`emidomi-cliente-service`, `ClienteController`), correspondiente al
mismo proyecto formativo.

## Tecnologías

- React 19 + Vite
- Axios (consumo de la API REST)
- CSS plano

## Estructura del proyecto

```
src/
├── components/
│   ├── ClienteForm.jsx   # Formulario de creación / edición
│   └── ClienteList.jsx   # Tabla de clientes con acciones
├── services/
│   └── clienteService.js # Llamadas HTTP al backend (Axios)
├── App.jsx                # Orquesta el estado y los componentes
├── App.css
└── main.jsx
```

## Cómo ejecutarlo

1. Tener corriendo el backend `emidomi-cliente-service` en
   `http://localhost:8080` (ver repositorio principal de EMIDOMI).
2. Instalar dependencias:
   ```
   npm install
   ```
3. Ejecutar en modo desarrollo:
   ```
   npm run dev
   ```
4. Abrir la URL que indique la terminal (por defecto `http://localhost:5173`).

## Repositorio

Repositorio del proyecto EMIDOMI: ver archivo `enlace_repositorio.txt`
en la carpeta de entrega.
