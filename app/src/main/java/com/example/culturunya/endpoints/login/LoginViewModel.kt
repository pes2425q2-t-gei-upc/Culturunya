package com.example.culturunya.endpoints.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.culturunya.controllers.Api
import com.example.culturunya.controllers.AuthRepository
import com.example.culturunya.models.currentSession.CurrentSession
import com.example.culturunya.models.login.LoginRequest
import com.example.culturunya.models.login.LoginResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import android.content.Context
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialResponse
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
    private val WEB_CLIENT_ID = "128086553198-dgd6f6ctoubddlmnvod4ib8ad10ubonc.apps.googleusercontent.com"

    fun initialize(context: Context) {
        credentialManager = CredentialManager.create(context)
    }

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

    fun signInWithGoogle(context: Context) {
        viewModelScope.launch {
            try {
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setServerClientId(WEB_CLIENT_ID)
                    .setFilterByAuthorizedAccounts(false)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val result = credentialManager.getCredential(
                    context = context,
                    request = request
                )
                handleGoogleSignIn(result)
            } catch (e: GetCredentialException) {
                _googleLoginError.value = "Google Sign-In failed: ${e.message}"
            }
        }
    }

    private fun handleGoogleSignIn(result: GetCredentialResponse) {
        viewModelScope.launch {
            try {
                val credential = result.credential
                if (credential is CustomCredential &&
                    credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                ) {
                    val googleIdTokenCredential = GoogleIdTokenCredential
                        .createFrom(credential.data)

                    CurrentSession.getInstance()
                    CurrentSession.setGoogleToken(googleIdTokenCredential.idToken)

                    val backendResponse = AuthRepository.googleLogin(googleIdTokenCredential.idToken)

                    _loginResponse.value = backendResponse
                    _googleLoginError.value = null
                }
            } catch (e: Exception) {
                _googleLoginError.value = when (e) {
                    is HttpException -> "Server error: ${e.code()}"
                    else -> "Error processing Google token: ${e.message}"
                }
            }
        }
    }
}