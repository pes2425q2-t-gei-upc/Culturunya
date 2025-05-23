package com.example.culturunya.repositories

import com.example.culturunya.Api
import com.example.culturunya.dataclasses.events.Event
import com.example.culturunya.session.CurrentSession

/**
 * @class EventsRepository
 * @brief Clase que gestiona la obtención de eventos.
 *
 * Proporciona métodos para obtener eventos filtrados por diferentes criterios.
 */
class EventsRepository(private val api: Api) {

    /**
     * @brief Obtiene la lista de eventos.
     *
     * @return Result<List<Event>>
     */
    suspend fun getEvents(): Result<List<Event>> = runCatching {
        api.getEvents().events
    }

    /**
     * @brief Obtiene eventos filtrados por categorías, fechas y ubicación.
     *
     * @param categories Lista de categorías para filtrar los eventos.
     * @param dateStart Fecha de inicio para filtrar los eventos.
     * @param dateEnd Fecha de fin para filtrar los eventos.
     * @param location Ubicación (latitud y longitud) para filtrar los eventos.
     * @param range Rango de distancia en metros para filtrar los eventos.
     * @return Result<List<Event>>
     */
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

    /**
     * @brief Filtra eventos por fechas.
     *
     * @param dateStart Fecha de inicio para filtrar los eventos.
     * @param dateEnd Fecha de fin para filtrar los eventos.
     * @return Result<List<Event>>
     */
    suspend fun filterByDateRange(
        dateStart: String,
        dateEnd: String
    ): Result<List<Event>> = runCatching {
        getFilteredEvents(
            dateStart = dateStart,
            dateEnd = dateEnd
        ).getOrThrow()
    }

    /**
     * @brief Filtra eventos por distancia y fechas.
     *
     * @param dateStart Fecha de inicio para filtrar los eventos.
     * @param dateEnd Fecha de fin para filtrar los eventos.
     * @param location Ubicación (latitud y longitud) para filtrar los eventos.
     * @param range Rango de distancia en metros para filtrar los eventos.
     * @return Result<List<Event>>
     */
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