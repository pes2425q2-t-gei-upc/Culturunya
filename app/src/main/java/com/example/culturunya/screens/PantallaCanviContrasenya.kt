package com.example.culturunya.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.culturunya.R
import com.example.culturunya.endpoints.changePassword.ChangePasswordViewModel
import com.example.culturunya.models.currentSession.CurrentSession
import com.example.culturunya.navigation.AppScreens
import com.example.culturunya.ui.theme.Morat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCanviContrasenya(navController: NavController) {
    var contrasenyaActual by remember { mutableStateOf("") }
    var novaContrasenya by remember { mutableStateOf("") }
    var confirmaNovaContrasenya by remember { mutableStateOf("") }
    var missatgeError by remember { mutableStateOf("") }
    var haCanviat by remember { mutableStateOf(false) }
    var currentPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    CurrentSession.getInstance()
    val currentLocale = CurrentSession.language

    val changePasswordViewModel: ChangePasswordViewModel = viewModel()
    val changePasswordCode by changePasswordViewModel.changePasswordStatus.collectAsState()
    var showErrorDialog by remember { mutableStateOf(false) }

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

            Image(
                painter = painterResource(id = R.drawable.pany),
                contentDescription = "Pany",
                modifier = Modifier
                    .size(140.dp)
                    .padding(top = 20.dp, bottom = 8.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = getString(context, R.string.changePassword, currentLocale),
                fontSize = 24.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(20.dp))
            OutlinedTextField(
                value = contrasenyaActual,
                onValueChange = { contrasenyaActual = it },
                label = { Text(getString(context, R.string.actualPassword, currentLocale)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (currentPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (currentPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    IconButton(onClick = { currentPasswordVisible = !currentPasswordVisible }) {
                        Icon(imageVector = image, contentDescription = "Mostrar/Amagar contrasenya", tint = Color.Gray)
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
                value = novaContrasenya,
                onValueChange = { novaContrasenya = it },
                label = { Text(getString(context, R.string.newPassword, currentLocale)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (newPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                        Icon(imageVector = image, contentDescription = "Mostrar/Amagar contrasenya", tint = Color.Gray)
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
                value = confirmaNovaContrasenya,
                onValueChange = { confirmaNovaContrasenya = it },
                label = { Text(getString(context, R.string.confirmNewPassword, currentLocale)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (newPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                        Icon(imageVector = image, contentDescription = "Mostrar/Amagar contrasenya", tint = Color.Gray)
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

            if (missatgeError.isNotEmpty()) {
                Text(
                    text = missatgeError,
                    color = Color.Red,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            Spacer(modifier = Modifier.height(10.dp))
            Log.d("Codi change password:", "$changePasswordCode")
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
                    title = { Text(getString(context, R.string.passwordChangeCompleted, currentLocale)) },
                    text = { Text(getString(context, R.string.passwordChangedSuccessfully, currentLocale)) },
                    containerColor = Color.White
                )
            }

            Button(
                onClick = {
                    CurrentSession.getInstance()
                    val contrasenyaCorrecta = CurrentSession.password
                    Log.d("Contra actual", "$contrasenyaCorrecta")
                    when {
                        contrasenyaActual.isEmpty() || novaContrasenya.isEmpty() || confirmaNovaContrasenya.isEmpty() ->
                            missatgeError = getString(context, R.string.allFieldsRequired, currentLocale)

                        novaContrasenya != confirmaNovaContrasenya ->
                            missatgeError = getString(context, R.string.passwordsDontMatch, currentLocale)

                        contrasenyaActual != contrasenyaCorrecta ->
                            missatgeError = getString(context, R.string.incorrectActualPassword, currentLocale)

                        contrasenyaActual == novaContrasenya ->
                            missatgeError = getString(context, R.string.passwordsMustBeDifferent, currentLocale)

                        else -> {
                            missatgeError = ""
                            changePasswordViewModel.changePassword(contrasenyaActual, novaContrasenya)
                        }
                    }
                },
                modifier = Modifier.width(250.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Morat)
            ) {
                Text(
                    text = getString(context, R.string.changeThePassword, currentLocale),
                    color = Color.White
                )
            }


            LaunchedEffect(changePasswordCode) {
                when (changePasswordCode) {
                    200 -> haCanviat = true
                    else -> if (changePasswordCode != null) showErrorDialog = true
                }
            }

            if (showErrorDialog) {
                var message = getString(context, R.string.changePasswordErrorNoAuth, currentLocale)
                if (changePasswordCode == 400) {
                    message = getString(context, R.string.validationError, currentLocale)
                }
                popUpError(message, onClick = {
                    showErrorDialog = false
                })
            }
        }
    }
}