package com.example.culturunya.controllers

import com.example.culturunya.endpoints.events.*
import com.example.culturunya.models.currentSession.CurrentSession

class EventsRepository(private val api: Api) {

    suspend fun getEvents(): Result<List<Event>> = runCatching {
        api.getEvents().events
    }

    suspend fun getFilteredEvents(
        categories: List<String>? = null,
        dateStart: String? = null,
        dateEnd: String? = null,
        location: Pair<Double, Double>? = null,
        range: Int? = null
    ): Result<List<Event>> = runCatching {
        if (CurrentSession.token.isBlank()) {
            throw IllegalStateException("No hay token de autenticación disponible")
        }

        api.getFilteredEvents(
            categories = categories?.joinToString(","),
            dateStart = dateStart,
            dateEnd = dateEnd,
            longitude = location?.first,
            latitude = location?.second,
            range = range,
            token = CurrentSession.getAuthHeader()
        ).events
    }

    suspend fun filterByDateRange(
        dateStart: String,
        dateEnd: String
    ): Result<List<Event>> = runCatching {
        getFilteredEvents(
            dateStart = dateStart,
            dateEnd = dateEnd
        ).getOrThrow()
    }

    suspend fun filterByDistanceAndDate(
        dateStart: String,
        dateEnd: String,
        location: Pair<Double, Double>,
        range: Int,
    ): Result<List<Event>> = runCatching {
        getFilteredEvents(
            dateStart = dateStart,
            dateEnd = dateEnd,
            location = location,
            range = range,
        ).getOrThrow()
    }
}