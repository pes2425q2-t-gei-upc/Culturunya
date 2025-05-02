package com.example.culturunya.screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.culturunya.R
import com.example.culturunya.endpoints.deleteAccount.DeleteAccountViewModel
import com.example.culturunya.endpoints.getChats.GetChatsViewModel
import com.example.culturunya.models.currentSession.CurrentSession
import com.example.culturunya.navigation.AppScreens
import com.example.culturunya.ui.theme.GrisMoltFluix
import com.example.culturunya.ui.theme.Morat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    // Control de diàlegs
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val deleteAccountViewModel: DeleteAccountViewModel = viewModel()
    val deleteCode by deleteAccountViewModel.deleteAccountStatus.collectAsState()
    var showDeleteErrorDialog by remember { mutableStateOf(false) }

    val getChatsViewModel: GetChatsViewModel = viewModel()
    val getChatsResponse = getChatsViewModel.getChatsResponse.collectAsState().value
    var getChatsCode = getChatsViewModel.getChatsError.collectAsState().value
    var showGetChatsErrorDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    CurrentSession.getInstance()
    var currentLocale by remember { mutableStateOf(CurrentSession.language) }
    val username = CurrentSession.username

    val options = listOf("English", "Español")
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(if (currentLocale == "en") options[0] else options[1]) }

    LaunchedEffect(Unit) {
        getChatsViewModel.reset()
    }

    LaunchedEffect(getChatsResponse, getChatsCode) {
        if (getChatsResponse != null) {
            navController.navigate(route = AppScreens.LlistaXats.route)
            getChatsViewModel.reset()
            CurrentSession.isAdmin()
        }
        else if (getChatsCode == 403) {
            navController.navigate(route = AppScreens.Xat.route)
            getChatsViewModel.reset()
        }
        else showGetChatsErrorDialog = true
    }

    if (showDeleteErrorDialog) {
        var message = getString(context, R.string.unexpectedErrorLoadingChat, currentLocale)
        popUpError(message, onClick = {
            showGetChatsErrorDialog = false
        })
    }

    // Contenidor principal
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // SECTION: Perfil (Avatar, Nom, Correu)
        ProfileHeader(
            username = username,
            email = "exampleaddress@gmail.com",
            avatarRes = R.drawable.ic_launcher_foreground
        )

        Spacer(modifier = Modifier.height(24.dp))

        // SECTION: "ACCOUNT"
        Text(
            text = getString(context, R.string.account, currentLocale),
            fontSize = 14.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .border(
                    width = 2.dp,
                    color = Color.LightGray,
                    shape = RoundedCornerShape(16.dp)
                ),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column {
                SettingsButton(
                    icon = Icons.Default.Person,
                    text = getString(context, R.string.changeUsername, currentLocale),
                    onClick = {
                        // Pantalla Canvi de username
                    }
                )
                Divider(color = Color.LightGray)
                SettingsButton(
                    icon = Icons.Default.Key,
                    text = getString(context, R.string.changeThePassword, currentLocale),
                    onClick = {
                        navController.navigate(AppScreens.CanviContrasenya.route)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // SECTION: "SETTINGS"
        Text(
            text = getString(context, R.string.settings, currentLocale),
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = Color.Black,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .border(
                    width = 2.dp,
                    color = Color.LightGray,
                    shape = RoundedCornerShape(16.dp)
                ),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = {})
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = getString(context, R.string.currentLanguage, currentLocale),
                            color = Color.Gray
                        )
                    }
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        TextField(
                            value = selectedOption,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .padding(10.dp, 0.dp)
                                .width(200.dp)
                                .height(54.dp)
                                .border(
                                    width = 1.dp,
                                    color = Color.LightGray,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .background(Color.White, shape = RoundedCornerShape(8.dp)),
                            colors = ExposedDropdownMenuDefaults.textFieldColors(
                                containerColor = Color.White,
                                textColor = Color.Black,
                                unfocusedIndicatorColor = Color.LightGray,
                                focusedIndicatorColor = Color.LightGray,
                                disabledIndicatorColor = Color.Transparent
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.background(color = GrisMoltFluix)
                        ) {
                            options.forEach { selectionOption ->
                                DropdownMenuItem(
                                    text = { Text(selectionOption, color = Color.Black) },
                                    onClick = {
                                        selectedOption = selectionOption
                                        expanded = false
                                        if (selectionOption == "English") CurrentSession.changeLanguage("en") else CurrentSession.changeLanguage("es")
                                        CurrentSession.getInstance()
                                        currentLocale = CurrentSession.language
                                    },
                                    modifier = Modifier.background(GrisMoltFluix)
                                )
                            }
                        }
                    }
                }
                Divider(color = Color.LightGray)
                SettingsButton(
                    icon = Icons.Default.Help,
                    text = getString(context, R.string.helpNSupport, currentLocale),
                    onClick = {
                        getChatsViewModel.getChats()
                        Log.d("Codi getChats:", "$getChatsCode")
                        val token = CurrentSession.token
                        Log.d("Token actual:", "$token")
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // SEPARADOR
        Divider(modifier = Modifier.padding(vertical = 8.dp))

        // BOTÓ "LOG OUT"
        Text(
            text = getString(context, R.string.logout, currentLocale),
            fontWeight = FontWeight.Bold,
            color = Color.Red,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showLogoutDialog = true }
                .padding(8.dp)
        )

        // BOTÓ "DELETE ACCOUNT"
        Text(
            text = getString(context, R.string.deleteAccount, currentLocale),
            fontWeight = FontWeight.Medium,
            color = Color.Gray,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDeleteDialog = true }
                .padding(8.dp)
        )
    }

    // DIALOG: Confirmació Logout
    if (showLogoutDialog) {
        popUpDialog(
            getString(context, R.string.sureLogout, currentLocale),
            onConfirm = {navController.navigate(AppScreens.IniciSessio.route)},
            onDismiss = {showLogoutDialog = false}
        )
    }

    LaunchedEffect(deleteCode) {
        if (deleteCode == 204) {
            navController.navigate(AppScreens.IniciSessio.route)
        }
        else if (deleteCode != null) {
            showDeleteErrorDialog = true
        }
    }

    // DIALOG: Confirmació Eliminar compte
    if (showDeleteDialog) {
        popUpDialog(
            getString(context, R.string.sureDeleteAccount, currentLocale),
            onConfirm = {
                deleteAccountViewModel.deleteAccount()
                showDeleteDialog = false
            },
            onDismiss = { showDeleteDialog = false }
        )
    }

    if (showDeleteErrorDialog) {
        var message = getString(context, R.string.deleteErrorNoAuth, currentLocale)
        if (deleteCode != 401) getString(context, R.string.deleteError, currentLocale)
        popUpError(message, onClick = {
            showDeleteErrorDialog = false
        })
    }
}


@Composable
fun ProfileHeader(
    username: String,
    email: String,
    avatarRes: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar rodó
        Icon(
            painter = painterResource(id = avatarRes),
            contentDescription = "Avatar",
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(Color.LightGray),
            tint = Color.White
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Info d'usuari
        Column {
            Text(
                text = username,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.Black
            )
            Text(
                text = email,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}

/**
 * Un botó de configuració reutilitzable que mostra un icon, text i una fletxa.
 */
@Composable
fun SettingsButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                color = Color.Gray
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Color.LightGray
        )
    }
}

@Composable
fun popUpDialog(title: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    val context = LocalContext.current
    CurrentSession.getInstance()
    var currentLocale by remember { mutableStateOf(CurrentSession.language) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Morat)
            ) {
                Text(getString(context, R.string.accept, currentLocale))
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Morat)
            ) {
                Text(getString(context, R.string.cancel, currentLocale))
            }
        },
        containerColor = Color.White
    )
}

@Composable
fun popUpError(text: String, onClick: () -> Unit) {
    AlertDialog(
        onDismissRequest = onClick,
        title = {
            Text(text)
        },
        confirmButton = {
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = Morat)
            ) {
                Text("OK")
            }
        },
        containerColor = Color.White
    )
}