package com.example.culturunya.repositories

import com.example.culturunya.Api
import com.example.culturunya.dataclasses.events.Event
import com.example.culturunya.CurrentSession

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