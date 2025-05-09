package com.example.culturunya.dataclasses.ratings

import com.example.culturunya.dataclasses.user.UserInfo
import com.squareup.moshi.Json

data class Rating (
    @Json(name="user")
    val user: UserInfo,
    @Json(name = "id")
    val id: Long,
    @Json(name="date")
    val date: String,
    @Json(name="rating")
    val rating: String,
    @Json(name="comment")
    val comment: String? = null
)