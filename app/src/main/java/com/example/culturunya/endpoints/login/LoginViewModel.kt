package com.example.culturunya.endpoints.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.culturunya.controllers.Api
import com.example.culturunya.controllers.UserRepository
import com.example.culturunya.models.currentSession.CurrentSession
import com.example.culturunya.models.login.LoginRequest
import com.example.culturunya.models.login.LoginResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialResponse
import com.example.culturunya.controllers.AuthRepository
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

class LoginViewModel : ViewModel() {
    private val _loginResponse = MutableStateFlow<LoginResponse?>(null)
    val loginResponse: StateFlow<LoginResponse?> = _loginResponse

    private val _loginError = MutableStateFlow<Int?>(null)
    val loginError: StateFlow<Int?> = _loginError

    private val _googleLoginError = MutableStateFlow<String?>(null)
    val googleLoginError: StateFlow<String?> = _googleLoginError

    private val api = Api.instance
    private val repository = AuthRepository(api)
    private lateinit var credentialManager: CredentialManager
    private val WEB_CLIENT_ID = "102065557294-a14162ejafi97l4a776ftomhrguon2rv.apps.googleusercontent.com"

    fun initialize(context: Context) {
        Log.d("LoginViewModel", "Inicializando CredentialManager")
        credentialManager = CredentialManager.create(context)
    }

    fun login(username: String, password: String) {
        Log.d("LoginViewModel", "Intentando iniciar sesión con usuario: $username")
        viewModelScope.launch {
            val result = repository.login(LoginRequest(username, password))

            result.onSuccess { body ->
                Log.d("LoginViewModel", "Inicio de sesión exitoso. Token recibido: ${body.token}")
                Log.d("LoginViewModel", "Inicio de sesión exitoso. Password recibido: $password")
                _loginResponse.value = body
                CurrentSession.getInstance()
                CurrentSession.setTokenAndPassword(body.token, password)
                _loginError.value = null
            }.onFailure { error ->
                Log.e("LoginViewModel", "Error en login: ${error.message}")
                _loginResponse.value = null
                _loginError.value = when (error) {
                    is HttpException -> {
                        Log.e("LoginViewModel", "HttpException: código ${error.code()}")
                        error.code()
                    }
                    else -> {
                        Log.e("LoginViewModel", "Error desconocido")
                        -1
                    }
                }
            }
        }
    }

    fun signInWithGoogle(context: Context) {
        Log.d("LoginViewModel", "Iniciando login con Google")
        viewModelScope.launch {
            try {
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(WEB_CLIENT_ID)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val result = credentialManager.getCredential(
                    context = context,
                    request = request
                )
                Log.d("LoginViewModel", "Credenciales de Google obtenidas")
                handleGoogleSignIn(result)
            } catch (e: GetCredentialException) {
                Log.e("LoginViewModel", "Error obteniendo credenciales de Google: ${e.message}")
                _googleLoginError.value = "Google Sign-In failed: ${e.message}"
            }
        }
    }

    private fun handleGoogleSignIn(result: GetCredentialResponse) {
        Log.d("LoginViewModel", "Procesando credenciales de Google")
        viewModelScope.launch {
            try {
                val credential = result.credential
                if (credential is CustomCredential &&
                    credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                ) {
                    val googleIdTokenCredential = GoogleIdTokenCredential
                        .createFrom(credential.data)

                    val idToken = googleIdTokenCredential.idToken
                    Log.d("LoginViewModel", "ID Token de Google obtenido: $idToken")

                    CurrentSession.getInstance()
                    CurrentSession.setGoogleToken(idToken)

                    val loginResult = repository.googleLogin(idToken)

                    loginResult.onSuccess { response ->
                        Log.d("LoginViewModel", "Login con Google exitoso. Token: ${response.token}")
                        _loginResponse.value = response
                        CurrentSession.setTokenAndPassword(response.token, "")
                        CurrentSession.setUserData(googleIdTokenCredential.displayName ?: "", "", "")
                        _googleLoginError.value = null
                    }.onFailure { error ->
                        Log.e("LoginViewModel", "Error en login con Google: ${error.message}")
                        _googleLoginError.value = when (error) {
                            is HttpException -> "Server error: ${error.code()}"
                            else -> "Error processing Google login: ${error.message}"
                        }
                    }
                } else {
                    Log.e("LoginViewModel", "Credencial recibida no es del tipo esperado")
                }
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Excepción al procesar token de Google: ${e.message}")
                _googleLoginError.value = "Error processing Google token: ${e.message}"
            }
        }
    }
}
