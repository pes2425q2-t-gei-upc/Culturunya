package com.example.culturunya.dataclasses.users

import com.squareup.moshi.Json

data class UserInfo (
    @Json(name="username")
    val username: String,
    @Json(name="first_name")
    val first_name: String,
    @Json(name="last_name")
    val last_name: String,
    @Json(name="email")
    val email: String,
    @Json(name="fullname")
    val fullname: String,
    @Json(name="phone_number")
    val phone_number: String?,
    @Json(name="profile_pic")
    val profile_pic: String?,
    @Json(name="birth_date")
    val birth_date: String?,
    @Json(name="language")
    val language: String,
    @Json(name="rank_event")
    val rank_event: String,
    @Json(name="rank_quiz")
    val rank_quiz: String,
    @Json(name="total_event_points")
    val total_event_points: Int,
    @Json(name="total_quiz_points")
    val total_quiz_points: Int,
    @Json(name="banned_from_comments")
    val banned_from_comments: Boolean,
    @Json(name="is_admin")
    val is_admin: Boolean
)