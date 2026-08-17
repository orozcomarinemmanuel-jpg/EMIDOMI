package com.example.ga8_220501096_aa2_ev01.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ga8_220501096_aa2_ev01.data.model.DetallePedidoRequest
import com.example.ga8_220501096_aa2_ev01.data.model.Pedido
import com.example.ga8_220501096_aa2_ev01.data.model.PedidoRequest
import com.example.ga8_220501096_aa2_ev01.data.repository.PedidoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PedidoViewModel : ViewModel() {

    private val repository = PedidoRepository()

    private val _pedidos = MutableStateFlow<List<Pedido>>(emptyList())
    val pedidos: StateFlow<List<Pedido>> = _pedidos

    private val _pedidoActual = MutableStateFlow<Pedido?>(null)
    val pedidoActual: StateFlow<Pedido?> = _pedidoActual

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje

    private val _pedidoConfirmado = MutableStateFlow(false)
    val pedidoConfirmado: StateFlow<Boolean> = _pedidoConfirmado

    fun confirmarPedido(idCliente: Int?, direccionEntrega: String, items: Map<Int, Int>) {
        if (idCliente == null) {
            _mensaje.value = "Error: debes iniciar sesión"
            return
        }
        if (direccionEntrega.isBlank()) {
            _mensaje.value = "Error: ingresa una dirección de entrega"
            return
        }
        if (items.isEmpty()) {
            _mensaje.value = "Error: el carrito está vacío"
            return
        }

        viewModelScope.launch {
            _cargando.value = true
            _mensaje.value = null
            _pedidoConfirmado.value = false

            val detalle = items.map { (idProducto, cantidad) ->
                DetallePedidoRequest(idProducto = idProducto, cantidad = cantidad)
            }
            val request = PedidoRequest(
                idCliente = idCliente,
                direccionEntrega = direccionEntrega,
                detallePedido = detalle
            )

            val resultado = repository.crearPedido(request)
            resultado
                .onSuccess { pedido ->
                    _pedidoActual.value = pedido
                    _pedidoConfirmado.value = true
                    _mensaje.value = "Pedido #${pedido.idPedido} confirmado"
                }
                .onFailure { _mensaje.value = "Error: ${it.message}" }
            _cargando.value = false
        }
    }

    fun cargarPedidos(idCliente: Int) {
        viewModelScope.launch {
            _cargando.value = true
            val resultado = repository.getPedidosPorCliente(idCliente)
            resultado
                .onSuccess { _pedidos.value = it }
                .onFailure { _mensaje.value = "Error: ${it.message}" }
            _cargando.value = false
        }
    }

    fun limpiarPedidoConfirmado() {
        _pedidoConfirmado.value = false
        _pedidoActual.value = null
        _mensaje.value = null
    }

    fun limpiarMensaje() {
        _mensaje.value = null
    }
}
