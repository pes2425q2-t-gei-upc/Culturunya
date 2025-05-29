package com.example.culturunya.views

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.background
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
import com.example.culturunya.ui.theme.Morat
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.culturunya.Api
import com.example.culturunya.session.CurrentSession
import com.example.culturunya.repositories.AuthRepository
import com.example.culturunya.viewmodels.LoginViewModel
import kotlinx.coroutines.runBlocking


/**
 * Component principal per a la pantalla de registre d'usuaris.
 * Permet als usuaris crear un nou compte amb nom d'usuari, correu electrònic i contrasenya.
 *
 * @param navController Controlador de navegació per moure's entre pantalles.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaRegistre(navController: NavController) {
    // Variables d'estat per emmagatzemar les dades d'entrada de l'usuari
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var registreExit by remember { mutableStateOf(false) } // Controla si mostrar la confirmacio
    var passwordVisible by remember { mutableStateOf(false) }
    val imageRes = if (passwordVisible) R.drawable.image_visible else R.drawable.image_hidden
    var showDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    //variables relacionadas con el cambio de idioma
    val context = LocalContext.current
    CurrentSession.getInstance()
    var currentLocale by remember { mutableStateOf(CurrentSession.language) }

    //variable per executar un login quan el usuari termini el registre
    val loginViewModel: LoginViewModel = viewModel()
    CurrentSession.getInstance()
    // Inicializa el ViewModel con el contexto
    LaunchedEffect(Unit) {
        loginViewModel.initialize(context)
    }


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

            // imatge que muta en funció de la visibilitat de la contrassenya
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = "Passwors visible/hidden picture",
                modifier = Modifier
                    .size(120.dp)
                    .padding(top = 20.dp, bottom = 8.dp)
            )

            //Títol de la pantalla de registre
            Text(
                text = com.example.culturunya.views.functions.getString(
                    context,
                    R.string.registerScreenTitle,
                    currentLocale
                ),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            //Camp de text per el username
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text(
                    com.example.culturunya.views.functions.getString(
                        context,
                        R.string.usernameBox,
                        currentLocale
                    )
                ) },
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

            Spacer(modifier = Modifier.height(10.dp))

            //Camp de text per el correu
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(
                    com.example.culturunya.views.functions.getString(
                        context,
                        R.string.mailBox,
                        currentLocale
                    )
                ) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Email, contentDescription = "Persona", tint = Color.Gray)
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = Color.Black,
                    cursorColor = Color.Black,
                    focusedBorderColor = Color.Gray,
                    unfocusedBorderColor = Color.LightGray
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            //Camp de text per la contrassenya (amb botó per amagar/mostrar)
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(
                    com.example.culturunya.views.functions.getString(
                        context,
                        R.string.password,
                        currentLocale
                    )
                ) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = "Pany", tint = Color.Gray)
                },
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
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

            //Camp per tornar a inserir la contrassenya (per la confirmació)
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text(
                    com.example.culturunya.views.functions.getString(
                        context,
                        R.string.confirmPasswordBox,
                        currentLocale
                    )
                )},
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = "Pany", tint = Color.Gray)
                },
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
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



            Spacer(modifier = Modifier.height(16.dp))

            /*
            * Botó per a dur a terme l'acció de registrar-se
            * La funció verifica a nivell de front-end que:
            *   --> L'usuari ha introduit totes les dades
            *   --> El correu és vàlid (té l'estructura de string@string.string
            *   --> Les contrassenyes coincideixen
             */
            Button(
                onClick = {
                    when {
                        username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() -> {
                            errorMessage = com.example.culturunya.views.functions.getString(
                                context,
                                R.string.allFieldsRequired,
                                currentLocale
                            )
                            showDialog = true
                        }
                        !isValidEmail(email) -> {
                            errorMessage = com.example.culturunya.views.functions.getString(
                                context,
                                R.string.invalidEmailError,
                                currentLocale
                            )
                            showDialog = true
                        }
                        password != confirmPassword -> {
                            errorMessage = com.example.culturunya.views.functions.getString(
                                context,
                                R.string.unmatchingPasswordsError,
                                currentLocale
                            )
                            showDialog = true
                        }
                        else -> {
                            val responseCode = enviarDadesAlBackend(username, email, password)
                            when (responseCode) {
                                201 -> registreExit = true // Anar a la pantalla principal
                                400 -> {
                                    errorMessage = com.example.culturunya.views.functions.getString(
                                        context,
                                        R.string.invalidDataError,
                                        currentLocale
                                    )
                                    showDialog = true
                                }
                                500 -> {
                                    errorMessage = com.example.culturunya.views.functions.getString(
                                        context,
                                        R.string.serverError,
                                        currentLocale
                                    )
                                    showDialog = true
                                }
                                -1 -> {
                                    errorMessage = com.example.culturunya.views.functions.getString(
                                        context,
                                        R.string.networkError,
                                        currentLocale
                                    )
                                    showDialog = true
                                }
                                else -> {
                                    errorMessage = com.example.culturunya.views.functions.getString(
                                        context,
                                        R.string.unknownError,
                                        currentLocale
                                    ) + "$responseCode"
                                    showDialog = true
                                }
                            }
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Morat)
            ) {
                Text(
                    text = com.example.culturunya.views.functions.getString(
                        context,
                        R.string.registerButton,
                        currentLocale
                    ),
                    color = Color.White
                )
            }

            /*
            * Línia que informa a l'usuari que si ja té un compte pot iniciar sessió
            * a més conté un botó per anar a la pantalla de inici de sessió
             */
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = com.example.culturunya.views.functions.getString(
                        context,
                        R.string.alreadyRegisterded,
                        currentLocale
                    ),
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
                        text = com.example.culturunya.views.functions.getString(context, R.string.login, currentLocale),
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
                            CurrentSession.addRegisterData(username, password, email)
                            loginViewModel.login(username, password)
                            navController.navigate(route = AppScreens.MainScreen.createRoute("Events"))
                        }
                    ) {
                        Text(
                            text = com.example.culturunya.views.functions.getString(
                                context,
                                R.string.confirmationButton,
                                currentLocale
                            ),
                            color = Color.White
                        )
                    }
                },
                title = {
                    Text(
                        text = com.example.culturunya.views.functions.getString(
                            context,
                            R.string.registrationCompleted,
                            currentLocale
                        ),
                        color = Color.Black
                    )
                },
                text = {
                    Text(
                        text = com.example.culturunya.views.functions.getString(
                            context,
                            R.string.registrationConfirmation,
                            currentLocale
                        ),
                        color = Color.Black
                    )
                }
            )
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = {
                    com.example.culturunya.views.functions.getString(
                        context,
                        R.string.registrationGeneralError,
                        currentLocale
                    )
                },
                text = { Text(errorMessage) },
                confirmButton = {
                    Button(onClick = { showDialog = false }) {
                        Text(
                            text = com.example.culturunya.views.functions.getString(
                                context,
                                R.string.confirmationButton,
                                currentLocale
                            ),
                            color = Color.White
                        )
                    }
                }
            )
        }
    }
}

// Funció per verificar que el correu té una estructura adequeada (amb el domini)
fun isValidEmail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

fun enviarDadesAlBackend(username: String, email: String, password: String): Int {
    val api = Api.instance
    val repository = AuthRepository(api)

    return runBlocking {
        try {
            val response = repository.registerUser(username, email, password)
            //Només retornem el codi HTTP, ignorant username i email de la resposta JSON
            return@runBlocking response.code()
        } catch (e: Exception) {
            Log.e("Registre", "Error de xarxa: ${e.message}", e)
            return@runBlocking -1  // Retornem -1 si hi ha un error de xarxa
        }
    }
}