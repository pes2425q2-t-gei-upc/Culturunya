package com.example.culturunya

import com.example.culturunya.dataclasses.chargingPoints.ChargingPointItem
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


/**
 * API interface for charging points
 */
interface ChargingApi {

    companion object {
        val instance: ChargingApi = Retrofit.Builder()
            .baseUrl("http://nattech.fib.upc.edu:40502/api_punts_carrega/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(ChargingApi::class.java)
    }

    @GET("punt_mes_proper/")
    suspend fun getNearestChargingPoints(
        @Query("lat") latitude: Double,
        @Query("lng") longitude: Double
    ): List<ChargingPointItem>
}