package com.example.culturunya.dataclasses.messages

data class SendMessageToUserRequest(
    val receiver_id: Int,
    val text: String
)
