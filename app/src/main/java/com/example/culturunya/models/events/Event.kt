package com.example.culturunya.models.events

data class Event(
    val categories: List<String>,
    val date_end: String,
    val date_start: String,
    val description: String,
    val id: String,
    val location: Location,
    val name: String,
    val price: String
)