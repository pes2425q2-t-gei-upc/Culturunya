package com.example.culturunya.repositories

import com.example.culturunya.Api
import com.example.culturunya.dataclasses.ratings.Rating
import com.example.culturunya.dataclasses.ratings.RatingRequest
import com.example.culturunya.session.CurrentSession

/**
 * @class RatingsRepository
 * @brief Clase que gestiona la obtención y creación de valoraciones.
 *
 * Proporciona métodos para obtener valoraciones de eventos y crear nuevas valoraciones.
 */
class RatingsRepository(private val api: Api) {

    /**
     * @brief Obtiene la lista de valoraciones para un evento específico.
     *
     * @param eventId ID del evento para el que se desean obtener las valoraciones.
     * @return Result<List<Rating>>
     */
    suspend fun getRatingsForEvent(eventId: Long): Result<List<Rating>>{
        return try {
            val response = api.getRatingsForEvent(eventId)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * @brief Obtiene una valoración específica por su ID.
     *
     * @param ratingId ID de la valoración que se desea obtener.
     * @return Result<Rating>
     */
    suspend fun getRatingById(ratingId: String): Result<Rating> {
        return try {
            val response = api.getRatingById(ratingId)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * @brief Crea una nueva valoración.
     *
     * @param rating Objeto que contiene la información de la valoración a crear.
     * @return Result<Rating>
     */
    suspend fun createRating(rating: RatingRequest): Result<Rating> {
        return try {
            val response = api.postRating(rating, CurrentSession.getAuthHeader())
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
