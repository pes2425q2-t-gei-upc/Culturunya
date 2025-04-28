package com.example.culturunya.endpoints.events

import android.annotation.SuppressLint
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.culturunya.controllers.Api
import com.example.culturunya.controllers.RatingsRepository
import kotlinx.coroutines.launch
import java.util.Collections.emptyList
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class RatingViewModel : ViewModel() {
    private val ratingsRepository = RatingsRepository(Api.instance)

    private val _ratings = MutableStateFlow<List<Rating>>(emptyList())
    val ratings: StateFlow<List<Rating>> = _ratings

    private val _rating = MutableStateFlow(Rating(UserSimpleInfo("", "", ""),0,"","",))
    val rating: StateFlow<Rating> = _rating

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun fetchRatingsForEvent(eventId: Long) {
        viewModelScope.launch {
            ratingsRepository.getRatingsForEvent(eventId).onSuccess {
                _ratings.value = it
                _error.value = null
            }.onFailure {
                _error.value = "Error: ${it.message}"
            }
        }
    }

    fun fetchRatingById(ratingId: String) {
        viewModelScope.launch {
            ratingsRepository.getRatingById(ratingId).onSuccess {
                _rating.value = it
            }.onFailure {
            }
        }
    }
}