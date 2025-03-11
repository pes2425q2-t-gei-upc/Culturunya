package com.example.culturunya

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.example.culturunya.R
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.culturunya.ui.theme.CulturunyaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComposablePrincipal() {
    var usuario by remember { mutableStateOf("") }
    var contrasenya by remember { mutableStateOf("") }
    var missatgeError by remember { mutableStateOf("") }

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
            /*Text(
                text = "Culturunya",
                fontSize = 40.sp,
                color = Color.Black
            )*/

            Image (
                painter = painterResource(id = R.drawable.logo_retallat),
                contentDescription = "Logo retallat"
            )

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Iniciar Sessió",
                fontSize = 24.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = usuario,
                onValueChange = { usuario = it },
                label = { Text("Nom d'usuari") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = contrasenya,
                onValueChange = { contrasenya = it },
                label = { Text("Contrasenya") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
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
                    if (usuario.isEmpty() || contrasenya.isEmpty()) {
                        missatgeError = "Indiqui el nom d'usuari i la contrasenya"
                    }
                    else {

                    }
                },
                modifier = Modifier.width(250.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
            ) {
                Text(text = "Ingresar")
            }

            OutlinedButton(
                onClick = {  },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                modifier = Modifier.padding(16.dp)) {
                Image(painter = painterResource(id = R.drawable.logo_google), contentDescription = "logo de google")
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = "Accedir amb Google", color = Color.Black)
            }

            Divider(
                color = Color.Gray,
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 25.dp)
            )

            Row (
                verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Encara no tens compte? ",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = "Registra't",
                    fontSize = 14.sp,
                    color = Color.Blue,
                    modifier = Modifier.clickable {  }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CulturunyaTheme {


    }
}