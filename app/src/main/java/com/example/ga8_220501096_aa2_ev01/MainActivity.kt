package com.example.ga8_220501096_aa2_ev01

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ga8_220501096_aa2_ev01.data.model.Producto
import com.example.ga8_220501096_aa2_ev01.viewmodel.AuthViewModel
import com.example.ga8_220501096_aa2_ev01.viewmodel.DetallePedidoViewModel
import com.example.ga8_220501096_aa2_ev01.viewmodel.PagoViewModel
import com.example.ga8_220501096_aa2_ev01.viewmodel.PedidoViewModel
import com.example.ga8_220501096_aa2_ev01.viewmodel.ProductoViewModel
import com.example.ga8_220501096_aa2_ev01.viewmodel.RepartidorViewModel
import com.example.ga8_220501096_aa2_ev01.viewmodel.RestauranteViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppDomicilios()
        }
    }
}

@Composable
fun AppDomicilios() {
    val authViewModel: AuthViewModel = viewModel()
    val productoViewModel: ProductoViewModel = viewModel()
    val pedidoViewModel: PedidoViewModel = viewModel()
    val restauranteViewModel: RestauranteViewModel = viewModel()
    val repartidorViewModel: RepartidorViewModel = viewModel()
    val pagoViewModel: PagoViewModel = viewModel()
    val detallePedidoViewModel: DetallePedidoViewModel = viewModel()

    val sesionActiva by authViewModel.sesionActiva.collectAsState()
    val nombreUsuario by authViewModel.nombreUsuario.collectAsState()
    val idCliente by authViewModel.idCliente.collectAsState()

    var pantalla by remember { mutableStateOf("login") }
    val carrito = remember { mutableStateMapOf<Producto, Int>() }
    var productoSeleccionado by remember { mutableStateOf<Producto?>(null) }
    var pedidoSeleccionadoId by remember { mutableIntStateOf(0) }
    var direccionEntrega by remember { mutableStateOf("") }

    LaunchedEffect(sesionActiva) {
        if (sesionActiva && pantalla == "login") {
            pantalla = "home"
        }
    }

    when (pantalla) {
        "login" -> LoginScreen(
            onLoginSuccess = { pantalla = "home" },
            onIrARegistro = { pantalla = "registro" },
            viewModel = authViewModel
        )

        "registro" -> RegistroScreen(
            onRegistroExitoso = { pantalla = "login" },
            onVolver = { pantalla = "login" },
            viewModel = authViewModel
        )

        "home" -> PantallaPrincipal(
            productos = productoViewModel.productos.collectAsState().value,
            cargando = productoViewModel.cargando.collectAsState().value,
            error = productoViewModel.error.collectAsState().value,
            carrito = carrito,
            nombreUsuario = nombreUsuario,
            onIrACarrito = { pantalla = "carrito" },
            onIrAHistorial = { pantalla = "historial" },
            onIrARestaurantes = { pantalla = "restaurantes" },
            onIrARepartidores = { pantalla = "repartidores" },
            onCerrarSesion = {
                authViewModel.cerrarSesion()
                carrito.clear()
                pantalla = "login"
            },
            onProductoClick = { producto ->
                productoSeleccionado = producto
                pantalla = "detalle"
            },
            onRecargar = { productoViewModel.cargarProductos() }
        )

        "detalle" -> productoSeleccionado?.let { prod ->
            ProductoDetalleScreen(
                producto = prod,
                cantidad = carrito[prod] ?: 0,
                onVolver = { pantalla = "home" },
                onAgregarAlCarrito = { cantidad ->
                    carrito[prod] = (carrito[prod] ?: 0) + cantidad
                }
            )
        }

        "carrito" -> PantallaCarrito(
            carrito = carrito,
            onVolver = { pantalla = "home" },
            onConfirmarPedido = { items, total ->
                pedidoViewModel.confirmarPedido(
                    idCliente = idCliente,
                    direccionEntrega = direccionEntrega,
                    items = items,
                    total = total
                )
                pantalla = "confirmacion"
            },
            onDireccionChange = { direccionEntrega = it },
            direccionEntrega = direccionEntrega
        )

        "confirmacion" -> {
            val pedidoActual by pedidoViewModel.pedidoActual.collectAsState()
            val msgPedido by pedidoViewModel.mensaje.collectAsState()
            val cargandoPedido by pedidoViewModel.cargando.collectAsState()

            when {
                cargandoPedido -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = Color(0xFF1565C0))
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Procesando pedido...", fontSize = 16.sp, color = Color.Gray)
                        }
                    }
                }
                pedidoActual != null -> {
                    PedidoConfirmacionScreen(
                        pedido = pedidoActual!!,
                        onVolverAlInicio = {
                            pantalla = "pago"
                        }
                    )
                }
                msgPedido != null -> {
                    PedidoErrorScreen(
                        mensaje = msgPedido!!,
                        onReintentar = {
                            pedidoViewModel.limpiarMensaje()
                            pantalla = "carrito"
                        },
                        onVolver = {
                            pedidoViewModel.limpiarPedidoConfirmado()
                            pantalla = "home"
                        }
                    )
                }
            }
        }

        "historial" -> {
            val pedidos by pedidoViewModel.pedidos.collectAsState()
            val cargandoHist by pedidoViewModel.cargando.collectAsState()

            LaunchedEffect(Unit) {
                pedidoViewModel.cargarPedidos(idCliente ?: 1)
            }

            HistorialScreen(
                pedidos = pedidos,
                cargando = cargandoHist,
                onVolver = { pantalla = "home" },
                onPedidoClick = { idPedido ->
                    pedidoSeleccionadoId = idPedido
                    pantalla = "detalle_pedido"
                }
            )
        }

        "detalle_pedido" -> {
            val detalles by detallePedidoViewModel.detalles.collectAsState()
            val cargandoDet by detallePedidoViewModel.cargando.collectAsState()
            val errorDet by detallePedidoViewModel.error.collectAsState()

            LaunchedEffect(pedidoSeleccionadoId) {
                detallePedidoViewModel.cargarDetalles(pedidoSeleccionadoId)
            }

            DetallePedidoScreen(
                idPedido = pedidoSeleccionadoId,
                detalles = detalles,
                cargando = cargandoDet,
                error = errorDet,
                onVolver = { pantalla = "historial" },
                onRecargar = { detallePedidoViewModel.cargarDetalles(pedidoSeleccionadoId) }
            )
        }

        "restaurantes" -> {
            val restaurantes by restauranteViewModel.restaurantes.collectAsState()
            val cargandoRest by restauranteViewModel.cargando.collectAsState()
            val errorRest by restauranteViewModel.error.collectAsState()

            RestauranteScreen(
                restaurantes = restaurantes,
                cargando = cargandoRest,
                error = errorRest,
                onVolver = { pantalla = "home" },
                onRecargar = { restauranteViewModel.cargarRestaurantes() }
            )
        }

        "repartidores" -> {
            val repartidores by repartidorViewModel.repartidores.collectAsState()
            val cargandoRep by repartidorViewModel.cargando.collectAsState()
            val errorRep by repartidorViewModel.error.collectAsState()

            RepartidorScreen(
                repartidores = repartidores,
                cargando = cargandoRep,
                error = errorRep,
                onVolver = { pantalla = "home" },
                onRecargar = { repartidorViewModel.cargarRepartidores() }
            )
        }

        "pago" -> {
            val pedidoActual by pedidoViewModel.pedidoActual.collectAsState()
            val cargandoPago by pagoViewModel.cargando.collectAsState()
            val msgPago by pagoViewModel.mensaje.collectAsState()
            val pagoConfirmado by pagoViewModel.pagoConfirmado.collectAsState()

            val totalPedido = pedidoActual?.total ?: 0.0
            val idPedido = pedidoActual?.idPedido ?: 0

            PagoScreen(
                idPedido = idPedido,
                total = totalPedido,
                cargando = cargandoPago,
                mensaje = msgPago,
                pagoConfirmado = pagoConfirmado,
                onPagoRegistrado = { metodo ->
                    pagoViewModel.registrarPago(idPedido, metodo, totalPedido)
                },
                onVolver = { pantalla = "confirmacion" },
                onContinuar = {
                    carrito.clear()
                    pedidoViewModel.limpiarPedidoConfirmado()
                    pagoViewModel.limpiarPago()
                    pantalla = "home"
                }
            )
        }
    }
}

