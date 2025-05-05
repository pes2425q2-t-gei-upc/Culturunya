package com.example.culturunya.endpoints.deleteAccount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.culturunya.controllers.Api
import com.example.culturunya.controllers.UserRepository
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
    private val repository = UserRepository(api)

    fun deleteAccount() {
        viewModelScope.launch {
            val token = CurrentSession.token
            val result = repository.deleteAccount("Token $token")

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
