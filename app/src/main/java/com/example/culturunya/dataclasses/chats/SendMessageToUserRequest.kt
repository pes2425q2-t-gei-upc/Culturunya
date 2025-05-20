package com.example.culturunya.dataclasses.chats

data class SendMessageToUserRequest(
    val receiver_id: Int,
    val text: String
)
