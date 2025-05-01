package com.example.culturunya.endpoints.changePassword

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.culturunya.controllers.Api
import com.example.culturunya.controllers.AuthRepository
import com.example.culturunya.models.changePassword.ChangePasswordRequest
import com.example.culturunya.models.currentSession.CurrentSession
import com.example.culturunya.models.deleteAccount.DeleteAccountRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class ChangePasswordViewModel: ViewModel() {
    private val _changePasswordStatus = MutableStateFlow<Int?>(null)
    val changePasswordStatus: StateFlow<Int?> = _changePasswordStatus

    private val api = Api.instance
    private val repository = AuthRepository(api)

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