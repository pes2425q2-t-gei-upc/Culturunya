package com.example.culturunya.controllers

import com.example.culturunya.endpoints.Test.Test
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import com.example.culturunya.endpoints.events.*
import com.example.culturunya.endpoints.Test.*

interface Api {
    companion object{
        val instance = Retrofit.Builder().baseUrl("http://nattech.fib.upc.edu:40369/api/")
            .addConverterFactory(MoshiConverterFactory.create())
            .client(OkHttpClient.Builder().build()).build().create(Api::class.java)
    }

    @GET("events")
    suspend fun getEvents(): events

    @POST("register")
    //suspend fun register(@Body user: RegisterRequest): Response<RegisterResponse>


    @GET("test")
    suspend fun getokay(): Test
}