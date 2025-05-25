package com.example.culturunya.dataclasses.ratings
import com.squareup.moshi.Json

data class Report (
    @Json(name = "id")
    val id : Int,
    @Json(name = "reporter")
    val reporter : String,
    @Json(name = "reported_user")
    val reported_user : String,
    @Json(name = "message")
    val message : String?,
    @Json(name = "date")
    val date : String
)