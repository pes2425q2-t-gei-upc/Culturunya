package com.example.culturunya.controllers

import android.util.Log
import kotlinx.coroutines.runBlocking

fun enviarDadesAlBackend(username: String, email: String, password: String): Int {
    val api = Api.instance
    val repository = AuthRepository(api)

    return runBlocking {
        try {
            val response = repository.registerUser(username, email, password)
            // ✅ Només retornem el codi HTTP, ignorant username i email de la resposta JSON
            return@runBlocking response.code()
        } catch (e: Exception) {
            Log.e("Registre", "Error de xarxa: ${e.message}", e)
            return@runBlocking -1  // Retornem -1 si hi ha un error de xarxa
        }
    }
}
