package com.example.culturunya.screens

import android.content.Context
import android.content.res.Configuration
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import java.util.Locale
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

fun getString(context: Context, resId: Int, locale: String): String {
    val config = Configuration(context.resources.configuration)
    config.setLocale(Locale(locale))
    return context.createConfigurationContext(config).resources.getString(resId)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComposableIniciSessio(navController: NavController) {
    var usuari by remember { mutableStateOf("") }
    var contrasenya by remember { mutableStateOf("") }
    var missatgeError by remember { mutableStateOf("") }

    //variables que necessitem per canviar d'idioma
    val context = LocalContext.current
    var currentLocale by remember { mutableStateOf(Locale.getDefault().language) }

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
            //canviar currentLocale a "iso de l'idioma" fa que es canvii l'idioma

            Image (
                painter = painterResource(id = R.drawable.logo_retallat),
                contentDescription = "Logo retallat"
            )

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = getString(context, R.string.login, currentLocale),
                fontSize = 24.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = usuari,
                onValueChange = { usuari = it },
                label = { Text(getString(context, R.string.username, currentLocale)) },
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
                label = { Text(getString(context, R.string.password, currentLocale)) },
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
                        missatgeError = getString(context, R.string.errNeedUsernameAndPassword, currentLocale)
                    }
                    else {
                        //Comprova si el nom i la contrasenya són correctes
                        if (comprovaNomContrasenya(usuari, contrasenya)) navController.navigate(route = AppScreens.MainScreen.route)
                        else missatgeError = getString(context, R.string.errIncorrectUsernameAndPassword, currentLocale)
                    }
                },
                modifier = Modifier.width(250.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Morat)
            ) {
                Text(text = getString(context, R.string.enter, currentLocale), color = Color.White)
            }

            OutlinedButton(
                onClick = {
                    //Crida a la pantalla d'iniciar sessió amb Google
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                modifier = Modifier.padding(16.dp)) {
                Image(painter = painterResource(id = R.drawable.logo_google), contentDescription = "logo de google")
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = getString(context, R.string.enterWithGoogle, currentLocale), color = Color.Black)
            }

            Divider(
                color = Color.Gray,
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 25.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = getString(context, R.string.noAccountYet, currentLocale),
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Button(
                    onClick = { navController.navigate(route = AppScreens.PantallaRegistre.route) },
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
        }
    }
}