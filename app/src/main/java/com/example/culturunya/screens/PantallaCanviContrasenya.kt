package com.example.culturunya.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.culturunya.controllers.getContrasenyaUsuariActual
import com.example.culturunya.ui.theme.Morat
import com.example.culturunya.ui.theme.VerdFosc

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComposableCanviContrasenya(navController: NavController) {
    var contrasenyaActual by remember { mutableStateOf("") }
    var novaContrasenya by remember { mutableStateOf("") }
    var confirmaNovaContrasenya by remember { mutableStateOf("") }
    var missatgeError by remember { mutableStateOf("") }
    var haCanviat by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .clickable { navController.popBackStack() }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Fletxa a l'esquerra",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Enrere",
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .align(Alignment.Center)
                .background(Color.White, shape = RoundedCornerShape(16.dp))
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Pany",
                modifier = Modifier.size(100.dp)
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

            if (haCanviat) {
                Text(
                    text = "La contrasenya s'ha canviat correctament.",
                    color = VerdFosc,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    val contrasenyaCorrecta = getContrasenyaUsuariActual()
                    when {
                        haCanviat -> true //Si ja s'ha fet el canvi, ignora que tornis a apretar
                        contrasenyaActual.isEmpty() || novaContrasenya.isEmpty() || confirmaNovaContrasenya.isEmpty() ->
                            missatgeError = "Tots els camps són obligatoris."

                        novaContrasenya != confirmaNovaContrasenya ->
                            missatgeError = "Les noves contrasenyes no coincideixen."

                        contrasenyaActual != contrasenyaCorrecta ->
                            missatgeError = "La contrasenya actual és incorrecta."

                        contrasenyaActual == novaContrasenya ->
                            missatgeError = "La nova contrasenya no pot ser igual que l'anterior."
                        else -> {
                            missatgeError = ""
                            haCanviat = true
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