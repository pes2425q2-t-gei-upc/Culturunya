package com.example.culturunya.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.culturunya.Api
import com.example.culturunya.repositories.ChatRepository
import com.example.culturunya.dataclasses.messages.Message
import com.example.culturunya.CurrentSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class GetChatWithAdminViewModel : ViewModel() {

    private val _getChatWithAdminResponse = MutableStateFlow<List<Message>?>(null)
    val getChatWithAdminResponse: StateFlow<List<Message>?> = _getChatWithAdminResponse

    private val _getChatWithAdminError = MutableStateFlow<Int?>(null)
    val getChatWithAdminError: StateFlow<Int?> = _getChatWithAdminError

    private val api = Api.instance
    private val repository = ChatRepository(api)

    fun getChatWithAdmin() {
        Log.d("GetChatWithAdmin", "Function called")

        viewModelScope.launch {
            val token = CurrentSession.token
            Log.d("GetChatWithAdmin", "Using token: $token")

            val result = repository.getChatWithAdmin("Token $token")

            result.onSuccess { body ->
                Log.d("GetChatWithAdmin", "Success: Received ${body.size} messages")
                _getChatWithAdminResponse.value = body
                _getChatWithAdminError.value = null
            }.onFailure { error ->
                Log.e("GetChatWithAdmin", "Error fetching chat: ${error.message}")
                _getChatWithAdminResponse.value = null
                _getChatWithAdminError.value = when (error) {
                    is HttpException -> {
                        Log.e("GetChatWithAdmin", "HTTP Error code: ${error.code()}")
                        error.code()
                    }
                    else -> -1
                }
            }
        }
    }
}
