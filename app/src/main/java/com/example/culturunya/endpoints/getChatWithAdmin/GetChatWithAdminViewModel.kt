package com.example.culturunya.endpoints.getChatWithAdmin

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.culturunya.controllers.Api
import com.example.culturunya.controllers.AuthRepository
import com.example.culturunya.models.Message
import com.example.culturunya.models.currentSession.CurrentSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class GetChatWithAdminViewModel : ViewModel(){
    private val _getChatWithAdminResponse = MutableStateFlow<List<Message>?>(null)
    val getChatWithAdminResponse: StateFlow<List<Message>?> = _getChatWithAdminResponse

    private val _getChatWithAdminError = MutableStateFlow<Int?>(null)
    val getChatWithAdminError: StateFlow<Int?> = _getChatWithAdminError

    private val api = Api.instance
    private val repository = AuthRepository(api)

    fun getChatWithAdmin() {
        viewModelScope.launch {
            Log.d("GetChatWithAdminVM", "Llamando al endpoint de chat con admin")
            val token = CurrentSession.token
            Log.d("GetChatWithAdminVM", "Token: $token")
            val result = repository.getChatWithAdmin("Token $token")
            result.onSuccess { body ->
                Log.d("GetChatWithAdminVM", "Mensajes recibidos: ${body.size}")
                _getChatWithAdminResponse.value = body
                _getChatWithAdminError.value = null
            }.onFailure { error ->
                Log.e("GetChatWithAdminVM", "Error: ${error.message}")
                _getChatWithAdminResponse.value = null
                _getChatWithAdminError.value = when (error) {
                    is HttpException -> error.code()
                    else -> -1
                }
            }
        }
    }

}