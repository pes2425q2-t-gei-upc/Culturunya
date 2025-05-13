package com.example.culturunya.endpoints.changeUsername

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.culturunya.controllers.Api
import com.example.culturunya.controllers.UserRepository
import com.example.culturunya.models.changeUsername.ChangeUsernameRequest
import com.example.culturunya.models.currentSession.CurrentSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

data class ChangeUsernameState(
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val error: String? = null,
    val newUsername: String = ""
)

class ChangeUsernameViewModel: ViewModel() {
    private val _state = MutableStateFlow(ChangeUsernameState())
    val state: StateFlow<ChangeUsernameState> = _state

    private val repository = UserRepository(Api.instance)

    fun updateNewUsername(username: String) {
        _state.value = _state.value.copy(newUsername = username)
    }

    fun changeUsername() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val currentToken = CurrentSession.token
                val result = repository.changeUsername("Token $currentToken", ChangeUsernameRequest(_state.value.newUsername))

                result.onSuccess {
                    _state.value = _state.value.copy(isLoading = false, success = true)
                }.onFailure { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = when (error) {
                            is HttpException -> "Error: ${error.code()}"
                            else -> "Error desconegut: ${error.message}"
                        }
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Error desconegut: ${e.message}"
                )
            }
        }
    }
} 