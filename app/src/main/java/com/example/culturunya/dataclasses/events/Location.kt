package com.example.culturunya.dataclasses.events

import com.squareup.moshi.Json

data class Location(
    @Json(name = "city")
    val city: String,
    @Json(name = "address")
    val address: String,
    @Json(name = "latitude")
    val latitude: Double,
    @Json(name = "longitude")
    val longitude: Double,
)