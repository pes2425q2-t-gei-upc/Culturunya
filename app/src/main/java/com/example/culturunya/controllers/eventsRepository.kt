package com.example.culturunya.controllers

import com.example.culturunya.models.events.*

class eventsRepository (private val api: Api) {
    suspend fun getEvents() : Result<List<Event>>{
        return try {
            Result.success(api.getEvents().events)
        } catch (e: Exception){
            Result.failure(e)
        }
    }
}