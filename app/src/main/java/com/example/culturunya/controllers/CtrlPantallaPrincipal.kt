package com.example.culturunya.controllers

import com.example.culturunya.endpoints.events.*

suspend fun getCleanEvents(): List<Event> {
    var api = Api.instance
    val events = api.getEvents()
    return events.events
}