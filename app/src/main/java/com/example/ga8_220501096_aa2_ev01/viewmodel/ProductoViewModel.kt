package com.example.ga8_220501096_aa2_ev01.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ga8_220501096_aa2_ev01.data.model.Producto
import com.example.ga8_220501096_aa2_ev01.data.repository.ProductoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProductoViewModel : ViewModel() {

    private val repository = ProductoRepository()

    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        cargarProductos()
    }

    fun cargarProductos() {
        viewModelScope.launch {
            _cargando.value = true
            _error.value = null
            val resultado = repository.getProductos()
            resultado
                .onSuccess { _productos.value = it }
                .onFailure { _error.value = "Error: ${it.message}" }
            _cargando.value = false
        }
    }
}
