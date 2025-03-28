package com.example.culturunya.controllers

import kotlinx.coroutines.runBlocking

fun enviarDadesAlBackend(username: String, email: String, password: String): Int {
    val api = Api.instance
    val repository = AuthRepository(api)

    return runBlocking {
        try {
            val response = repository.registerUser(username, email, password)
            response.code() // Retorna el codi HTTP (200, 400, 500...)
        } catch (e: Exception) {
            -1 // Retornem -1 en cas d'error
        }
    }
}
