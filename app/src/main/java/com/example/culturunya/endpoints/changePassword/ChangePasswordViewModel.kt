package com.example.culturunya.endpoints.changePassword

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.culturunya.controllers.Api
import com.example.culturunya.controllers.AuthRepository
import com.example.culturunya.models.changePassword.ChangePasswordRequest
import com.example.culturunya.models.changePassword.ChangePasswordResponse
import com.example.culturunya.models.currentSession.CurrentSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChangePasswordViewModel: ViewModel() {


    private val _changePasswordResponse = MutableStateFlow<ChangePasswordResponse?>(null)
    val changePasswordResponse: StateFlow<ChangePasswordResponse?> = _changePasswordResponse

    private val _changePasswordError = MutableStateFlow<String?>(null)
    val changePasswordError: StateFlow<String?> = _changePasswordError

    private val api = Api.instance
    private val repository = AuthRepository(api)

    fun changePassword(oldPassword: String, newPassword: String) {
        viewModelScope.launch {
            try {
                CurrentSession.getInstance()
                val currentToken = CurrentSession.token
                val response = repository.changePassword("Token $currentToken", ChangePasswordRequest(oldPassword, newPassword))
                _changePasswordResponse.value = response
                _changePasswordError.value = null
            } catch (e: Exception) {
                _changePasswordError.value = e.message ?: "Error desconocido"
                _changePasswordResponse.value = null
            }
        }
    }
}