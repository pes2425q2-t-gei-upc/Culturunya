package com.example.culturunya.dataclasses.events

import com.squareup.moshi.Json

data class Event(
    @Json(name = "id")
    val id: String,
    @Json(name = "name")
    val name: String,
    @Json(name = "date_start")
    val date_start: String,
    @Json(name = "date_end")
    val date_end: String,
    @Json(name = "description")
    val description: String,
    @Json(name = "price")
    val price: String,
    @Json(name = "location")
    val location: Location,
    @Json(name = "categories")
    val categories: List<String>,
)