package com.example.culturunya.endpoints.getChats

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.culturunya.controllers.Api
import com.example.culturunya.controllers.AuthRepository
import com.example.culturunya.models.currentSession.CurrentSession
import com.example.culturunya.models.getChats.ChatInfo
import com.example.culturunya.models.login.LoginRequest
import com.example.culturunya.models.login.LoginResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class GetChatsViewModel : ViewModel() {
    private val _getChatsResponse = MutableStateFlow<List<ChatInfo>?>(null)
    val getChatsResponse: StateFlow<List<ChatInfo>?> = _getChatsResponse

    private val _getChatsError = MutableStateFlow<Int?>(null)
    val getChatsError: StateFlow<Int?> = _getChatsError

    private val api = Api.instance
    private val repository = AuthRepository(api)

    fun getChats() {
        viewModelScope.launch {
            val token = CurrentSession.token
            val result = repository.getChats("Token $token")
            Log.d("GetChats", "Llamando al endpoint de getchats")
            result.onSuccess { body ->
                _getChatsResponse.value = body
                _getChatsError.value = null
                Log.d("GetChats", "Xats rebuts: ${body.size}")
            }.onFailure { error ->
                _getChatsResponse.value = null
                Log.e("GetChats", "Error: ${error.message}", error)
                _getChatsError.value = when (error) {
                    is HttpException -> error.code()
                    else -> -1
                }
            }
        }
    }

    fun reset() {
        _getChatsResponse.value = null
        _getChatsError.value = null
    }
}

