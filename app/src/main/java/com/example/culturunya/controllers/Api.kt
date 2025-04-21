package com.example.culturunya.controllers

import com.example.culturunya.models.RegisterRequest
import com.example.culturunya.models.RegisterResponse
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.example.culturunya.endpoints.events.*
import com.example.culturunya.endpoints.test.Test
import com.example.culturunya.models.changePassword.ChangePasswordRequest
import com.example.culturunya.models.changePassword.ChangePasswordResponse
import com.example.culturunya.models.deleteAccount.DeleteAccountRequest
import com.example.culturunya.models.deleteAccount.DeleteAccountResponse
import com.example.culturunya.models.login.LoginRequest
import com.example.culturunya.models.login.LoginResponse
//import okhttp3.Response
import retrofit2.Response
import retrofit2.http.*

interface Api {
    companion object{
        val instance = Retrofit.Builder().baseUrl("http://nattech.fib.upc.edu:40369/api/")
            .addConverterFactory(MoshiConverterFactory.create())
            .client(OkHttpClient.Builder().build()).build().create(Api::class.java)
    }

    @GET("events/")
    suspend fun getEvents(): Events

    @POST("create_user/")
    suspend fun registerUser(@Body user: RegisterRequest): Response<RegisterResponse>


    @GET("test/")
    suspend fun getokay(): Test

    @POST("login/")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse

    @DELETE("delete_account/")
    suspend fun deleteAccount(@Header("Authorization") token: String): DeleteAccountResponse

    @POST("change-password/")
    suspend fun changePassword(@Header("Authorization") token: String, @Body changePasswordRequest: ChangePasswordRequest): ChangePasswordResponse
}