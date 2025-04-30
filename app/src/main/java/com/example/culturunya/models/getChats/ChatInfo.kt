package com.example.culturunya.models.getChats

data class ChatInfo(
    val user_id: String,
    val username: String,
    val profile_pic: String,
    val last_message_text: String,
    val last_message_from_admin: Boolean,
    val last_message_date: String
)