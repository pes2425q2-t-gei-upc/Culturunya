package com.example.culturunya.controllers

import kotlinx.coroutines.runBlocking

fun enviarDadesAlBackend(username: String, email: String, password: String): Boolean {
    val api = Api.instance
    val repository = TestRepository(api)

    return runBlocking {
        val result = repository.getokay()
        result.isSuccess && result.getOrNull()?.message == "okay"
    }
}
