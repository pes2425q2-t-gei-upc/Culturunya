package com.example.culturunya.endpoints.deleteAccount

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.culturunya.controllers.Api
import com.example.culturunya.controllers.AuthRepository
import com.example.culturunya.models.currentUser.User
import com.example.culturunya.models.deleteAccount.DeleteAccountRequest
import com.example.culturunya.models.deleteAccount.DeleteAccountResponse
import com.example.culturunya.models.login.LoginRequest
import com.example.culturunya.models.login.LoginResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DeleteAccountViewModel : ViewModel() {
    private val _deleteAccountResponse = MutableStateFlow<DeleteAccountResponse?>(null)
    val deleteAccountResponse: StateFlow<DeleteAccountResponse?> = _deleteAccountResponse

    private val _deleteAccountError = MutableStateFlow<String?>(null)
    val deleteAccountError: StateFlow<String?> = _deleteAccountError

    private val api = Api.instance
    private val repository = AuthRepository(api)

    fun deleteAccount() {
        viewModelScope.launch {
            try {
                User.getInstance()
                val currentToken = User.token
                //Log.d("tokenFinal", "Este es el token: $currentToken")
                val currentUsername = User.username
                val response = repository.deleteAccount("Token $currentToken", DeleteAccountRequest(currentUsername))
                _deleteAccountResponse.value = response
                _deleteAccountError.value = null
            } catch (e: Exception) {
                _deleteAccountError.value = e.message ?: "Error desconocido"
                _deleteAccountResponse.value = null
            }
        }
    }
}
