package com.example.culturunya.dataclasses.reports

import com.squareup.moshi.Json

data class ResolveRequest(
    @Json(name = "action")
    val action: String,
    @Json(name = "message")
    val message: String,
)