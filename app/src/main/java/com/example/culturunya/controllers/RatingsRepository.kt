package com.example.culturunya.controllers

import com.example.culturunya.endpoints.ratings.Rating
import com.example.culturunya.endpoints.ratings.RatingRequest
import com.example.culturunya.models.currentSession.CurrentSession


class RatingsRepository(private val api: Api) {

    suspend fun getRatingsForEvent(eventId: Long): Result<List<Rating>>{
        return try {
            val response = api.getRatingsForEvent(eventId)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getRatingById(ratingId: String): Result<Rating> {
        return try {
            val response = api.getRatingById(ratingId)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createRating(rating: RatingRequest): Result<Rating> {
        return try {
            val response = api.postRating(rating, CurrentSession.getAuthHeader())
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
