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
        viewModelScope.launch {
            val token = CurrentSession.token

            val result = repository.getChatWithUser("Token $token", userId)

            result.onSuccess { body ->
                _getChatWithUserResponse.value = body
                _getChatWithUserError.value = null
            }.onFailure { error ->
                _getChatWithUserResponse.value = null
                _getChatWithUserError.value = when (error) {
                    is HttpException -> {
                        error.code()
                    }
                    else -> {
                        -1
                    }
                }
            }
        }
    }
}
