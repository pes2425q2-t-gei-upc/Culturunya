package com.example.culturunya.endpoints.deleteAccount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.culturunya.controllers.Api
import com.example.culturunya.controllers.AuthRepository
import com.example.culturunya.models.currentSession.CurrentSession
import com.example.culturunya.models.deleteAccount.DeleteAccountRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class DeleteAccountViewModel : ViewModel() {
    private val _deleteAccountStatus = MutableStateFlow<Int?>(null)
    val deleteAccountStatus: StateFlow<Int?> = _deleteAccountStatus

    private val api = Api.instance
    private val repository = AuthRepository(api)

    fun deleteAccount() {
        viewModelScope.launch {
            val token = CurrentSession.token
            val username = CurrentSession.username
            val result = repository.deleteAccount("Token $token", DeleteAccountRequest(username))

            result.onSuccess {
                _deleteAccountStatus.value = 204
            }.onFailure { error ->
                _deleteAccountStatus.value = when (error) {
                    is HttpException -> error.code()
                    else -> -1
                }
            }
        }
    }
}
