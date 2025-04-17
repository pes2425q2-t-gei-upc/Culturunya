package com.example.culturunya.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.culturunya.R
import com.example.culturunya.navigation.AppScreens
import com.example.culturunya.ui.theme.CulturunyaTheme
import com.example.culturunya.ui.theme.Morat
import com.example.culturunya.controllers.enviarDadesAlBackend



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaRegistre(navController: NavController) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var registreExit by remember { mutableStateOf(false) } // Controla si mostrar la confirmacio
    var passwordVisible by remember { mutableStateOf(false) }
    val imageRes = if (passwordVisible) R.drawable.image_visible else R.drawable.image_hidden
    var showDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .background(Color.White, shape = RoundedCornerShape(16.dp))
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = painterResource(id = imageRes),
                contentDescription = "Passwors visible/hidden picture",
                modifier = Modifier
                    .size(120.dp)
                    .padding(top = 20.dp, bottom = 8.dp)
            )

            Text(
                text = "Crea el teu compte",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Nom d'usuari") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Person, contentDescription = "Persona")
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = Color.Black,
                    cursorColor = Color.Black,
                    focusedBorderColor = Color.Gray,
                    unfocusedBorderColor = Color.LightGray
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correu electronic") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Email, contentDescription = "Persona")
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = Color.Black,
                    cursorColor = Color.Black,
                    focusedBorderColor = Color.Gray,
                    unfocusedBorderColor = Color.LightGray
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contrasenya") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = "Pany")
                },
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = "Mostrar/Amagar contrasenya")
                    }
                },

                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = Color.Black,
                    cursorColor = Color.Black,
                    focusedBorderColor = Color.Gray,
                    unfocusedBorderColor = Color.LightGray
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirmar contrasenya") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = "Pany")
                },
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = "Mostrar/Amagar contrasenya")
                    }
                },

                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = Color.Black,
                    cursorColor = Color.Black,
                    focusedBorderColor = Color.Gray,
                    unfocusedBorderColor = Color.LightGray
                )
            )

            Spacer(modifier = Modifier.height(10.dp))



            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    when {
                        username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() -> {
                            errorMessage = "Tots els camps són obligatoris."
                            showDialog = true
                        }
                        !isValidEmail(email) -> {
                            errorMessage = "El correu electrònic no és vàlid."
                            showDialog = true
                        }
                        password != confirmPassword -> {
                            errorMessage = "Les contrasenyes no coincideixen."
                            showDialog = true
                        }
                        else -> {
                            val responseCode = enviarDadesAlBackend(username, email, password)
                            when (responseCode) {
                                201 -> registreExit = true // Anar a la pantalla principal
                                400 -> {
                                    errorMessage = "Error: Dades incorrectes."
                                    showDialog = true
                                }
                                500 -> {
                                    errorMessage = "Error: Problema al servidor."
                                    showDialog = true
                                }
                                -1 -> {
                                    errorMessage = "Error de xarxa. Torna a intentar-ho."
                                    showDialog = true
                                }
                                else -> {
                                    errorMessage = "Error desconegut: $responseCode"
                                    showDialog = true
                                }
                            }
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Morat)
            ) {
                Text("Registrar-se")
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ja tens compte? ",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Button(
                    onClick = {
                        navController.navigate(route = AppScreens.IniciSessio.route)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(0.dp),
                    elevation = null
                ) {
                    Text(
                        text = "Inicia sessió",
                        fontSize = 14.sp,
                        color = Color.Blue
                    )
                }
            }
        }


        if (registreExit) {
            AlertDialog(
                onDismissRequest = { registreExit = false },
                confirmButton = {
                    Button(
                        onClick = {
                            registreExit = false
                            navController.navigate(route = AppScreens.MainScreen.route) // Torna a la pantalla anterior
                        }
                    ) {
                        Text("D'acord")
                    }
                },
                title = { Text("Registre completat") },
                text = { Text("T'has registrat correctament!") }
            )
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Error en el registre") },
                text = { Text(errorMessage) },
                confirmButton = {
                    Button(onClick = { showDialog = false }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}


fun isValidEmail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}