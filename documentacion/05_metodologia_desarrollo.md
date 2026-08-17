# Metodología de Desarrollo - EMIDOMI

## Metodología Aplicada: Iterativa e Incremental

Se adoptó un enfoque de desarrollo iterativo e incremental, donde el sistema se construyó
en ciclos sucesivos, comenzando por los módulos fundamentales y agregando funcionalidad
progresivamente hasta completar el sistema.

---

## Fases del Desarrollo

### Fase 1: Configuración del Entorno
- Creación del proyecto Android con Kotlin + Jetpack Compose
- Configuración del proyecto Spring Boot con Java 17
- Configuración de MySQL (base de datos `db_domicilios`)
- Configuración de Git y repositorio remoto en GitHub
- Configuración de dependencias: Retrofit, OkHttp, Material3, Spring Security

### Fase 2: Diseño de la Base de Datos
- Diseño del esquema relacional con 7 tablas:
  - `clientes` - Usuarios del sistema
  - `productos` - Catálogo de productos alimenticios
  - `restaurantes` - Establecimientos asociados
  - `repartidores` - Personal de entrega
  - `pedidos` - Órdenes de compra
  - `detalle_pedido` - Líneas de producto por pedido
  - `pagos` - Registros de pago
- Definición de llaves foráneas, restricciones NOT NULL y valores por defecto
- Creación de enums para estados y métodos de pago

### Fase 3: Backend - API REST
- Implementación de las 7 entidades JPA con sus repositorios Spring Data
- Creación de 7 controladores REST con operaciones CRUD completas
- Implementación de 7 servicios de negocio
- Configuración de Spring Security con JWT (generación, validación, filtros)
- Autenticación: BCrypt para contraseñas, JWT para sesiones sin estado
- Configuración CORS para permitir acceso desde el emulador Android
- Configuración de network security para tráfico HTTP en desarrollo

### Fase 4: Android - Capa de Datos
- Modelado de las 7 entidades como data classes en Kotlin
- Implementación de 7 interfaces Retrofit (services) con los endpoints del backend
- Implementación de 7 repositories con manejo de errores (Result<T>)
- Configuración del interceptor JWT en OkHttp para inyectar tokens automáticamente
- Implementación de SessionManager con DataStore para persistir sesión

### Fase 5: Android - Capa de Lógica (ViewModels)
- Implementación de 7 ViewModels con patrón MVVM
- Gestión de estados reactivos con StateFlow
- Implementación de AuthViewModel con decodificación JWT para extraer idCliente
- Implementación de PedidoViewModel con lógica de confirmación de pedido
- Implementación de PagoViewModel con validaciones de monto

### Fase 6: Android - Capa de Presentación (UI)
- Implementación de 11 pantallas con Jetpack Compose
- Navegación por estado (string-based) en AppDomicilios()
- Login con validación de campos y manejo de errores
- Registro con formulario de 7 campos
- Catálogo de productos con detalle, cantidad y carrito
- Flujo completo: producto → carrito → confirmación → pago
- Pantallas de historial con códigos de color por estado
- Pantallas de restaurantes y repartidores
- Diseño responsive con Material Design 3

### Fase 7: Integración y Pruebas
- Pruebas de conectividad Android ↔ Backend (emulador → localhost)
- Resolución de problemas de serialización Jackson (@JsonIgnore vs @JsonManagedReference)
- Alineación de enums entre Android y Backend (case sensitivity)
- Resolución de restricciones NOT NULL en base de datos
- Resolución de validación de montos en flujo de pago
- Alineación de modelos de datos entre frontend y backend
- Pruebas de todos los endpoints con PowerShell

### Fase 8: Documentación
- Generación de diagrama de clases (PlantUML)
- Generación de diagrama de paquetes (PlantUML)
- Generación de diagrama de componentes (PlantUML)
- Generación de mapa de navegación (PlantUML)
- Documentación de metodología de desarrollo
- Escritura de pruebas unitarias

---

## Herramientas Utilizadas

| Categoría | Herramienta |
|---|---|
| IDE Android | Android Studio (AGP 9.1.1) |
| Lenguaje Android | Kotlin 2.2.10 |
| UI Framework | Jetpack Compose (BOM 2026.02.01) |
| Networking | Retrofit 2.11.0 + OkHttp 4.12.0 |
| Persistencia local | DataStore Preferences |
| IDE Backend | IntelliJ IDEA / VS Code |
| Lenguaje Backend | Java 17 |
| Framework Backend | Spring Boot 4.1.0 |
| Seguridad | Spring Security 7.1.0 + JWT |
| Base de datos | MySQL Server 9.7 |
| ORM | Hibernate (Spring Data JPA) |
| Control de versiones | Git + GitHub |
| Testing | JUnit 5 + Mockito |

---

## Arquitectura del Sistema

```
┌─────────────────────────────────────────────────────┐
│                 CAPA DE PRESENTACIÓN                │
│         Jetpack Compose (11 pantallas)              │
│     MainActivity → AppDomicilios() → Screens        │
├─────────────────────────────────────────────────────┤
│               CAPA DE LÓGICA DE NEGOCIO            │
│          7 ViewModels (MVVM + StateFlow)            │
├─────────────────────────────────────────────────────┤
│                   CAPA DE DATOS                     │
│      7 Repositories + SessionManager               │
├─────────────────────────────────────────────────────┤
│                  CAPA DE RED                        │
│     Retrofit + OkHttp (7 service interfaces)        │
│              JWT Interceptor                        │
├──────────────────── HTTP/JSON ──────────────────────┤
│               SERVIDOR BACKEND                      │
│        Spring Boot (Controllers → Services          │
│              → Repositories)                        │
│     SecurityConfig → JwtAuthFilter → JwtUtil        │
├─────────────────────────────────────────────────────┤
│               CAPA DE PERSISTENCIA                  │
│         MySQL (7 tablas,关系 FK)                    │
└─────────────────────────────────────────────────────┘
```

---

## Patrones de Diseño Aplicados

1. **MVVM (Model-View-ViewModel):** Separación clara entre UI (Screen),
   lógica de negocio (ViewModel) y datos (Model).

2. **Repository Pattern:** Cada entidad tiene su Repository que abstrae
   la fuente de datos (API REST), facilitando el testing y mantenimiento.

3. **Singleton Pattern:** RetrofitClient y SessionManager son singletons
   que garantizan una única instancia.

4. **Observer Pattern:** StateFlow en ViewModels permite que la UI reaccione
   a cambios de estado de forma reactiva.

5. **Filter Chain Pattern:** JwtAuthFilter se encadena en el pipeline de
   Spring Security para validar tokens en cada petición.
