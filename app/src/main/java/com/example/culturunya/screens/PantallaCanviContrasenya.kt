package com.example.culturunya.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.culturunya.R
import com.example.culturunya.controllers.comprovaNomContrasenya
import com.example.culturunya.navigation.AppScreens
import com.example.culturunya.ui.theme.Morat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComposableCanviContrasenya(navController: NavController) {
    var contrasenyaActual by remember { mutableStateOf("") }
    var novaContrasenya by remember { mutableStateOf("") }
    var confirmaNovaContrasenya by remember { mutableStateOf("") }
    var missatgeError by remember { mutableStateOf("") }

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
            Icon (
                imageVector = Icons.Default.Lock, contentDescription = "Pany", Modifier.size(100.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Canvi de Contrasenya",
                fontSize = 24.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = contrasenyaActual,
                onValueChange = { contrasenyaActual = it },
                label = { Text("Contrasenya actual") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = Color.Black,
                    cursorColor = Color.Black,
                    focusedBorderColor = Color.Gray,
                    unfocusedBorderColor = Color.LightGray
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = novaContrasenya,
                onValueChange = { novaContrasenya = it },
                label = { Text("Nova contrasenya") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = Color.Black,
                    cursorColor = Color.Black,
                    focusedBorderColor = Color.Gray,
                    unfocusedBorderColor = Color.LightGray
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = confirmaNovaContrasenya,
                onValueChange = { confirmaNovaContrasenya = it },
                label = { Text("Confirmar nova contrasenya") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = Color.Black,
                    cursorColor = Color.Black,
                    focusedBorderColor = Color.Gray,
                    unfocusedBorderColor = Color.LightGray
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (missatgeError.isNotEmpty()) {
                Text(
                    text = missatgeError,
                    color = Color.Red,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    when {
                        contrasenyaActual.isEmpty() || novaContrasenya.isEmpty() || confirmaNovaContrasenya.isEmpty() ->
                            missatgeError = "Tots els camps són obligatoris."

                        novaContrasenya != confirmaNovaContrasenya ->
                            missatgeError = "Les noves contrasenyes no coincideixen."

                        contrasenyaActual != "contrasenya_correcta" ->
                            missatgeError = "La contrasenya actual és incorrecta."

                        else -> {
                            missatgeError = ""
                            navController.navigate("pantalla_confirmacio")
                        }
                    }
                },
                modifier = Modifier.width(250.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Morat)
            ) {
                Text(text = "Canviar Contrasenya", color = Color.White)
            }
        }
    }
}