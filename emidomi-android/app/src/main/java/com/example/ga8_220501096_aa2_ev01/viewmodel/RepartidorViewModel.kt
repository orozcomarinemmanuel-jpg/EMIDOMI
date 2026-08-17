package com.example.ga8_220501096_aa2_ev01.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ga8_220501096_aa2_ev01.data.model.Repartidor
import com.example.ga8_220501096_aa2_ev01.data.repository.RepartidorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RepartidorViewModel : ViewModel() {

    private val repository = RepartidorRepository()

    private val _repartidores = MutableStateFlow<List<Repartidor>>(emptyList())
    val repartidores: StateFlow<List<Repartidor>> = _repartidores

    private val _repartidorActual = MutableStateFlow<Repartidor?>(null)
    val repartidorActual: StateFlow<Repartidor?> = _repartidorActual

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        cargarRepartidores()
    }

    fun cargarRepartidores() {
        viewModelScope.launch {
            _cargando.value = true
            _error.value = null
            val resultado = repository.getRepartidores()
            resultado
                .onSuccess { _repartidores.value = it }
                .onFailure { _error.value = "Error: ${it.message}" }
            _cargando.value = false
        }
    }

    fun cargarRepartidor(id: Int) {
        viewModelScope.launch {
            _cargando.value = true
            val resultado = repository.getRepartidorPorId(id)
            resultado
                .onSuccess { _repartidorActual.value = it }
                .onFailure { _error.value = "Error: ${it.message}" }
            _cargando.value = false
        }
    }
}
