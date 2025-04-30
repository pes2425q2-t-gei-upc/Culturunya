package com.example.culturunya.controllers

import com.example.culturunya.models.RegisterRequest
import com.example.culturunya.models.RegisterResponse
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.example.culturunya.endpoints.events.*
import com.example.culturunya.endpoints.ratings.Rating
import com.example.culturunya.endpoints.ratings.RatingRequest
import com.example.culturunya.endpoints.test.Test
import com.example.culturunya.endpoints.users.UserSimpleInfo
import com.example.culturunya.models.changePassword.ChangePasswordRequest
import com.example.culturunya.models.changePassword.ChangePasswordResponse
import com.example.culturunya.models.deleteAccount.DeleteAccountResponse
import com.example.culturunya.models.login.LoginRequest
import com.example.culturunya.models.login.LoginResponse
import retrofit2.Response
import retrofit2.http.*

interface Api {
    companion object{
        val instance = Retrofit.Builder().baseUrl("http://nattech.fib.upc.edu:40369/api/")
            .addConverterFactory(MoshiConverterFactory.create())
            .client(OkHttpClient.Builder().build()).build().create(Api::class.java)
    }

    @GET("events/")
    suspend fun getEvents(): Events

    @GET("events/filter/")
    suspend fun getFilteredEvents(
        @Query("categories") categories: String? = null,
        @Query("date_start_range") dateStart: String? = null,
        @Query("date_end_range") dateEnd: String? = null,
        @Query("longitude") longitude: Double? = null,
        @Query("latitude") latitude: Double? = null,
        @Query("range") range: Int? = null,
        @Header("Authorization") token: String
    ): Events

    @POST("create_user/")
    suspend fun registerUser(@Body user: RegisterRequest): Response<RegisterResponse>

    @GET("test/")
    suspend fun getokay(): Test

    @POST("login/")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse

    @DELETE("delete_account/")
    suspend fun deleteAccount(@Header("Authorization") token: String): DeleteAccountResponse

    @PUT("user/change_password/")
    suspend fun changePassword(@Header("Authorization") token: String, @Body newPassword: ChangePasswordRequest): ChangePasswordResponse

    @GET("ratings/{event_id}/")
    suspend fun getRatingsForEvent(@Path("event_id") eventId: Long, @Header("Authorization") token: String? = null): List<Rating>

    @GET("ratings/{ratingId}")
    suspend fun getRatingById(@Path("ratingId") ratingId: String, @Header("Authorization") token: String? = null): Rating

    @POST("ratings/create/")
    suspend fun postRating(@Body rating: RatingRequest, @Header("Authorization") token: String): Rating

    @GET("user/profile_info")
    suspend fun getProfileInfo(@Header("Authorization") token: String): UserSimpleInfo
}