package com.example.culturunya.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.culturunya.R
import com.example.culturunya.viewmodels.ChangeUsernameViewModel
import com.example.culturunya.session.CurrentSession
import com.example.culturunya.navigation.AppScreens
import com.example.culturunya.ui.theme.Morat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
/**
 * Pantalla per canviar el nom d'usuari.
 * Permet introduir un nou nom d'usuari i actualitzar-lo.
 *
 * @param navController Controlador de navegació per gestionar la navegació entre pantalles.
 */
fun PantallaCanviNom(navController: NavController) {
    var nouNom by remember { mutableStateOf("") }
    var missatgeError by remember { mutableStateOf("") }
    var haCanviat by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    CurrentSession.getInstance()
    val currentLocale = CurrentSession.language
    val currentUsername = CurrentSession.username

    val viewModel: ChangeUsernameViewModel = viewModel()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.success) {
        if (state.success) {
            haCanviat = true
            CurrentSession.username = nouNom
        }
    }

    LaunchedEffect(state.error) {
        if (state.error != null) {
            missatgeError = state.error.toString()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        IconButton(onClick = {
            isLoading = true
            navController.navigate(AppScreens.MainScreen.createRoute("Settings"))
        }) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.Black
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
                imageVector = Icons.Default.Person,
                contentDescription = "Persona",
                modifier = Modifier
                    .size(140.dp)
                    .padding(top = 20.dp, bottom = 8.dp),
                tint = Morat
            )

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = getString(context, R.string.changeUsername, currentLocale),
                fontSize = 24.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = nouNom,
                onValueChange = { 
                    nouNom = it
                    viewModel.updateNewUsername(it)
                    if (it == currentUsername) {
                        missatgeError = getString(context, R.string.sameUsernameError, currentLocale)
                    } else {
                        missatgeError = ""
                    }
                },
                label = { Text(getString(context, R.string.newUsername, currentLocale)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
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
                AlertDialog(
                    onDismissRequest = { haCanviat = false },
                    confirmButton = {
                        Button(
                            onClick = {
                                haCanviat = false
                                navController.navigate(AppScreens.MainScreen.createRoute("Settings"))
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Morat)
                        ) {
                            Text(getString(context, R.string.ok, currentLocale))
                        }
                    },
                    title = { Text(getString(context, R.string.usernameChangedSuccessfully, currentLocale)) },
                    containerColor = Color.White
                )
            }

            Button(
                onClick = { viewModel.changeUsername() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Morat),
                enabled = !state.isLoading && nouNom.isNotBlank() && nouNom != currentUsername
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(getString(context, R.string.saveChanges, currentLocale))
                }
            }
        }
    }
} 