package com.example.culturunya.views

import SessionManager
import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.modifier.modifierLocalMapOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.culturunya.R
import com.example.culturunya.viewmodels.DeleteAccountViewModel
import com.example.culturunya.viewmodels.GetChatsViewModel
import com.example.culturunya.viewmodels.LogoutViewModel
import com.example.culturunya.viewmodels.UpdateLanguageViewModel
import com.example.culturunya.viewmodels.UserViewModel
import com.example.culturunya.session.CurrentSession
import com.example.culturunya.navigation.AppScreens
import com.example.culturunya.screens.RankIcon
import com.example.culturunya.ui.theme.GrisMoltFluix
import com.example.culturunya.ui.theme.Morat
import com.example.culturunya.viewmodels.AuthViewModel
import com.example.culturunya.viewmodels.ReportViewModel
import com.example.culturunya.viewmodels.*
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
/**
 * Pantalla de configuració de l'usuari.
 * Permet canviar el nom d'usuari, la contrasenya, la foto de perfil i la llengua de l'aplicació.
 *
 * @param navController Controlador de navegació per gestionar la navegació entre pantalles.
 */
fun SettingsScreen(navController: NavController) {
    // Control de diàlegs
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val deleteAccountViewModel: DeleteAccountViewModel = viewModel()
    val deleteCode by deleteAccountViewModel.deleteAccountStatus.collectAsState()
    var showDeleteErrorDialog by remember { mutableStateOf(false) }

    val reportViewModel: ReportViewModel = viewModel()
    val reportResponse by reportViewModel.reports.collectAsState()
    val reportCode by reportViewModel.errorCode.collectAsState()
    var showReportErrorDialog by remember { mutableStateOf(false) }

    val updateLanguageViewModel: UpdateLanguageViewModel = viewModel()
    val updateLanguageCode by updateLanguageViewModel.updateLanguageStatus.collectAsState()
    var showUpdateLanguageErrorDialog by remember { mutableStateOf(false) }

    val logoutViewModel: LogoutViewModel = viewModel()
    val logoutCode by logoutViewModel.logoutStatus.collectAsState()
    var showLogoutErrorDialog by remember { mutableStateOf(false) }

    val getChatsViewModel: GetChatsViewModel = viewModel()
    val getChatsResponse = getChatsViewModel.getChatsResponse.collectAsState().value
    val getChatsCode = getChatsViewModel.getChatsError.collectAsState().value
    var showGetChatsErrorDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    CurrentSession.getInstance()
    var currentLocale by remember { mutableStateOf(CurrentSession.language) }
    val username = CurrentSession.username
    val email = CurrentSession.email
    val imageUrl = CurrentSession.profile_pic
    val rank_quiz = CurrentSession.rank_quiz
    val rank_event = CurrentSession.rank_event
    val total_quiz_points = CurrentSession.total_quiz_points
    val total_event_points = CurrentSession.total_event_points

    val options = listOf("English", "Español")
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(if (currentLocale == "en" || currentLocale == "EN") options[0] else options[1]) }

    val userViewModel: UserViewModel = viewModel()

    val authViewModel: AuthViewModel = viewModel()
    val sessionManager = remember { SessionManager(context) }
    var showProfileRanks by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

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

    LaunchedEffect(reportResponse, reportCode) {
        if (reportResponse.isNotEmpty()) {
            navController.navigate(route = AppScreens.ListReports.route)
            reportViewModel.reset()
        }
        else if (reportCode == 403) {
            navController.navigate(route = AppScreens.Reports.route)
            reportViewModel.reset()
        }
        else if( reportCode == null){
            //
        }
        else showReportErrorDialog = true
    }

    if (showReportErrorDialog) {
        var message = getString(context, R.string.unexpectedErrorLoadingReports, currentLocale)
        if (reportCode == 400) message = getString(context, R.string.notAValidLanguage, currentLocale)
        else if (reportCode == 401) message = getString(context, R.string.unauthenticated, currentLocale)
        else if (reportCode == 500) getString(context, R.string.serverError, currentLocale)
        popUpError(message, onClick = {
            showReportErrorDialog = false
        })
        reportViewModel.reset()
    }

    if (showUpdateLanguageErrorDialog) {
        var message = getString(context, R.string.unexpectedErrorLanguage, currentLocale)
        if (updateLanguageCode == 400) message = getString(context, R.string.notAValidLanguage, currentLocale)
        else if (updateLanguageCode == 401) message = getString(context, R.string.unauthenticated, currentLocale)
        else if (updateLanguageCode == 500) getString(context, R.string.serverError, currentLocale)
        popUpError(message, onClick = {
            showUpdateLanguageErrorDialog = false
        })
        updateLanguageViewModel.reset()
    }

    // Contenidor principal
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        // SECTION: Perfil (Avatar, Nom, Correu)
        ProfileHeader(
            username = username,
            email = email,
            avatarRes = imageUrl,
            navController = navController
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
                        navController.navigate(AppScreens.ChangeUsername.route)
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
                Divider(color = Color.LightGray)
                SettingsButton(
                    icon = Icons.Default.PhotoCamera,
                    text = getString(context, R.string.changeProfilePic, currentLocale),
                    onClick = {
                        navController.navigate(AppScreens.ChangeProfilePic.route)
                    }
                )
                Divider(color = Color.LightGray)
                SettingsButton(
                    icon = Icons.Default.BarChart,
                    text = getString(context, R.string.monthlyClassification, currentLocale),
                    onClick = {
                        showProfileRanks = true
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
                                        updateLanguageViewModel.updateLanguage(if (selectedOption == "Español") "ES" else "EN")
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
                    }
                )
                Log.d("Admin", "El usuario tiene admin en: ${CurrentSession.is_admin}")
                if(CurrentSession.is_admin) {
                    Divider(color = Color.LightGray)
                    SettingsButton(
                        icon = Icons.Default.Dangerous,
                        text = getString(context, R.string.Reports, currentLocale),
                        onClick = {
                            reportViewModel.getReports()
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // SEPARADOR
        Divider(modifier = Modifier.padding(vertical = 6.dp))

        // BOTÓ "LOG OUT"
        Text(
            text = getString(context, R.string.logout, currentLocale),
            fontWeight = FontWeight.Bold,
            color = Color.Red,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showLogoutDialog = true }
                .padding(4.dp)
        )

        // BOTÓ "DELETE ACCOUNT"
        Text(
            text = getString(context, R.string.deleteAccount, currentLocale),
            fontWeight = FontWeight.Medium,
            color = Color.Gray,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDeleteDialog = true }
                .padding(4.dp)
        )
    }

    // DIALOG: Confirmació Logout
    if (showLogoutDialog) {
        popUpDialog(
            getString(context, R.string.sureLogout, currentLocale),
            onConfirm = {logoutViewModel.logout()},
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

    LaunchedEffect(updateLanguageCode) {
        if (updateLanguageCode == 200) {
            if (selectedOption == "English") CurrentSession.changeLanguage("EN")
            else CurrentSession.changeLanguage("ES")
            CurrentSession.getInstance()
            currentLocale = CurrentSession.language
            updateLanguageViewModel.reset()
        }
        else if (updateLanguageCode != null) showUpdateLanguageErrorDialog = true
    }

    LaunchedEffect(logoutCode) {
        if (logoutCode == 200) {
            authViewModel.logout(sessionManager)
            navController.navigate(AppScreens.IniciSessio.route)
        }
        else if (logoutCode != null) {
            showLogoutErrorDialog = true
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
        val message = getString(context, R.string.deleteErrorNoAuth, currentLocale)
        if (deleteCode != 401) getString(context, R.string.deleteError, currentLocale)
        popUpError(message, onClick = {
            showDeleteErrorDialog = false
        })
    }

    if (showLogoutErrorDialog) {
        val message = getString(context, R.string.logoutUnexpectedError, currentLocale)
        if (logoutCode == 400) getString(context, R.string.logoutUnexpectedError, currentLocale)
        popUpError(message, onClick = {
            showLogoutErrorDialog = false
        })
    }

    if (showProfileRanks) {
        AlertDialog(
            onDismissRequest = { showProfileRanks = false },
            text = {
                Row {
                    Column {
                        Text(
                            text = getString(context, R.string.quiz, currentLocale),
                            fontSize = 20.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer (modifier = Modifier.height(15.dp))
                        RankIcon(rank_quiz, modifier = Modifier.align(Alignment.CenterHorizontally))
                        Spacer (modifier = Modifier.height(10.dp))
                        Text(rank_quiz, modifier = Modifier.align(Alignment.CenterHorizontally))
                        Spacer (modifier = Modifier.height(10.dp))
                        Text(text = "$total_quiz_points" + " pts", modifier = Modifier.align(Alignment.CenterHorizontally))
                    }
                    Spacer (modifier = Modifier.width(30.dp))
                    Column {
                        Text(
                            text = getString(context, R.string.eventAssistance, currentLocale),
                            fontSize = 20.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer (modifier = Modifier.height(15.dp))
                        RankIcon(rank_event, modifier = Modifier.align(Alignment.CenterHorizontally))
                        Spacer (modifier = Modifier.height(10.dp))
                        Text(rank_event, modifier = Modifier.align(Alignment.CenterHorizontally))
                        Spacer (modifier = Modifier.height(10.dp))
                        Text(text = "$total_event_points" + " pts", modifier = Modifier.align(Alignment.CenterHorizontally))
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showProfileRanks = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Morat)
                ) {
                    Text(text = "OK")
                }
            },
            containerColor = Color.White
        )
    }

}


@SuppressLint("ResourceType")
@Composable
/**
 * Header de perfil que mostra la imatge d'usuari, el nom i el correu electrònic.
 *
 * @param username Nom d'usuari a mostrar.
 * @param email Correu electrònic a mostrar.
 * @param avatarRes URL de la imatge d'usuari.
 * @param navController Controlador de navegació per gestionar la navegació entre pantalles.
 */
fun ProfileHeader(
    username: String,
    email: String,
    avatarRes: String,
    navController: NavController
) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar rodó
        if (avatarRes != null && avatarRes != "") {
            val baseUrl = "http://nattech.fib.upc.edu:40369"
            val urlFinal = baseUrl + avatarRes
            AsyncImage(
                model = urlFinal,
                contentDescription = "Perfil Image",
                modifier = Modifier
                    .clip(CircleShape)
                    .size(60.dp)
                    .clickable { navController.navigate(AppScreens.ChangeProfilePic.route) },
            )
        }
        else {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Perfil default",
                tint = Morat,
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .clickable { navController.navigate(AppScreens.ChangeProfilePic.route) }
            )
        }

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

        Spacer(modifier = Modifier.weight(1f))

        IconButton(
            onClick = {
                // Crear un fitxer temporal per la imatge
                val imageFile = File(context.cacheDir, "logo_share.png")
                context.resources.openRawResource(R.drawable.logo_retallat).use { input ->
                    FileOutputStream(imageFile).use { output ->
                        input.copyTo(output)
                    }
                }

                // Crear l'URI de la imatge
                val imageUri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    imageFile
                )

                // Crear l'Intent per compartir
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND_MULTIPLE
                    type = "image/*"
                    putExtra(Intent.EXTRA_TEXT, context.getString(R.string.shareMessage))
                    putParcelableArrayListExtra(Intent.EXTRA_STREAM, arrayListOf(imageUri))
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

                context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.shareButton)))
            },
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "Share",
                tint = Morat
            )
        }
    }
}

/**
 * Botó de configuració amb una icona i text.
 *
 * @param icon Icona a mostrar.
 * @param text Text a mostrar.
 * @param onClick Funció a executar quan es fa clic al botó.
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
/**
 * Diàleg de confirmació per a accions importants.
 *
 * @param title Títol del diàleg.
 * @param onConfirm Funció a executar quan es confirma l'acció.
 * @param onDismiss Funció a executar quan es tanca el diàleg.
 */
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
/**
 * Diàleg d'error per mostrar missatges d'error.
 *
 * @param text Text a mostrar al diàleg.
 * @param onClick Funció a executar quan es fa clic al botó "OK".
 */
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