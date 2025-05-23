package com.example.culturunya.dataclasses.chats;

data class Chat(
    val username: String,
    val lastMessage: String,
    val avatar: String?,
    val lastMessageDate: String,
    val userId: Int
)