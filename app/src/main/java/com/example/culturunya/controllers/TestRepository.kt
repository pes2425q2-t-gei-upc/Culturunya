package com.example.culturunya.controllers

import com.example.culturunya.endpoints.test.Test

class TestRepository (private val api: Api){
    suspend fun getokay() : Result<Test>{
        val response = api.getokay()
        return try {
            Result.success(response)
        } catch (e: Exception){
            Result.failure(e)
        }
    }
}