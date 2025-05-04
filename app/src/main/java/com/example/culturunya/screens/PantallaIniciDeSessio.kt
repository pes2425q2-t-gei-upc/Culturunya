package com.example.culturunya.screens

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.culturunya.R
import com.example.culturunya.navigation.AppScreens
import com.example.culturunya.ui.theme.Morat
import com.example.culturunya.endpoints.login.LoginViewModel
import com.example.culturunya.models.currentSession.CurrentSession

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComposableIniciSessio(navController: NavController) {
    var usuari by remember { mutableStateOf("") }
    var contrasenya by remember { mutableStateOf("") }
    var missatgeError by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current
    CurrentSession.getInstance()
    val currentLocale = CurrentSession.language
    val loginViewModel: LoginViewModel = viewModel()
    var token = ""
    val scrollState = rememberScrollState()

    // Inicializa el ViewModel con el contexto
    LaunchedEffect(Unit) {
        loginViewModel.initialize(context)
    }

    // Estados del ViewModel
    val loginError by loginViewModel.loginError.collectAsState()
    val googleLoginError by loginViewModel.googleLoginError.collectAsState()
    val loginResponse by loginViewModel.loginResponse.collectAsState()

    // Navegar al MainScreen cuando el login es exitoso
    LaunchedEffect(loginResponse) {
        if (loginResponse != null) {
            navController.navigate(AppScreens.MainScreen.createRoute("Events"))
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .systemBarsPadding()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .align(Alignment.Center)
                .background(Color.White, shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_retallat),
                contentDescription = "Logo retallat",
                modifier = Modifier
                    .height(150.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = getString(context, R.string.login, currentLocale),
                fontSize = 22.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = usuari,
                onValueChange = { usuari = it },
                label = { Text(getString(context, R.string.username, currentLocale)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Person, contentDescription = "Persona", tint = Color.Gray)
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = Color.Black,
                    cursorColor = Color.Black,
                    focusedBorderColor = Color.Gray,
                    unfocusedBorderColor = Color.LightGray
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(
                value = contrasenya,
                onValueChange = { contrasenya = it },
                label = { Text(getString(context, R.string.password, currentLocale)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = "Mostrar/Amagar contrasenya", tint = Color.Gray)
                    }
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = "Pany", tint = Color.Gray)
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = Color.Black,
                    cursorColor = Color.Black,
                    focusedBorderColor = Color.Gray,
                    unfocusedBorderColor = Color.LightGray
                )
            )

            if (loginError != null) {
                val errorMessage = when (loginError) {
                    400 -> getString(context, R.string.errIncorrectUsernameAndPassword, currentLocale)
                    401 -> getString(context, R.string.notAuthorized, currentLocale)
                    500 -> getString(context, R.string.serverError, currentLocale)
                    else -> "Unknown error"
                }
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            googleLoginError?.let { error ->
                Text(
                    text = error,
                    color = Color.Red,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    if (usuari.isEmpty() || contrasenya.isEmpty()) {
                        missatgeError = getString(context, R.string.errNeedUsernameAndPassword, currentLocale)
                    } else {
                        loginViewModel.login(usuari, contrasenya)
                    }
                },
                modifier = Modifier.width(250.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Morat)
            ) {
                Text(text = getString(context, R.string.enter, currentLocale), color = Color.White)
            }

            OutlinedButton(
                onClick = {
                    loginViewModel.signInWithGoogle(context)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                modifier = Modifier.padding(16.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_google),
                    contentDescription = "Logo de Google"
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = getString(context, R.string.enterWithGoogle, currentLocale),
                    color = Color.Black
                )
            }

            Divider(
                color = Color.Gray,
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = getString(context, R.string.noAccountYet, currentLocale),
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Button(
                    onClick = {
                        navController.navigate(route = AppScreens.PantallaRegistre.route)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(0.dp),
                    elevation = null
                ) {
                    Text(
                        text = getString(context, R.string.register, currentLocale),
                        fontSize = 14.sp,
                        color = Color.Blue
                    )
                }
            }

            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}