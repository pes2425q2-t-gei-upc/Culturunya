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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
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
import com.example.culturunya.R
import com.example.culturunya.navigation.AppScreens
import com.example.culturunya.ui.theme.CulturunyaTheme
import com.example.culturunya.ui.theme.Morat
import com.example.culturunya.controllers.comprovaNomContrasenya

class TestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CulturunyaTheme {
                //ComposablePrincipal()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComposableIniciSessio(navController: NavController) {
    var usuari by remember { mutableStateOf("") }
    var contrasenya by remember { mutableStateOf("") }
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
            Image (
                painter = painterResource(id = R.drawable.logo_retallat),
                contentDescription = "Logo retallat"
            )

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Iniciar Sessió",
                fontSize = 24.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = usuari,
                onValueChange = { usuari = it },
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
                value = contrasenya,
                onValueChange = { contrasenya = it },
                label = { Text("Contrasenya") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = "Pany")
                },
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
                    if (usuari.isEmpty() || contrasenya.isEmpty()) {
                        missatgeError = "Indiqui el nom d'usuari i la contrasenya"
                    }
                    else {
                        //Comprova si el nom i la contrasenya són correctes
                        if (comprovaNomContrasenya(usuari, contrasenya)) navController.navigate(route = AppScreens.MainScreen.route)
                        else missatgeError = "El nom d'usuari i/o la contrasenya són incorrectes"
                    }
                },
                modifier = Modifier.width(250.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Morat)
            ) {
                Text(text = "Ingresar", color = Color.White)
            }

            OutlinedButton(
                onClick = {
                    //Crida a la pantalla d'iniciar sessió amb Google
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                modifier = Modifier.padding(16.dp)) {
                Image(painter = painterResource(id = R.drawable.logo_google), contentDescription = "logo de google")
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = "Accedir amb Google", color = Color.Black)
            }

            Divider(
                color = Color.Gray,
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 25.dp)
            )

            Row (
                verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Encara no tens compte? ",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = "Registra't",
                    fontSize = 14.sp,
                    color = Color.Blue,
                    modifier = Modifier.clickable {  }
                )
            }
        }
    }
}