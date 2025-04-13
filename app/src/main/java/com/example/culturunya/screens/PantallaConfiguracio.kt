package com.example.culturunya.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import com.example.culturunya.models.currentSession.CurrentSession
import com.example.culturunya.models.currentSession.CurrentSession.Companion.language
import com.example.culturunya.navigation.AppScreens
import com.example.culturunya.ui.theme.Morat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val deleteAccountViewModel: DeleteAccountViewModel = viewModel()

    val context = LocalContext.current
    CurrentSession.getInstance()
    var currentLocale by remember { mutableStateOf(CurrentSession.language) }

    val options = listOf("English", "Español")
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(if (currentLocale == "en") options[0] else options[1]) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        ProfileHeader(
            username = "Username",
            email = "exampleaddress@gmail.com",
            avatarRes = R.drawable.ic_launcher_foreground
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = getString(context, R.string.account, currentLocale),
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
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
                Divider()
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
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
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
                        onExpandedChange = { expanded = !expanded },
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
                                .background(Color.White)
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            options.forEach { selectionOption ->
                                DropdownMenuItem(
                                    text = { Text(selectionOption) },
                                    onClick = {
                                        selectedOption = selectionOption
                                        expanded = false
                                        if (selectionOption == "English") CurrentSession.changeLanguage("en") else CurrentSession.changeLanguage("es")
                                        CurrentSession.getInstance()
                                        currentLocale = CurrentSession.language
                                    },
                                    modifier = Modifier.background(Color.LightGray)
                                )
                            }
                        }
                    }
                }
                Divider()
                SettingsButton(
                    icon = Icons.Default.Help,
                    text = getString(context, R.string.helpNSupport, currentLocale),
                    onClick = {
                        // Navega a la secció d'ajuda
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        Text(
            text = getString(context, R.string.logout, currentLocale),
            fontWeight = FontWeight.Bold,
            color = Color.Red,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showLogoutDialog = true }
                .padding(8.dp)
        )

        Text(
            text = getString(context, R.string.deleteAccount, currentLocale),
            fontWeight = FontWeight.Medium,
            color = Color.Gray,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {

                    showDeleteDialog = true
                }
                .padding(8.dp)
        )
    }

    // DIALOG: Confirmació Logout
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text(getString(context, R.string.sureLogout, currentLocale)) },
            confirmButton = {
                Button(
                    onClick = {
                        navController.navigate(AppScreens.IniciSessio.route)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Morat)
                ) {
                    Text(getString(context, R.string.accept, currentLocale))
                }
            },
            dismissButton = {
                Button(
                    onClick = { showLogoutDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Morat)
                ) {
                    Text(getString(context, R.string.cancel, currentLocale))
                }
            },
            containerColor = Color.White
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(getString(context, R.string.sureDeleteAccount, currentLocale)) },
            confirmButton = {
                Button(
                    onClick = {
                        deleteAccountViewModel.deleteAccount()
                        navController.navigate(AppScreens.IniciSessio.route)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Morat)
                ) {
                    Text(getString(context, R.string.accept, currentLocale))
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDeleteDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Morat)
                ) {
                    Text(getString(context, R.string.cancel, currentLocale))
                }
            },
            containerColor = Color.White
        )
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
            tint = Color.White // Canvia'l si el recurs és un vector
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Info d'usuari
        Column {
            Text(
                text = username,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Text(
                text = email,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}


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
