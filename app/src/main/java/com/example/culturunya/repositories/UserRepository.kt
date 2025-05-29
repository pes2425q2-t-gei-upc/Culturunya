package com.example.culturunya.repositories

import com.example.culturunya.Api
import com.example.culturunya.dataclasses.quiz.SetQuizPointsRequest
import com.example.culturunya.dataclasses.users.UserInfo
import com.example.culturunya.dataclasses.settings.ChangePasswordRequest
import com.example.culturunya.dataclasses.settings.ChangeUsernameRequest
import com.example.culturunya.dataclasses.settings.UpdateLanguageRequest
import retrofit2.HttpException
import okhttp3.MultipartBody

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

    suspend fun changeUsername(token: String, request: ChangeUsernameRequest): Result<Unit> {
        return try {
            val response = api.changeUsername(token, request)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadProfilePic(token: String, profilePic: MultipartBody.Part): Result<Unit> {
        return try {
            val response = api.uploadProfilePic(token, profilePic)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateLangugage(token: String, language: String): Result<Unit> {
        return try {
            val response = api.updateLanguage(token, UpdateLanguageRequest(language))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun setQuizPoints(token: String, points: Int): Result<Unit> {
        return try {
            val response = api.setQuizPoints(token, SetQuizPointsRequest(points))
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Error: ${response.code()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}