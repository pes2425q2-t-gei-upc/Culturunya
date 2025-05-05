package com.example.culturunya.endpoints.sendMessageToAdmin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.culturunya.controllers.Api
import com.example.culturunya.controllers.ChatRepository
import com.example.culturunya.models.currentSession.CurrentSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class SendMessageToAdminViewModel : ViewModel() {
    private val _sendMessageToAdminStatus = MutableStateFlow<Int?>(null)
    val sendMessageToAdminStatus: StateFlow<Int?> = _sendMessageToAdminStatus

    private val api = Api.instance
    private val repository = ChatRepository(api)

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