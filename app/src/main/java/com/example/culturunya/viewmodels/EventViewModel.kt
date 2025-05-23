package com.example.culturunya.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.culturunya.Api
import com.example.culturunya.dataclasses.events.Event
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.culturunya.repositories.EventsRepository

/**
 * ViewModel for handling events-related operations.
 * It manages the state of the events list, loading status, and error messages.
 */
class EventViewModel: ViewModel() {
    private val _allEvents = MutableStateFlow<List<Event>>(emptyList())
    val allEvents: StateFlow<List<Event>> = _allEvents

    private val _filteredEvents = MutableStateFlow<List<Event>>(emptyList())
    val filteredEvents: StateFlow<List<Event>> = _filteredEvents

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val api = Api.instance
    private val repository = EventsRepository(api)

    private val _filteredEventsByDistanceAndDate = MutableStateFlow<List<Event>>(emptyList())
    val filteredEventsByDistanceAndDate: StateFlow<List<Event>> = _filteredEventsByDistanceAndDate

    /**
     * Initializes the ViewModel and loads all events.
     */
    init {
        loadAllEvents()
    }

    /**
     * Loads all events from the repository.
     * It updates the loading status and handles any errors that occur during the process.
     */
    fun loadAllEvents() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _allEvents.value = repository.getEvents().getOrThrow()
                _filteredEvents.value = _allEvents.value // Inicialmente, mostrar todos los eventos
            } catch (e: Exception) {
                _error.value = "Error al cargar eventos: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Filters events by a specific date range.
     * @param dateStart The start date of the range.
     * @param dateEnd The end date of the range.
     */
    fun filterEventsByDate(dateStart: String, dateEnd: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.filterByDateRange(dateStart, dateEnd)
                .onSuccess {
                    _filteredEvents.value = it
                }
                .onFailure { e ->
                    _error.value = when {
                        e is IllegalStateException -> "Por favor inicie sesión"
                        e is java.net.HttpRetryException && e.responseCode() == 401 ->
                            "Sesión expirada. Vuelva a iniciar sesión"
                        else -> "Error al filtrar eventos: ${e.message}"
                    }
                }
            _isLoading.value = false
        }
    }

    /**
     * Filters events by a specific date range and location.
     * @param dateStart The start date of the range.
     * @param dateEnd The end date of the range.
     * @param location The location coordinates (latitude, longitude).
     * @param range The distance range in meters.
     */
    fun filterEventsByRangeAndDate(
        dateStart: String,
        dateEnd: String,
        location: Pair<Double, Double>,
        range: Int
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.filterByDistanceAndDate(dateStart, dateEnd, location, range)
                .onSuccess {
                    _filteredEventsByDistanceAndDate.value = it
                }
                .onFailure { e ->
                    _error.value = when {
                        e is IllegalStateException -> "Por favor inicie sesión"
                        e is java.net.HttpRetryException && e.responseCode() == 401 ->
                            "Sesión expirada. Vuelva a iniciar sesión"
                        else -> "Error al filtrar eventos: ${e.message}"
                    }
                }
            _isLoading.value = false
        }
    }
}