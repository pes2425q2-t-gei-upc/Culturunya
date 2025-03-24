package com.example.culturunya.screens

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import java.util.Locale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.culturunya.R
import com.example.culturunya.navigation.AppScreens
import com.example.culturunya.ui.theme.CulturunyaTheme
import com.example.culturunya.ui.theme.Morat
import com.example.culturunya.controllers.comprovaNomContrasenya
import com.example.culturunya.controllers.esborrarCompte
import com.example.culturunya.controllers.ferLogout

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    var ferLogout by remember { mutableStateOf(false) }
    var eliminarCompte by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var currentLocale by remember { mutableStateOf(Locale.getDefault().language) }

    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
    ) {
        SettingsButton(
            icon = Icons.Default.Key,
            text = getString(context, R.string.changePassword, currentLocale),
            onClick = { navController.navigate(AppScreens.CanviContrasenya.route) }
        )

        Text(
            text = getString(context, R.string.logout, currentLocale),
            color = Color.Red,
            modifier = Modifier.clickable { ferLogout = true }
        )

        Text(
            text = getString(context, R.string.deleteAccount, currentLocale),
            color = Color.Gray,
            modifier = Modifier.clickable { eliminarCompte = true }
        )
    }

    if (ferLogout) {
        AlertDialog(
            onDismissRequest = { ferLogout = false },
            confirmButton = {
                Button(
                    onClick = {
                        if (ferLogout()) navController.navigate(AppScreens.IniciSessio.route)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Morat)
                ) {
                    Text(getString(context, R.string.accept, currentLocale))
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        ferLogout = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Morat)
                ) {
                    Text(getString(context, R.string.cancel, currentLocale))
                }
            },
            title = { Text(getString(context, R.string.sureLogout, currentLocale)) },
            containerColor = Color.White
        )
    }

    if (eliminarCompte) {
        AlertDialog(
            onDismissRequest = { eliminarCompte = false },
            confirmButton = {
                Button(
                    onClick = {
                        if (esborrarCompte()) navController.navigate(AppScreens.IniciSessio.route)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Morat)
                ) {
                    Text(getString(context, R.string.accept, currentLocale))
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        eliminarCompte = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Morat)
                ) {
                    Text(getString(context, R.string.cancel, currentLocale))
                }
            },
            title = { Text(getString(context, R.string.sureDeleteAccount, currentLocale)) },
            containerColor = Color.White
        )
    }
}

@Composable
private fun SettingsButton(
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