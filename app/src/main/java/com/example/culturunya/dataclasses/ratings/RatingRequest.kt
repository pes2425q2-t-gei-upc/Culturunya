package com.example.culturunya.dataclasses.ratings

import com.squareup.moshi.Json

data class RatingRequest(
    @Json(name = "event_id")
    val event_id: Long,
    @Json(name = "rating")
    val rating: String,
    @Json(name = "comment")
    val comment: String? = null
)