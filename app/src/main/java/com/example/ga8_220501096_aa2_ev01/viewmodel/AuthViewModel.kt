package com.example.ga8_220501096_aa2_ev01.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ga8_220501096_aa2_ev01.data.model.RegistroRequest
import com.example.ga8_220501096_aa2_ev01.data.network.RetrofitClient
import com.example.ga8_220501096_aa2_ev01.data.repository.AuthRepository
import com.example.ga8_220501096_aa2_ev01.data.session.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AuthRepository()
    private val context get() = getApplication<Application>()

    var correo = MutableStateFlow("")
    var contrasena = MutableStateFlow("")
    var nombre = MutableStateFlow("")
    var apellido = MutableStateFlow("")
    var telefono = MutableStateFlow("")
    var direccion = MutableStateFlow("")
    var ciudad = MutableStateFlow("")

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje

    private val _sesionActiva = MutableStateFlow(false)
    val sesionActiva: StateFlow<Boolean> = _sesionActiva

    private val _nombreUsuario = MutableStateFlow("")
    val nombreUsuario: StateFlow<String> = _nombreUsuario

    private val _idCliente = MutableStateFlow<Int?>(null)
    val idCliente: StateFlow<Int?> = _idCliente

    init {
        verificarSesion()
    }

    private fun verificarSesion() {
        viewModelScope.launch {
            val isLoggedIn = SessionManager.isLoggedIn(context)
            if (isLoggedIn) {
                _sesionActiva.value = true
                _nombreUsuario.value = SessionManager.getNombre(context) ?: ""
                _idCliente.value = SessionManager.getIdCliente(context)
                val token = SessionManager.getToken(context)
                if (token != null) {
                    RetrofitClient.authToken = token
                }
            }
        }
    }

    fun login() {
        viewModelScope.launch {
            if (correo.value.isBlank()) {
                _mensaje.value = "Error: ingresa tu correo electrónico"
                return@launch
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo.value).matches()) {
                _mensaje.value = "Error: ingresa un correo válido"
                return@launch
            }
            if (contrasena.value.isBlank()) {
                _mensaje.value = "Error: ingresa tu contraseña"
                return@launch
            }

            _cargando.value = true
            _mensaje.value = null
            val resultado = repository.login(correo.value, contrasena.value)
            resultado
                .onSuccess { token ->
                    RetrofitClient.authToken = token
                    SessionManager.saveToken(context, token)
                    SessionManager.saveUser(context, correo.value, correo.value.substringBefore("@"))
                    _nombreUsuario.value = correo.value.substringBefore("@")
                    _sesionActiva.value = true
                    _mensaje.value = "Bienvenido"
                }
                .onFailure { _mensaje.value = "Error: ${it.message}" }
            _cargando.value = false
        }
    }

    fun registro() {
        viewModelScope.launch {
            if (nombre.value.isBlank()) {
                _mensaje.value = "Error: ingresa tu nombre"
                return@launch
            }
            if (apellido.value.isBlank()) {
                _mensaje.value = "Error: ingresa tu apellido"
                return@launch
            }
            if (correo.value.isBlank()) {
                _mensaje.value = "Error: ingresa tu correo electrónico"
                return@launch
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo.value).matches()) {
                _mensaje.value = "Error: ingresa un correo válido"
                return@launch
            }
            if (contrasena.value.isBlank()) {
                _mensaje.value = "Error: ingresa tu contraseña"
                return@launch
            }
            if (contrasena.value.length < 6) {
                _mensaje.value = "Error: la contraseña debe tener mínimo 6 caracteres"
                return@launch
            }
            if (telefono.value.isBlank()) {
                _mensaje.value = "Error: ingresa tu teléfono"
                return@launch
            }
            if (direccion.value.isBlank()) {
                _mensaje.value = "Error: ingresa tu dirección"
                return@launch
            }
            if (ciudad.value.isBlank()) {
                _mensaje.value = "Error: ingresa tu ciudad"
                return@launch
            }

            _cargando.value = true
            _mensaje.value = null
            val request = RegistroRequest(
                nombre = nombre.value,
                apellido = apellido.value,
                correo = correo.value,
                contrasena = contrasena.value,
                telefono = telefono.value,
                direccion = direccion.value,
                ciudad = ciudad.value
            )
            val resultado = repository.registro(request)
            resultado
                .onSuccess { cliente ->
                    _mensaje.value = "Registro exitoso. Ahora inicia sesión."
                }
                .onFailure { _mensaje.value = "Error: ${it.message}" }
            _cargando.value = false
        }
    }

    fun cerrarSesion() {
        viewModelScope.launch {
            SessionManager.clearSession(context)
            RetrofitClient.authToken = null
            _sesionActiva.value = false
            _nombreUsuario.value = ""
            correo.value = ""
            contrasena.value = ""
            _mensaje.value = null
        }
    }

    fun limpiarMensaje() {
        _mensaje.value = null
    }
}
