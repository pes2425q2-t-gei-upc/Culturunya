package com.example.culturunya.dataclasses.reports

import com.squareup.moshi.Json

data class ReportRequest(
        @Json(name = "rating_id")
        val rating_id: Int,
        @Json(name = "message")
        val message: String,
)