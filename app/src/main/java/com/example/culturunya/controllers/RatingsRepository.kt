package com.example.culturunya.controllers

import com.example.culturunya.endpoints.events.*


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

    suspend fun postRating(rating: Rating): Result<Rating> {
        return try {
            val response = api.postRating(rating)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
