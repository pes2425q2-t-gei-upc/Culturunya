package com.example.culturunya.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.culturunya.R
import com.example.culturunya.endpoints.events.RatingViewModel
import com.example.culturunya.endpoints.events.Rating
import com.example.culturunya.models.currentSession.CurrentSession
import com.example.culturunya.ui.theme.*


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RatingListScreen(
    eventId: Long,
    onRatingSelected: (Rating) -> Unit, // Use your Rating class
    ratingViewModel: RatingViewModel = viewModel()
) {
    // Collect the error
    val error by ratingViewModel.error.collectAsState()
    // Fetch ratings for the specific event ID
    ratingViewModel.fetchRatingsForEvent(eventId)
    // Collect the ratings
    val ratings by ratingViewModel.ratings.collectAsState()
    var rating_new by remember { mutableStateOf("") }
    val context = LocalContext.current
    CurrentSession.getInstance()
    val currentLocale by remember { mutableStateOf(CurrentSession.language) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Column(
                modifier = Modifier
                    .background(Color.White)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Column(modifier= Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = rating_new,
                        onValueChange = { rating_new = it },
                        label = { Text(getString(context, R.string.Comment, currentLocale)) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
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
                    Spacer(modifier = Modifier.padding(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            modifier = Modifier.width(200.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Purple40,
                                contentColor = Color.White
                            ),
                            onClick = {
                                // logica Comentar
                            }
                        ) {
                            Text(
                                text = "Comentar",
                                modifier = Modifier.padding(2.dp),
                                color = Color.White
                            )
                        }
                    }
                }
            }
            Divider(modifier = Modifier.padding(vertical = 8.dp))
                // Display error if needed
            error?.let { errorMessage ->
                Text(text = errorMessage, Modifier.padding(4.dp), color = Color.Red)
            }
            if (ratings.isEmpty() && error == null) {
                    Text("No ratings yet", color = Purple40)
            } else {
                ratings.forEach { rating ->
                    RatingBox(rating = rating, onRatingClick = { onRatingSelected(rating) })
                }
            }
        }
    }
}