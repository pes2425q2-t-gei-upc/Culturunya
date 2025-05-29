package com.example.culturunya.views

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.culturunya.R
import com.example.culturunya.viewmodels.ChangeProfilePicViewModel
import com.example.culturunya.session.CurrentSession
import com.example.culturunya.ui.theme.Morat
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCanviFotoPerfil(navController: NavController) {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var missatgeError by remember { mutableStateOf("") }
    var haCanviat by remember { mutableStateOf(false) }
    val context = LocalContext.current
    CurrentSession.getInstance()
    val currentLocale = CurrentSession.language

    val viewModel: ChangeProfilePicViewModel = viewModel()
    val state by viewModel.state.collectAsState()

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            viewModel.uploadProfilePic(it)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.setContext(context)
    }

    LaunchedEffect(state.success) {
        if (state.success) {
            haCanviat = true
        }
    }

    LaunchedEffect(state.error) {
        if (state.error != null) {
            missatgeError = state.error.toString()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        IconButton(onClick = {
            navController.popBackStack()
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
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .background(Morat.copy(alpha = 0.1f))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                if (selectedImageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(selectedImageUri),
                        contentDescription = "Profile picture",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    val currentProfilePic = CurrentSession.profile_pic
                    if (currentProfilePic != null && currentProfilePic.isNotEmpty()) {
                        val baseUrl = "http://nattech.fib.upc.edu:40369"
                        val urlFinal = baseUrl + currentProfilePic
                        AsyncImage(
                            model = urlFinal,
                            contentDescription = "Current profile picture",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile picture",
                            modifier = Modifier.size(100.dp),
                            tint = Morat
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = com.example.culturunya.views.functions.getString(
                    context,
                    R.string.changeProfilePic,
                    currentLocale
                ),
                fontSize = 24.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (missatgeError.isNotEmpty()) {
                Text(
                    text = missatgeError,
                    color = Color.Red,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

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
                            Text(com.example.culturunya.views.functions.getString(context, R.string.ok, currentLocale))
                        }
                    },
                    title = { Text(
                        com.example.culturunya.views.functions.getString(
                            context,
                            R.string.profilePicUpdated,
                            currentLocale
                        )
                    ) },
                    containerColor = Color.White
                )
            }

            Button(
                onClick = { imagePicker.launch("image/*") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Morat),
                enabled = !state.isLoading
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(com.example.culturunya.views.functions.getString(context, R.string.selectImage, currentLocale))
                }
            }
        }
    }
} 