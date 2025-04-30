package com.example.culturunya.endpoints.ratings

import androidx.annotation.OptIn
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.example.culturunya.controllers.Api
import com.example.culturunya.controllers.RatingsRepository
import kotlinx.coroutines.launch
import java.util.Collections.emptyList
import com.example.culturunya.endpoints.users.UserSimpleInfo
import com.example.culturunya.models.currentSession.CurrentSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class RatingViewModel : ViewModel() {
    private val ratingsRepository = RatingsRepository(Api.instance)

    private val _ratings = MutableStateFlow<List<Rating>>(emptyList())
    val ratings: StateFlow<List<Rating>> = _ratings

    private val _rating = MutableStateFlow(Rating(UserSimpleInfo("", "", ""), 0, "", ""))
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

    @OptIn(UnstableApi::class)
    fun createRating(user: UserSimpleInfo, eventId: Long, date: String, rating: String, comment: String? = null) {
        val newRating = RatingRequest(eventId, rating, comment)
        Log.d("RatingViewModel", "Creating rating: $newRating") // Add a log

        viewModelScope.launch {
            ratingsRepository.createRating(newRating).onSuccess {
                _rating.value = it
            }.onFailure {
                _error.value = "Error: ${it.message}"

            }
        }
    }
}