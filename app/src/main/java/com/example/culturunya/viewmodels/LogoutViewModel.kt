package com.example.culturunya.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.culturunya.Api
import com.example.culturunya.repositories.UserRepository
import com.example.culturunya.session.CurrentSession
import androidx.credentials.CredentialManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class LogoutViewModel : ViewModel() {
    private val _logoutStatus = MutableStateFlow<Int?>(null)
    val logoutStatus: StateFlow<Int?> = _logoutStatus

    private val api = Api.instance
    private val repository = UserRepository(api)
    private lateinit var credentialManager: CredentialManager

    fun initialize(context: Context) {
        //Log.d("LogoutViewModel", "Inicializando CredentialManager")
        credentialManager = CredentialManager.create(context)
    }

    fun logout(context: Context? = null) {
        viewModelScope.launch {
            // Verificar si hay un token de Google activo
            val hasGoogleToken = CurrentSession.getGoogleToken().isNotEmpty()

            // Cerrar sesión en la API
            val token = CurrentSession.token
            val result = repository.logout("Token $token")

            result.onSuccess {
                // Si hay un token de Google, hacer también el logout de Google
                if (hasGoogleToken && context != null) {
                    logoutFromGoogle(context)
                }

                // Limpiar la sesión local
                CurrentSession.clearSession()
                _logoutStatus.value = 200
                Log.d("LogoutViewModel", "Logout exitoso")
            }.onFailure { error ->
                _logoutStatus.value = when (error) {
                    is HttpException -> error.code()
                    else -> -1
                }
                //Log.e("LogoutViewModel", "Error en logout: ${error.message}")
            }
        }
    }

    private fun logoutFromGoogle(context: Context) {
        try {
            // Usando la nueva API de Identity Services para cerrar sesión
            // No es necesario hacer un signOut explícito con la nueva API
            // Simplemente borramos los datos de la sesión en CurrentSession
            //Log.d("LogoutViewModel", "Limpiando datos de sesión de Google")
        } catch (e: Exception) {
            //Log.e("LogoutViewModel", "Excepción al manejar sesión con Google: ${e.message}")
        }
    }
}