package com.example.culturunya.models.getChats

data class ChatInfo(
    val last_message_date: String,
    val last_message_from_admin: Boolean,
    val last_message_text: String,
    val profile_pic: String?,
    val user_id: Int,
    val username: String
)