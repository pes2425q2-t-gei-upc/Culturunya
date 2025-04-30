package com.example.culturunya.endpoints.ratings

import com.example.culturunya.endpoints.users.UserSimpleInfo
import com.squareup.moshi.Json

data class Rating (
    @Json(name="user")
    val user: UserSimpleInfo,
    @Json(name = "id")
    val id: Long,
    @Json(name="date")
    val date: String,
    @Json(name="rating")
    val rating: String,
    @Json(name="comment")
    val comment: String? = null
)