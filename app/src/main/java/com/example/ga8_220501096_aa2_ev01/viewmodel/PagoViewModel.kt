package com.example.ga8_220501096_aa2_ev01.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ga8_220501096_aa2_ev01.data.model.Pago
import com.example.ga8_220501096_aa2_ev01.data.repository.PagoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PagoViewModel : ViewModel() {

    private val repository = PagoRepository()

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje

    private val _pagoConfirmado = MutableStateFlow<Pago?>(null)
    val pagoConfirmado: StateFlow<Pago?> = _pagoConfirmado

    fun registrarPago(idPedido: Int, metodoPago: String, monto: Double) {
        if (idPedido <= 0) {
            _mensaje.value = "Error: pedido inválido"
            return
        }
        if (monto <= 0) {
            _mensaje.value = "Error: el monto debe ser mayor a 0"
            return
        }

        viewModelScope.launch {
            _cargando.value = true
            _mensaje.value = null
            val pago = Pago(
                idPago = null,
                idPedido = idPedido,
                metodoPago = metodoPago,
                estadoPago = "PENDIENTE",
                monto = monto,
                referencia = null,
                fechaPago = null
            )
            val resultado = repository.crearPago(pago)
            resultado
                .onSuccess {
                    _pagoConfirmado.value = it
                    _mensaje.value = "Pago registrado"
                }
                .onFailure { _mensaje.value = "Error: ${it.message}" }
            _cargando.value = false
        }
    }

    fun limpiarPago() {
        _pagoConfirmado.value = null
        _mensaje.value = null
    }

    fun limpiarMensaje() {
        _mensaje.value = null
    }
}
