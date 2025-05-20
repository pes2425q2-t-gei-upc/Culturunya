package com.example.culturunya.repositories

import com.example.culturunya.Api
import com.example.culturunya.dataclasses.ranking.RankingPosition

class LeaderboardRepository(private val api: Api) {
    suspend fun getLeaderboardQuiz(token: String): Result<List<RankingPosition>> {
        return try {
            val response = api.getLeaderboardQuiz(token)
            Result.success(response)
        }
        catch (e: Exception) {
            Result.failure(e)
        }
    }
}