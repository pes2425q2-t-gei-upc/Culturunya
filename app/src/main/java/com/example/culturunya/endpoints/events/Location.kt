package com.example.culturunya.endpoints.events

data class Location(
    val address: String,
    val city: String,
    val latitude: Double,
    val longitude: Double
)