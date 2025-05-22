package com.example.culturunya.repositories

import com.example.culturunya.Api
import com.example.culturunya.session.CurrentSession
import com.example.culturunya.dataclasses.ratings.ReportRequest
import retrofit2.HttpException

class ReportsRepository(private val api: Api) {
    suspend fun reportRating(reportRequest: ReportRequest): Result<Unit> {
        return try{
            val response = api.reportRating(token = CurrentSession.getAuthHeader(), reportRequest = reportRequest)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: Exception){
            throw e
        }
    }
}