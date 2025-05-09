package com.example.culturunya.repositories

import com.example.culturunya.Api
import com.example.culturunya.dataclasses.ratings.Rating
import com.example.culturunya.dataclasses.ratings.RatingRequest
import com.example.culturunya.CurrentSession


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
