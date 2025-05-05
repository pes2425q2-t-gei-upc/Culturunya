package com.example.culturunya.controllers

import com.example.culturunya.endpoints.users.UserInfo
import com.example.culturunya.models.changePassword.ChangePasswordRequest
import com.example.culturunya.models.deleteAccount.DeleteAccountRequest
import com.example.culturunya.models.login.LoginRequest
import com.example.culturunya.models.login.LoginResponse
import retrofit2.HttpException

class UserRepository(private val api: Api) {
    suspend fun getProfileInfo(token: String): UserInfo {
        return try{
            api.getProfileInfo(token)
        } catch (e: Exception){
            throw e
        }
    }

    suspend fun logout(token: String): Result<Unit> {
        return try {
            val response = api.logout(token)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteAccount(token: String): Result<Unit> {
        return try {
            val response = api.deleteAccount(token)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun changePassword(token: String, request: ChangePasswordRequest): Result<Unit> {
        return try {
            val response = api.changePassword(token, request)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}