package com.example.culturunya.endpoints.changePassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.culturunya.controllers.Api
import com.example.culturunya.controllers.UserRepository
import com.example.culturunya.models.changePassword.ChangePasswordRequest
import com.example.culturunya.models.currentSession.CurrentSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class ChangePasswordViewModel: ViewModel() {
    private val _changePasswordStatus = MutableStateFlow<Int?>(null)
    val changePasswordStatus: StateFlow<Int?> = _changePasswordStatus

    private val api = Api.instance
    private val repository = UserRepository(api)

    fun changePassword(oldPassword: String, newPassword: String) {
        viewModelScope.launch {
            val currentToken = CurrentSession.token
            val result = repository.changePassword("Token $currentToken", ChangePasswordRequest(oldPassword, newPassword))

            result.onSuccess {
                _changePasswordStatus.value = 200
            }.onFailure { error ->
                _changePasswordStatus.value = when (error) {
                    is HttpException -> error.code()
                    else -> -1
                }
            }
        }
    }
}