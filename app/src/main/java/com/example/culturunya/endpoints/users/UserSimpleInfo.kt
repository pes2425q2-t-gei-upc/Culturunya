package com.example.culturunya.endpoints.users
import com.squareup.moshi.Json

data class UserSimpleInfo (
    @Json(name = "username")
    val username: String,
    @Json(name = "email")
    val email: String,
    @Json(name = "profile_pic")
    val profilePic: String? = null
)