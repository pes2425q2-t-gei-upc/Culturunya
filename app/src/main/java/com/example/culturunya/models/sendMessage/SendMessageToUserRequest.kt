package com.example.culturunya.models.sendMessage

data class SendMessageToUserRequest(
    val receiver_id: Int,
    val text: String
)
