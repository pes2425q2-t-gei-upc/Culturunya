package com.example.culturunya.controllers

import com.example.culturunya.models.register.RegisterRequest
import com.example.culturunya.models.register.RegisterResponse
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.example.culturunya.endpoints.events.*
import com.example.culturunya.endpoints.login.GoogleTokenRequest
import com.example.culturunya.endpoints.ratings.Rating
import com.example.culturunya.endpoints.ratings.RatingRequest
import com.example.culturunya.endpoints.users.UserInfo
import com.example.culturunya.models.Message
import com.example.culturunya.models.changePassword.ChangePasswordRequest
import com.example.culturunya.models.events.Events
import com.example.culturunya.models.getChats.ChatInfo
import com.example.culturunya.models.login.LoginRequest
import com.example.culturunya.models.login.LoginResponse
import com.example.culturunya.models.sendMessage.SendMessageToAdminRequest
import com.example.culturunya.models.sendMessage.SendMessageToUserRequest
import retrofit2.Response
import retrofit2.http.*

interface Api {
    companion object{
        val instance: Api = Retrofit.Builder().baseUrl("http://nattech.fib.upc.edu:40369/api/")
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

    @POST("login/")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse

    @DELETE("delete_account/")
    suspend fun deleteAccount(@Header("Authorization") token: String): Response<Unit>

    @PUT("user/change_password/")
    suspend fun changePassword(@Header("Authorization") token: String, @Body newPassword: ChangePasswordRequest): Response<Unit>

    @GET("chat/admin_chats/")
    suspend fun getChats(@Header("Authorization") token: String): List<ChatInfo>

    @GET("chat/with_admin/")
    suspend fun getChatWithAdmin(@Header("Authorization") token: String): List<Message>

    @GET("chat/with_user/{user_id}")
    suspend fun getChatWithUser(@Header("Authorization") token: String, @Path("user_id") user_id: String): List<Message>

    @POST("chat/send_to_admin/")
    suspend fun sendMessageToAdmin(@Header("Authorization") token: String, @Body sendMessageRequest: SendMessageToAdminRequest): Response<Unit>

    @POST("chat/send_to_user/")
    suspend fun sendMessageToUser(@Header("Authorization") token: String, @Body sendMessageRequest: SendMessageToUserRequest): Response<Unit>

    @GET("ratings/{event_id}/")
    suspend fun getRatingsForEvent(@Path("event_id") eventId: Long, @Header("Authorization") token: String? = null): List<Rating>

    @GET("ratings/{ratingId}")
    suspend fun getRatingById(@Path("ratingId") ratingId: String, @Header("Authorization") token: String? = null): Rating

    @POST("ratings/create/")
    suspend fun postRating(@Body rating: RatingRequest, @Header("Authorization") token: String): Rating

    @GET("user/profile_info")
    suspend fun getProfileInfo(@Header("Authorization") token: String): UserInfo

    @POST("auth/google/")
    suspend fun loginGoogle(@Body id_token: GoogleTokenRequest): LoginResponse

    @POST("logout/")
    suspend fun logout(@Header("Authorization") token: String): Response<Unit>
}