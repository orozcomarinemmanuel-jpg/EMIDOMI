package com.example.ga8_220501096_aa2_ev01.data.session

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "emidomi_session")

object SessionManager {

    private val TOKEN_KEY = stringPreferencesKey("auth_token")
    private val CORREO_KEY = stringPreferencesKey("correo")
    private val NOMBRE_KEY = stringPreferencesKey("nombre")
    private val ID_CLIENTE_KEY = intPreferencesKey("id_cliente")

    suspend fun saveToken(context: Context, token: String) {
        context.dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
        }
    }

    suspend fun getToken(context: Context): String? {
        return context.dataStore.data.map { prefs ->
            prefs[TOKEN_KEY]
        }.first()
    }

    suspend fun saveUser(context: Context, correo: String, nombre: String) {
        context.dataStore.edit { prefs ->
            prefs[CORREO_KEY] = correo
            prefs[NOMBRE_KEY] = nombre
        }
    }

    suspend fun saveIdCliente(context: Context, idCliente: Int) {
        context.dataStore.edit { prefs ->
            prefs[ID_CLIENTE_KEY] = idCliente
        }
    }

    suspend fun getIdCliente(context: Context): Int? {
        return context.dataStore.data.map { prefs ->
            prefs[ID_CLIENTE_KEY]
        }.first()
    }

    suspend fun getCorreo(context: Context): String? {
        return context.dataStore.data.map { prefs ->
            prefs[CORREO_KEY]
        }.first()
    }

    suspend fun getNombre(context: Context): String? {
        return context.dataStore.data.map { prefs ->
            prefs[NOMBRE_KEY]
        }.first()
    }

    suspend fun clearSession(context: Context) {
        context.dataStore.edit { prefs ->
            prefs.clear()
        }
    }

    suspend fun isLoggedIn(context: Context): Boolean {
        return getToken(context) != null
    }
}
