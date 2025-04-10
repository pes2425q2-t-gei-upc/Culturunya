package com.example.culturunya.controllers

import com.example.culturunya.models.currentUser.User

fun getContrasenyaUsuariActual(): String {
    //Crida a l'API i retorna la contrasenya de l'usuari actual
    User.getInstance()
    return User.password
}