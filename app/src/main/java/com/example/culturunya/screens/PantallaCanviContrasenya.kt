package com.example.culturunya.screens

import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.culturunya.R
import com.example.culturunya.controllers.getContrasenyaUsuariActual
import com.example.culturunya.endpoints.changePassword.ChangePasswordViewModel
import com.example.culturunya.endpoints.deleteAccount.DeleteAccountViewModel
import com.example.culturunya.navigation.AppScreens
import com.example.culturunya.ui.theme.Morat
import com.example.culturunya.ui.theme.VerdFosc
import java.util.*

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
    var confirmNewPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var currentLocale by remember { mutableStateOf(Locale.getDefault().language) }

    val changePasswordViewModel: ChangePasswordViewModel = viewModel()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .clickable(enabled = !isLoading) {
                    isLoading = true
                    navController.popBackStack()
                }
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
                text = getString(context, R.string.back, currentLocale),
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
                value = novaContrasenya,
                onValueChange = { novaContrasenya = it },
                label = { Text(getString(context, R.string.newPassword, currentLocale)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (newPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
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
                value = confirmaNovaContrasenya,
                onValueChange = { confirmaNovaContrasenya = it },
                label = { Text(getString(context, R.string.confirmNewPassword, currentLocale)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (confirmNewPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (confirmNewPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    IconButton(onClick = { confirmNewPasswordVisible = !confirmNewPasswordVisible }) {
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

            if (missatgeError.isNotEmpty()) {
                Text(
                    text = missatgeError,
                    color = Color.Red,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (haCanviat) {
                AlertDialog(
                    onDismissRequest = { haCanviat = false },
                    confirmButton = {
                        Button(
                            onClick = {
                                haCanviat = false
                                navController.popBackStack()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Morat)
                        ) {
                            Text(getString(context, R.string.ok, currentLocale))
                        }
                    },
                    title = { Text(getString(context, R.string.passwordChangeCompleted, currentLocale)) },
                    text = { Text(getString(context, R.string.passwordChangedSuccesfully, currentLocale)) },
                    containerColor = Color.White
                )
            }

            Button(
                onClick = {
                    val contrasenyaCorrecta = getContrasenyaUsuariActual()
                    when {
                        haCanviat -> true //Si ja s'ha fet el canvi, ignora que tornis a apretar
                        contrasenyaActual.isEmpty() || novaContrasenya.isEmpty() || confirmaNovaContrasenya.isEmpty() ->
                            missatgeError = getString(context, R.string.allFieldsCompulsory, currentLocale)

                        novaContrasenya != confirmaNovaContrasenya ->
                            missatgeError = getString(context, R.string.passwordsDontMatch, currentLocale)

                        contrasenyaActual != contrasenyaCorrecta ->
                            missatgeError = getString(context, R.string.incorrectActualPassword, currentLocale)

                        contrasenyaActual == novaContrasenya ->
                            missatgeError = getString(context, R.string.passwordsMustBeDifferent, currentLocale)
                        else -> {
                            missatgeError = ""
                            changePasswordViewModel.changePassword(contrasenyaActual, novaContrasenya)
                            haCanviat = true
                        }
                    }
                },
                modifier = Modifier.width(250.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Morat)
            ) {
                Text(text = getString(context, R.string.changeThePassword, currentLocale), color = Color.White)
            }
        }
    }
}