package com.example.culturunya.repositories

import com.example.culturunya.Api
import com.example.culturunya.dataclasses.reports.Report
import com.example.culturunya.dataclasses.reports.ReportRequest
import com.example.culturunya.dataclasses.reports.ResolveRequest
import com.example.culturunya.session.CurrentSession
import retrofit2.HttpException

/**
 * @class ReportsRepository
 * @brief Clase que gestiona la creación de informes sobre valoraciones.
 *
 * Proporciona métodos para informar sobre valoraciones específicas.
 */
class ReportsRepository(private val api: Api) {
    /**
     * @brief Reporta una valoración específica.
     *
     * @param reportRequest Objeto que contiene la información del informe a crear.
     * @return Result<Unit>
     */
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
    suspend fun getReports(): Result<List<Report>> {
        return try{
            val response = api.getReports(token = CurrentSession.getAuthHeader())
            Result.success(response)
        } catch (e: Exception){
            throw e
        }
    }
    suspend fun resolveReport(reportId: String, action: String, message: String): Result<Unit> {
        return try{
            val response = api.resolveReport(token = CurrentSession.getAuthHeader(), report_Id = reportId, resolveRequest = ResolveRequest(action = action, message = message))
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