@Composable
fun PantallaPrincipal(
    productos: List<Producto>,
    cargando: Boolean,
    error: String?,
    carrito: MutableMap<Producto, Int>,
    nombreUsuario: String,
    onIrACarrito: () -> Unit,
    onIrAHistorial: () -> Unit,
    onIrARestaurantes: () -> Unit,
    onIrARepartidores: () -> Unit,
    onCerrarSesion: () -> Unit,
    onProductoClick: (Producto) -> Unit,
    onRecargar: () -> Unit
) {
    val totalItems = carrito.values.sum()

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF2F4F7))) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1565C0))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "EMIDOMI",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                if (nombreUsuario.isNotBlank()) {
                    Text(
                        text = "Hola, $nombreUsuario",
                        color = Color(0xFFBBDEFB),
                        fontSize = 12.sp
                    )
                }
            }
            Text(
                text = "📋",
                fontSize = 22.sp,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .clickable { onIrAHistorial() }
            )
            Text(
                text = "🏪",
                fontSize = 22.sp,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .clickable { onIrARestaurantes() }
            )
            Text(
                text = "🛵",
                fontSize = 22.sp,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .clickable { onIrARepartidores() }
            )
            Text(
                text = "🚪",
                fontSize = 22.sp,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .clickable { onCerrarSesion() }
            )
            Box {
                IconButton(onClick = onIrACarrito) {
                    Text(text = "🛒", fontSize = 24.sp)
                }
                if (totalItems > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .background(Color(0xFF64B5F6), shape = RoundedCornerShape(50))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(text = "$totalItems", color = Color.White, fontSize = 12.sp)
                    }
                }
            }
        }

        when {
            cargando -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF1565C0))
                }
            }
            error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "😔", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = error, color = Color.Gray, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onRecargar,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))
                        ) {
                            Text("Reintentar")
                        }
                    }
                }
            }
            productos.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay productos disponibles", color = Color.Gray)
                }
            }
            else -> {
                LazyColumn(modifier = Modifier.padding(12.dp)) {
                    items(productos.filter { it.estado != false }) { producto ->
                        val cantidadEnCarrito = carrito[producto] ?: 0
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White, shape = RoundedCornerShape(12.dp))
                                .padding(12.dp)
                                .clickable { onProductoClick(producto) },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = producto.emoji ?: "🍽️", fontSize = 32.sp)
                            Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                                Text(
                                    text = producto.nombre,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "$${producto.precio.toInt()}",
                                    color = Color.Gray
                                )
                                if (producto.categoria != null) {
                                    Text(
                                        text = producto.categoria,
                                        fontSize = 11.sp,
                                        color = Color(0xFF1565C0)
                                    )
                                }
                            }
                            if (cantidadEnCarrito > 0) {
                                Text(
                                    text = "x$cantidadEnCarrito",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1565C0),
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                            }
                            Button(
                                onClick = {
                                    carrito[producto] = (carrito[producto] ?: 0) + 1
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF1565C0)
                                )
                            ) {
                                Text(
                                    text = "+",
                                    fontSize = 18.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PantallaCarrito(
    carrito: MutableMap<Producto, Int>,
    onVolver: () -> Unit,
    onConfirmarPedido: (Map<Int, Int>, Double) -> Unit,
    onDireccionChange: (String) -> Unit,
    direccionEntrega: String
) {
    val total = carrito.entries.sumOf { it.key.precio * it.value }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF2F4F7))) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1565C0))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onVolver) {
                Text(text = "←", fontSize = 24.sp, color = Color.White)
            }
            Text(
                text = "Tu carrito",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (carrito.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "🛒", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Tu carrito está vacío", color = Color.Gray, fontSize = 16.sp)
                }
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f).padding(12.dp)) {
                items(carrito.entries.toList()) { (producto, cantidad) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, shape = RoundedCornerShape(12.dp))
                            .padding(12.dp)
                            .padding(bottom = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = producto.emoji ?: "🍽️", fontSize = 28.sp)
                        Column(modifier = Modifier.weight(1f).padding(start = 10.dp)) {
                            Text(text = producto.nombre, fontWeight = FontWeight.Bold)
                            Text(
                                text = "$${producto.precio.toInt()} x $cantidad",
                                color = Color.Gray
                            )
                        }
                        IconButton(onClick = {
                            if (cantidad > 1) carrito[producto] = cantidad - 1
                            else carrito.remove(producto)
                        }) {
                            Text("−", fontSize = 20.sp)
                        }
                        Text(text = "$cantidad", fontSize = 16.sp)
                        IconButton(onClick = { carrito[producto] = cantidad + 1 }) {
                            Text("+", fontSize = 20.sp)
                        }
                        IconButton(onClick = { carrito.remove(producto) }) {
                            Text(text = "🗑️", fontSize = 20.sp)
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = direccionEntrega,
                    onValueChange = onDireccionChange,
                    label = { Text("Dirección de entrega") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Total: $${total.toInt()}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        val items = carrito.mapKeys { it.key.idProducto ?: 0 }
                            .filterKeys { it > 0 }
                            .mapValues { it.value }
                        onConfirmarPedido(items, total)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0)),
                    enabled = carrito.isNotEmpty() && direccionEntrega.isNotBlank()
                ) {
                    Text("Confirmar pedido")
                }
            }
        }
    }
}

