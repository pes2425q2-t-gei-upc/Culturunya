package com.example.culturunya.endpoints.sendMessageToUser

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

class SendMessageToUserViewModel : ViewModel() {
    private val _sendMessageToUserStatus = MutableStateFlow<Int?>(null)
    val sendMessageToUserStatus: StateFlow<Int?> = _sendMessageToUserStatus

    private val api = Api.instance
    private val repository = AuthRepository(api)

    fun sendMessageToUser(userId: Int, message: String) {
        viewModelScope.launch {
            Log.d("SendMessageToUserVM", "Attempting to send message to userId=$userId")
            val token = CurrentSession.token
            Log.d("SendMessageToUserVM", "Using token: Token $token")

            val result = repository.sendMessageToUser("Token $token", userId, message)

            result.onSuccess {
                Log.d("SendMessageToUserVM", "Message sent successfully.")
                _sendMessageToUserStatus.value = 201
            }.onFailure { error ->
                val errorCode = when (error) {
                    is HttpException -> {
                        Log.d("SendMessageToUserVM", "HTTP error code: ${error.code()}")
                        error.code()
                    }
                    else -> {
                        Log.d("SendMessageToUserVM", "Unknown error: ${error.message}")
                        -1
                    }
                }
                _sendMessageToUserStatus.value = errorCode
            }
        }
    }
}
