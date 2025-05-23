package com.example.culturunya.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.culturunya.Api
import com.example.culturunya.repositories.ChatRepository
import com.example.culturunya.session.CurrentSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

/**
 * ViewModel for sending messages to a user.
 *
 * @property api The API instance for making network requests.
 * @property repository The repository for sending messages to users.
 */
class SendMessageToUserViewModel : ViewModel() {
    private val _sendMessageToUserStatus = MutableStateFlow<Int?>(null)
    val sendMessageToUserStatus: StateFlow<Int?> = _sendMessageToUserStatus

    private val api = Api.instance
    private val repository = ChatRepository(api)

    /**
     * Sends a message to a user.
     *
     * @param userId The ID of the user to send the message to.
     * @param message The message to be sent.
     */
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
