package com.example.culturunya.endpoints.logout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.culturunya.controllers.Api
import com.example.culturunya.controllers.UserRepository
import com.example.culturunya.models.currentSession.CurrentSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class LogoutViewModel : ViewModel() {
    private val _logoutStatus = MutableStateFlow<Int?>(null)
    val logoutStatus: StateFlow<Int?> = _logoutStatus

    private val api = Api.instance
    private val repository = UserRepository(api)

    fun logout() {
        viewModelScope.launch {
            val token = CurrentSession.token
            val result = repository.logout("Token $token")

            result.onSuccess {
                _logoutStatus.value = 200
            }.onFailure { error ->
                _logoutStatus.value = when (error) {
                    is HttpException -> error.code()
                    else -> -1
                }
            }
        }
    }
}