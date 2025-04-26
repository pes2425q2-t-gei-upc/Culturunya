package com.example.culturunya.controllers

import com.example.culturunya.models.Test.Test
import com.example.culturunya.endpoints.events.Event
import kotlin.Result.Companion.failure

class TestRepository (private val api: Api){
    suspend fun getokay() : Result<Test>{
        return try {
            Result.success(api.getokay())
        } catch (e: Exception){
            Result.failure(e)
        }
    }
}
