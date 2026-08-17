package com.example.ga8_220501096_aa2_ev01.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ga8_220501096_aa2_ev01.data.model.DetallePedido
import com.example.ga8_220501096_aa2_ev01.data.repository.DetallePedidoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DetallePedidoViewModel : ViewModel() {

    private val repository = DetallePedidoRepository()

    private val _detalles = MutableStateFlow<List<DetallePedido>>(emptyList())
    val detalles: StateFlow<List<DetallePedido>> = _detalles

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun cargarDetalles(idPedido: Int) {
        viewModelScope.launch {
            _cargando.value = true
            _error.value = null
            val resultado = repository.getDetallesPorPedido(idPedido)
            resultado
                .onSuccess { _detalles.value = it }
                .onFailure { _error.value = "Error: ${it.message}" }
            _cargando.value = false
        }
    }

    fun limpiar() {
        _detalles.value = emptyList()
        _error.value = null
    }
}
