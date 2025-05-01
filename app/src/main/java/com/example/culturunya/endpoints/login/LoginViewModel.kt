package com.example.culturunya.endpoints.login

import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.culturunya.R
import com.example.culturunya.controllers.Api
import com.example.culturunya.controllers.AuthRepository
import com.example.culturunya.models.currentSession.CurrentSession
import com.example.culturunya.models.login.LoginRequest
import com.example.culturunya.models.login.LoginResponse
import com.example.culturunya.screens.getString
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

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
                CurrentSession.getInstance()
                CurrentSession.setUserData(response.token, username, password)
                Log.d("Token: ", "$response.token")
                _loginError.value = null
            } catch (e: HttpException) {
                _loginResponse.value = null
                _loginError.value = when (e.code()) {
                    400 -> "400"
                    401 -> "401"
                    500 -> "500"
                    else -> "Error: ${e.code()} ${e.message()}"
                }
            } catch (e: Exception) {
                // Otros errores como problemas de red
                _loginResponse.value = null
                _loginError.value = "Connection error"
            }
        }
    }
}
