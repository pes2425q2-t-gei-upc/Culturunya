package com.example.culturunya.endpoints.sendMessageToAdmin

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.culturunya.controllers.Api
import com.example.culturunya.controllers.AuthRepository
import com.example.culturunya.models.currentSession.CurrentSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class SendMessageToAdminViewModel : ViewModel() {
    private val _sendMessageToAdminStatus = MutableStateFlow<Int?>(null)
    val sendMessageToAdminStatus: StateFlow<Int?> = _sendMessageToAdminStatus

    private val api = Api.instance
    private val repository = AuthRepository(api)

    fun sendMessageToAdmin(message: String) {
        viewModelScope.launch {
            val token = CurrentSession.token
            val result = repository.sendMessageToAdmin("Token $token", message)

            result.onSuccess {
                Log.d("SendMessageToAdmin", "Mensaje enviado con éxito - Código 201")
                _sendMessageToAdminStatus.value = 201
            }.onFailure { error ->
                Log.e("SendMessageToAdmin", "Error al enviar mensaje: - ${error.message}")
                _sendMessageToAdminStatus.value = when (error) {
                    is HttpException -> error.code()
                    else -> -1
                }
            }
        }
    }
}