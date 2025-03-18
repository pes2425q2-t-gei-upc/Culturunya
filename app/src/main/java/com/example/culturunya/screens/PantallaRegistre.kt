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

@Composable
fun PantallaRegistre(navController: NavController) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MyTextField(navController, label = "Nom d'usuari") { username = it }
        //MyTextField(navController, label = "Correu electrònic") { email = it }
        //MyTextField(navController, label = "Contrasenya") { password = it }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (username.isNotBlank()
                    //&& email.isNotBlank() && password.isNotBlank()
                    ) {
                    navController.popBackStack()
                }
            }
        ) {
            Text("Registrar-se")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTextField(navController: NavController, label: String, onTextSaved: (String) -> Unit) {
    var text by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(8.dp)) {
        TextField(
            value = text,
            onValueChange = { text = it },
            label = { Text(label) }
        )

        Spacer(modifier = Modifier.height(8.dp))
    }
}