package com.example.culturunya.controllers

import com.example.culturunya.endpoints.users.UserSimpleInfo

class UserRepository(private val api: Api) {
    suspend fun getProfileInfo(token: String): UserSimpleInfo {
        return try{
            api.getProfileInfo(token)
        } catch (e: Exception){
            throw e
        }
    }
}