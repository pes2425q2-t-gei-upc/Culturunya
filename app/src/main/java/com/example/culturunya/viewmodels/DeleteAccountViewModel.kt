package com.example.culturunya.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.culturunya.Api
import com.example.culturunya.repositories.UserRepository
import com.example.culturunya.session.CurrentSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

/**
 * ViewModel for handling the delete account functionality.
 * It manages the state of the account deletion process.
 */
class DeleteAccountViewModel : ViewModel() {
    private val _deleteAccountStatus = MutableStateFlow<Int?>(null)
    val deleteAccountStatus: StateFlow<Int?> = _deleteAccountStatus

    private val api = Api.instance
    private val repository = UserRepository(api)

    /**
     * Deletes the user account by making a network request.
     * It updates the state based on the result of the request.
     */
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
