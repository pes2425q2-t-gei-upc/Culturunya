package com.example.culturunya.endpoints.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.culturunya.controllers.Api
import com.example.culturunya.controllers.AuthRepository
import com.example.culturunya.models.currentUser.User
import com.example.culturunya.models.login.LoginRequest
import com.example.culturunya.models.login.LoginResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val _loginResponse = MutableStateFlow<LoginResponse?>(null)
    val loginResponse: StateFlow<LoginResponse?> = _loginResponse

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError

    private val api = Api.instance
    private val repository = AuthRepository(api)

    fun login(username: String, password: String) {
        viewModelScope.launch {
            try {
                var response = repository.login(LoginRequest(username, password))
                _loginResponse.value = response
                User.getInstance()
                User.setUserData(response.token, username, password)
                _loginError.value = null
            } catch (e: Exception) {
                _loginError.value = e.message ?: "Error desconocido"
                _loginResponse.value = null
            }
        }
    }
}
