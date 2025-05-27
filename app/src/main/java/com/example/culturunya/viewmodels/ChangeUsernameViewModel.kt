package com.example.culturunya.viewmodels

import SessionManager
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.culturunya.Api
import com.example.culturunya.repositories.UserRepository
import com.example.culturunya.dataclasses.settings.ChangeUsernameRequest
import com.example.culturunya.session.CurrentSession
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

class ChangeUsernameViewModel(application: Application) : AndroidViewModel(application) {
    private val _state = MutableStateFlow(ChangeUsernameState())
    val state: StateFlow<ChangeUsernameState> = _state

    private val repository = UserRepository(Api.instance)
    private val sessionManager = SessionManager(application.applicationContext)

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
                    val userInfo = repository.getProfileInfo("Token $currentToken")
                    if (userInfo.profile_pic != null) {
                        CurrentSession.setUserData(userInfo.username, userInfo.email, userInfo.profile_pic, userInfo.language, userInfo.rank_quiz, userInfo.rank_event, userInfo.total_quiz_points, userInfo.total_event_points,userInfo.is_admin)
                        CurrentSession.saveToDataStore(sessionManager)
                    }
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