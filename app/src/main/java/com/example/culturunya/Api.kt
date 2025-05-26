package com.example.culturunya

import com.example.culturunya.dataclasses.register.RegisterRequest
import com.example.culturunya.dataclasses.register.RegisterResponse
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.example.culturunya.dataclasses.login.GoogleTokenRequest
import com.example.culturunya.dataclasses.ratings.Rating
import com.example.culturunya.dataclasses.ratings.RatingRequest
import com.example.culturunya.dataclasses.users.UserInfo
import com.example.culturunya.dataclasses.chats.Message
import com.example.culturunya.dataclasses.settings.ChangePasswordRequest
import com.example.culturunya.dataclasses.settings.ChangeUsernameRequest
import com.example.culturunya.dataclasses.events.EventList
import com.example.culturunya.dataclasses.chats.ChatInfo
import com.example.culturunya.dataclasses.login.LoginRequest
import com.example.culturunya.dataclasses.login.LoginResponse
import com.example.culturunya.dataclasses.chats.SendMessageToAdminRequest
import com.example.culturunya.dataclasses.chats.SendMessageToUserRequest
import com.example.culturunya.dataclasses.reports.Report
import com.example.culturunya.dataclasses.reports.ReportRequest
import com.example.culturunya.dataclasses.reports.ResolveRequest
import com.example.culturunya.dataclasses.ranking.RankingPosition
import com.example.culturunya.dataclasses.settings.UpdateLanguageRequest
import retrofit2.Response
import retrofit2.http.*
import okhttp3.MultipartBody

data class SetQuizPointsRequest(val points: Int)

interface Api {
    companion object{
        val instance: Api = Retrofit.Builder().baseUrl("http://nattech.fib.upc.edu:40369/api/")
            .addConverterFactory(MoshiConverterFactory.create())
            .client(OkHttpClient.Builder().build()).build().create(Api::class.java)
    }

    @GET("events/")
    suspend fun getEvents(): EventList

    @GET("events/filter/")
    suspend fun getFilteredEvents(
        @Query("categories") categories: String? = null,
        @Query("date_start_range") dateStart: String? = null,
        @Query("date_end_range") dateEnd: String? = null,
        @Query("longitude") longitude: Double? = null,
        @Query("latitude") latitude: Double? = null,
        @Query("range") range: Int? = null,
        @Header("Authorization") token: String
    ): EventList

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

    @PUT("user/update_username/")
    suspend fun changeUsername(
        @Header("Authorization") token: String,
        @Body request: ChangeUsernameRequest
    ): Response<Unit>

    @Multipart
    @POST("user/profile_pic/")
    suspend fun uploadProfilePic(
        @Header("Authorization") token: String,
        @Part profilePic: MultipartBody.Part
    ): Response<Unit>

    @PUT("user/update_language/")
    suspend fun updateLanguage(@Header("Authorization") token: String, @Body updateLanguageRequest: UpdateLanguageRequest): Response<Unit>

    @POST("reports/create/")
    suspend fun reportRating(@Header("Authorization") token: String, @Body reportRequest: ReportRequest): Response<Unit>

    @PUT("user/set_points_quiz/")
    suspend fun setQuizPoints(
        @Header("Authorization") token: String,
        @Body request: SetQuizPointsRequest
    ): Response<Unit>

    @GET("reports/")
    suspend fun getReports(@Header("Authorization") token: String): List<Report>

    @POST("reports/{report_Id}/resolve/")
    suspend fun resolveReport(@Header("Authorization") token: String, @Path("report_Id") report_Id: String, @Body resolveRequest: ResolveRequest): Response<Unit>


    @GET("leaderboard/quiz/")
    suspend fun getLeaderboardQuiz(@Header("Authorization") token: String): List<RankingPosition>

    @GET("leaderboard/events/")
    suspend fun getLeaderboardEvents(@Header("Authorization") token: String): List<RankingPosition>

    @PUT("user/get_points_event/{event_id}/")
    suspend fun getPointsEvent(
        @Path("event_id") eventId: String,
        @Header("Authorization") token: String
    ): Response<Unit>
}