@Composable
fun PedidoConfirmacionScreen(
    pedido: com.example.ga8_220501096_aa2_ev01.data.model.Pedido,
    onVolverAlInicio: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F4F7))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "✅", fontSize = 72.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "¡Pedido confirmado!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2E7D32)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Pedido #${pedido.idPedido}",
            fontSize = 18.sp,
            color = Color(0xFF1565C0)
        )
        Text(
            text = "Estado: ${pedido.estadoPedido}",
            fontSize = 14.sp,
            color = Color.Gray
        )
        Text(
            text = "Total: $${(pedido.total ?: 0.0).toInt()}",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1565C0)
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onVolverAlInicio,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))
        ) {
            Text("Volver al inicio", fontSize = 16.sp, color = Color.White)
        }
    }
}

@Composable
fun PedidoErrorScreen(
    mensaje: String,
    onReintentar: () -> Unit,
    onVolver: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F4F7))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "😔", fontSize = 72.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Error al procesar pedido",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFC62828)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
        ) {
            Text(
                text = mensaje,
                modifier = Modifier.padding(12.dp),
                color = Color(0xFFC62828),
                fontSize = 14.sp
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onReintentar,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))
        ) {
            Text("Reintentar", color = Color.White)
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = onVolver) {
            Text("Volver al inicio", color = Color(0xFF1565C0))
        }
    }
}
