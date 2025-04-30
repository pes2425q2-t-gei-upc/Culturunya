package com.example.culturunya.screens

import android.content.Context
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
import androidx.navigation.NavController
import com.example.culturunya.R
import com.example.culturunya.navigation.AppScreens
import com.example.culturunya.ui.theme.Morat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    // Control de diàlegs
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Contenidor principal
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // SECTION: Perfil (Avatar, Nom, Correu)
        ProfileHeader(
            username = "Username",
            email = "exampleaddress@gmail.com",
            avatarRes = R.drawable.ic_launcher_foreground  // Canviar pel recurs d'imatge
        )

        Spacer(modifier = Modifier.height(24.dp))

        // SECTION: "ACCOUNT"
        Text(
            text = "ACCOUNT",
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
                    text = "Change username",  // o getString(context, R.string.change_username, currentLocale)
                    onClick = {
                        // Navega al teu canvi de nom d'usuari, per exemple:
                        //navController.navigate(AppScreens.CanviUsuari.route)
                    }
                )
                Divider()
                SettingsButton(
                    icon = Icons.Default.Key,
                    text = "Change password",  // o getString(context, R.string.changePassword, currentLocale)
                    onClick = {
                        navController.navigate(AppScreens.CanviContrasenya.route)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // SECTION: "SETTINGS"
        Text(
            text = "SETTINGS",
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
                SettingsButton(
                    icon = Icons.Default.Language,
                    text = "Language",  // o getString(context, R.string.language, currentLocale)
                    onClick = {
                        // Navega o mostra un diàleg per canviar l'idioma
                    }
                )
                Divider()
                SettingsButton(
                    icon = Icons.Default.Help,
                    text = "Help & support", // o getString(context, R.string.helpSupport, currentLocale)
                    onClick = {
                        // Navega a la secció d'ajuda
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // SEPARADOR
        Divider(modifier = Modifier.padding(vertical = 8.dp))

        // BOTÓ "LOG OUT"
        Text(
            text = "Log out",  // o getString(context, R.string.logout, currentLocale)
            fontWeight = FontWeight.Bold,
            color = Color.Red,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showLogoutDialog = true }
                .padding(8.dp)
        )

        // BOTÓ "DELETE ACCOUNT"
        Text(
            text = "Delete account",  // o getString(context, R.string.deleteAccount, currentLocale)
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
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Are you sure you want to log out?") },
            confirmButton = {
                Button(
                    onClick = {
                        // Aquí crides la funció de logout:
                        // if (ferLogout()) ...
                        navController.navigate(AppScreens.IniciSessio.route)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Morat)
                ) {
                    Text("Accept")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showLogoutDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Morat)
                ) {
                    Text("Cancel")
                }
            },
            containerColor = Color.White
        )
    }

    // DIALOG: Confirmació Eliminar compte
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Are you sure you want to delete your account?") },
            confirmButton = {
                Button(
                    onClick = {
                        // Aquí crides la funció d'esborrar compte:
                        // if (esborrarCompte()) ...
                        navController.navigate(AppScreens.IniciSessio.route)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Morat)
                ) {
                    Text("Accept")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDeleteDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Morat)
                ) {
                    Text("Cancel")
                }
            },
            containerColor = Color.White
        )
    }
}

/**
 * Mostra la capçalera amb l'avatar circular, el nom d'usuari i el correu.
 */
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

/**
 * Exemple de funció per obtenir string internacionalitzat,
 * si ho vols integrar amb strings.xml i locals.
 */
fun getLocalizedString(context: Context, resId: Int, locale: String): String {
    return context.getString(resId)
}

