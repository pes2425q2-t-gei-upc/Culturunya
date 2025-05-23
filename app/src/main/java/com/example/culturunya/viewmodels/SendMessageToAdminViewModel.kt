package com.example.culturunya.viewmodels

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
 * ViewModel for sending messages to the admin.
 *
 * @property api The API instance for making network requests.
 * @property repository The repository for sending messages to the admin.
 */
class SendMessageToAdminViewModel : ViewModel() {
    private val _sendMessageToAdminStatus = MutableStateFlow<Int?>(null)
    val sendMessageToAdminStatus: StateFlow<Int?> = _sendMessageToAdminStatus

    private val api = Api.instance
    private val repository = ChatRepository(api)

    /**
     * Sends a message to the admin.
     *
     * @param message The message to be sent.
     */
    fun sendMessageToAdmin(message: String) {
        viewModelScope.launch {
            val token = CurrentSession.token
            val result = repository.sendMessageToAdmin("Token $token", message)

            result.onSuccess {
                _sendMessageToAdminStatus.value = 201
            }.onFailure { error ->
                _sendMessageToAdminStatus.value = when (error) {
                    is HttpException -> error.code()
                    else -> -1
                }
            }
        }
    }
}