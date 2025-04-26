package com.example.culturunya.controllers

import com.example.culturunya.models.currentSession.CurrentSession

fun getContrasenyaUsuariActual(): String {
    //Crida a l'API i retorna la contrasenya de l'usuari actual
    CurrentSession.getInstance()
    return CurrentSession.password
}