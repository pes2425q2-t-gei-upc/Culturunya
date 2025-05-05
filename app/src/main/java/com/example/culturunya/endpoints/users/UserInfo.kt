package com.example.culturunya.endpoints.users

data class UserInfo (
    val username: String,
    val first_name: String,
    val last_name: String,
    val email: String,
    val fullname: String,
    val phone_number: String?,
    val profile_pic: String?,
    val birth_date: String?,
    val language: String,
    val rank_event: String,
    val rank_quiz: String,
    val current_event_points: Int,
    val current_quiz_points: Int,
    val points_to_next_rank_event: Int,
    val points_to_next_quiz_points: Int
)