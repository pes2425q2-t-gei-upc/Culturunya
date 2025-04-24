package com.example.culturunya.endpoints.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.culturunya.controllers.*

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

    init {
        loadAllEvents()
    }

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
}