package com.example.culturunya.controllers

import retrofit2.Response
import com.example.culturunya.models.RegisterRequest
import com.example.culturunya.models.RegisterResponse
import com.example.culturunya.models.changePassword.ChangePasswordRequest
import com.example.culturunya.models.changePassword.ChangePasswordResponse
import com.example.culturunya.models.deleteAccount.DeleteAccountRequest
import com.example.culturunya.models.deleteAccount.DeleteAccountResponse
import com.example.culturunya.models.login.LoginRequest
import com.example.culturunya.models.login.LoginResponse

class AuthRepository(private val api: Api) {
    suspend fun registerUser(username: String, email: String, password: String): Response<RegisterResponse> {
        return api.registerUser(RegisterRequest(username, email, password))
    }

    suspend fun login(request: LoginRequest): LoginResponse {
        return api.login(request)
    }

    suspend fun deleteAccount(token: String, request: DeleteAccountRequest): DeleteAccountResponse {
        return api.deleteAccount(token)
    }

    suspend fun changePassword(token: String, request: ChangePasswordRequest): ChangePasswordResponse {
        return api.changePassword(token, request)
    }
}