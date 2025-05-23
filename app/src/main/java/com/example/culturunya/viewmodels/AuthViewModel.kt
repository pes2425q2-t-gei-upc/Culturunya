package com.example.culturunya.viewmodels

import SessionManager
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.culturunya.dataclasses.users.AuthState
import com.example.culturunya.session.CurrentSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para manejar el estado de autenticación del usuario.
 * Carga la sesión actual y maneja el cierre de sesión.
 */
class AuthViewModel : ViewModel() {

    companion object {
        private const val TAG = "AuthViewModel"
    }

    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState

    /**
     * Carga la sesión actual desde DataStore.
     * @param sessionManager SessionManager para manejar la sesión.
     */
    fun loadSession(sessionManager: SessionManager) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Loading session from DataStore...")
                CurrentSession.loadFromDataStore(sessionManager)
                Log.d(TAG, "DataStore load complete.")

                val isAuthenticated = try {
                    CurrentSession.hasActiveSession()
                } catch (e: Exception) {
                    Log.e(TAG, "Error en hasActiveSession: ${e.message}", e)
                    false
                }

                Log.d(TAG, "Session loaded. isAuthenticated=$isAuthenticated")

                _authState.value = AuthState(
                    isAuthenticated = isAuthenticated,
                    isLoading = false
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error en loadSession: ${e.message}", e)
            }
        }
    }

    /**
     * Cierra la sesión del usuario.
     * @param sessionManager SessionManager para manejar la sesión.
     */
    fun logout(sessionManager: SessionManager) {
        viewModelScope.launch {
            Log.d(TAG, "Logging out...")
            CurrentSession.clearSession()
            sessionManager.clearSession()
            Log.d(TAG, "Session cleared.")

            _authState.value = AuthState(
                isAuthenticated = false,
                isLoading = false
            )
        }
    }
}
