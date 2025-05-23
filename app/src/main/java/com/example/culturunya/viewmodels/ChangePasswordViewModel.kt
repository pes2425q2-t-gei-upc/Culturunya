package com.example.culturunya.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.culturunya.Api
import com.example.culturunya.repositories.UserRepository
import com.example.culturunya.dataclasses.settings.ChangePasswordRequest
import com.example.culturunya.session.CurrentSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

/**
 * ViewModel para manejar el proceso de cambio de contraseña.
 */
class ChangePasswordViewModel: ViewModel() {
    private val _changePasswordStatus = MutableStateFlow<Int?>(null)
    val changePasswordStatus: StateFlow<Int?> = _changePasswordStatus

    private val api = Api.instance
    private val repository = UserRepository(api)

    /**
     * Cambia la contraseña del usuario.
     * @param oldPassword Contraseña actual
     * @param newPassword Nueva contraseña
     */
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