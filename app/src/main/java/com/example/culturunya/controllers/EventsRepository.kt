package com.example.culturunya.controllers

import com.example.culturunya.models.events.Event

class EventsRepository (private val api: Api) {
    suspend fun getEvents() : List<Event>{
        return api.getEvents().events
    }
}