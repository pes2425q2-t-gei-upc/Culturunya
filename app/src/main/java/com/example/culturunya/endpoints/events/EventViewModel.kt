package com.example.culturunya.endpoints.events

import androidx.activity.result.launch
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.culturunya.controllers.*
import com.example.culturunya.models.events.Event

class EventViewModel: ViewModel() {
    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events
    var api = Api.instance
    private val repository = EventsRepository(api)

    init {
        viewModelScope.launch {
            _events.value = repository.getEvents()
        }
    }
}