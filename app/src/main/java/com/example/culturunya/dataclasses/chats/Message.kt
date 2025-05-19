package com.example.culturunya.dataclasses.chats

data class Message(
    val from: String,
    val to: String,
    val text: String,
    val date: String
)
