package com.example.culturunya.viewmodels

import androidx.annotation.OptIn
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.example.culturunya.Api
import com.example.culturunya.dataclasses.ratings.Rating
import com.example.culturunya.dataclasses.ratings.RatingRequest
import com.example.culturunya.repositories.RatingsRepository
import com.example.culturunya.dataclasses.users.UserInfo
import kotlinx.coroutines.launch
import java.util.Collections.emptyList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * ViewModel for managing ratings.
 *
 * @property ratingsRepository The repository for fetching and creating ratings.
 */
class RatingViewModel : ViewModel() {
    private val ratingsRepository = RatingsRepository(Api.instance)

    private val _ratings = MutableStateFlow<List<Rating>>(emptyList())
    val ratings: StateFlow<List<Rating>> = _ratings

    private val _rating = MutableStateFlow(Rating(UserInfo("", "", "", "", "", "", "", "", "", "", "", 0, 0,
        banned_from_comments = false,
        is_admin = false
    ), 0, "", ""))
    val rating: StateFlow<Rating> = _rating

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _ratingCreated = MutableStateFlow<Boolean>(false)
    val ratingCreated: StateFlow<Boolean> = _ratingCreated

    /**
     * Fetches ratings for a specific event.
     *
     * @param eventId The ID of the event to fetch ratings for.
     */
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

    /**
     * Fetches a rating by its ID.
     *
     * @param ratingId The ID of the rating to fetch.
     */
    fun fetchRatingById(ratingId: String) {
        viewModelScope.launch {
            ratingsRepository.getRatingById(ratingId).onSuccess {
                _rating.value = it
            }.onFailure {
            }
        }
    }

    /**
     * Creates a new rating.
     *
     * @param user The user creating the rating.
     * @param eventId The ID of the event being rated.
     * @param date The date of the rating.
     * @param rating The rating value.
     * @param comment An optional comment for the rating.
     */
    @OptIn(UnstableApi::class)
    fun createRating(user: UserInfo, eventId: Long, date: String, rating: String, comment: String? = null) {
        val newRating = RatingRequest(eventId, rating, comment)
        Log.d("RatingViewModel", "Creating rating: $newRating") // Add a log

        viewModelScope.launch {
            ratingsRepository.createRating(newRating).onSuccess {
                _rating.value = it
                _ratingCreated.value = true
            }.onFailure {
                _error.value = "Error: ${it.message}"

            }
        }
    }

    /**
     * Deletes a rating by its ID.
     *
     * @param eventId The ID of the rating to delete.
     */
    fun refreshRatingsForEvent(eventId: Long) {
        viewModelScope.launch {
            ratingsRepository.getRatingsForEvent(eventId).onSuccess {
                _ratings.value = it
                _error.value = null
            }.onFailure {
                _error.value = "Error: ${it.message}"
            }
        }
    }
}