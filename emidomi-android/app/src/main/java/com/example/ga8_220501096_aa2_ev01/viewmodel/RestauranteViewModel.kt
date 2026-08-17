package com.example.ga8_220501096_aa2_ev01.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ga8_220501096_aa2_ev01.data.model.Restaurante
import com.example.ga8_220501096_aa2_ev01.data.repository.RestauranteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RestauranteViewModel : ViewModel() {

    private val repository = RestauranteRepository()

    private val _restaurantes = MutableStateFlow<List<Restaurante>>(emptyList())
    val restaurantes: StateFlow<List<Restaurante>> = _restaurantes

    private val _restauranteActual = MutableStateFlow<Restaurante?>(null)
    val restauranteActual: StateFlow<Restaurante?> = _restauranteActual

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        cargarRestaurantes()
    }

    fun cargarRestaurantes() {
        viewModelScope.launch {
            _cargando.value = true
            _error.value = null
            val resultado = repository.getRestaurantes()
            resultado
                .onSuccess { _restaurantes.value = it }
                .onFailure { _error.value = "Error: ${it.message}" }
            _cargando.value = false
        }
    }

    fun cargarRestaurante(id: Int) {
        viewModelScope.launch {
            _cargando.value = true
            val resultado = repository.getRestaurantePorId(id)
            resultado
                .onSuccess { _restauranteActual.value = it }
                .onFailure { _error.value = "Error: ${it.message}" }
            _cargando.value = false
        }
    }
}
