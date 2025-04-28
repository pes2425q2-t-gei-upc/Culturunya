package com.example.culturunya.endpoints.events

import com.squareup.moshi.Json
import java.util.Date

data class Rating (
    @Json(name="user")
    val user: UserSimpleInfo,
    @Json(name = "id")
    val id: Int,
    @Json(name="date")
    val date: String,
    @Json(name="rating")
    val rating: String,
    @Json(name="comment")
    val comment: String? = null
)