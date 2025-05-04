package com.example.culturunya.endpoints.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.culturunya.controllers.Api
import com.example.culturunya.controllers.UserRepository
import com.example.culturunya.models.currentSession.CurrentSession
import com.example.culturunya.models.currentSession.CurrentSession.Companion.token
import com.example.culturunya.models.login.LoginRequest
import com.example.culturunya.models.login.LoginResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class LoginViewModel : ViewModel() {
    private val _loginResponse = MutableStateFlow<LoginResponse?>(null)
    val loginResponse: StateFlow<LoginResponse?> = _loginResponse

    private val _loginError = MutableStateFlow<Int?>(null)
    val loginError: StateFlow<Int?> = _loginError

    private val api = Api.instance
    private val repository = UserRepository(api)

    fun login(username: String, password: String) {
        viewModelScope.launch {
            val result = repository.login(LoginRequest(username, password))
            result.onSuccess { body ->
                _loginResponse.value = body
                CurrentSession.getInstance()
                CurrentSession.setUserData(body.token, username, password)
                _loginError.value = null
            }.onFailure { error ->
                _loginResponse.value = null
                _loginError.value = when (error) {
                    is HttpException -> error.code()
                    else -> -1
                }
            }
        }
    }
}
