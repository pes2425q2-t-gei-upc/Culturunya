package com.example.culturunya.endpoints.deleteAccount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.culturunya.controllers.Api
import com.example.culturunya.controllers.AuthRepository
import com.example.culturunya.models.currentSession.CurrentSession
import com.example.culturunya.models.deleteAccount.DeleteAccountRequest
import com.example.culturunya.models.deleteAccount.DeleteAccountResponse
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
                CurrentSession.getInstance()
                val currentToken = CurrentSession.token
                val currentUsername = CurrentSession.username
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
