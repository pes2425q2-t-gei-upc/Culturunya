package com.example.culturunya.endpoints.getChatWithUserViewModel

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

class GetChatWithUserViewModel : ViewModel() {

    private val _getChatWithUserResponse = MutableStateFlow<List<Message>?>(null)
    val getChatWithUserResponse: StateFlow<List<Message>?> = _getChatWithUserResponse

    private val _getChatWithUserError = MutableStateFlow<Int?>(null)
    val getChatWithUserError: StateFlow<Int?> = _getChatWithUserError

    private val api = Api.instance
    private val repository = AuthRepository(api)

    fun getChatWithUser(userId: String) {
        Log.d("GetChatWithUser", "Function called with userId: $userId")

        viewModelScope.launch {
            val token = CurrentSession.token
            Log.d("GetChatWithUser", "Using token: $token")

            val result = repository.getChatWithUser("Token $token", userId)

            result.onSuccess { body ->
                Log.d("GetChatWithUser", "Success: Received ${body.size} messages")
                _getChatWithUserResponse.value = body
                _getChatWithUserError.value = null
            }.onFailure { error ->
                Log.e("GetChatWithUser", "Error fetching chat with user $userId: ${error.message}")
                _getChatWithUserResponse.value = null
                _getChatWithUserError.value = when (error) {
                    is HttpException -> {
                        Log.e("GetChatWithUser", "HTTP Error code: ${error.code()}")
                        error.code()
                    }
                    else -> -1
                }
            }
        }
    }
}
