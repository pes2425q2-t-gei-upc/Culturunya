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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
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
import com.example.culturunya.endpoints.ratings.RatingViewModel
import com.example.culturunya.endpoints.ratings.Rating
import com.example.culturunya.endpoints.users.UserInfo
import com.example.culturunya.endpoints.users.UserViewModel
import com.example.culturunya.models.currentSession.CurrentSession
import com.example.culturunya.ui.theme.*
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.TextField
import androidx.compose.ui.Alignment
import com.example.culturunya.models.RatingType


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RatingListScreen(
    eventId: Long,
    onRatingSelected: (Rating) -> Unit, // Use your Rating class
    ratingViewModel: RatingViewModel = viewModel(),
    userViewModel: UserViewModel = viewModel()
) {
    // Collect the error
    val error by ratingViewModel.error.collectAsState()
    // Fetch ratings for the specific event ID
    ratingViewModel.fetchRatingsForEvent(eventId)
    // Collect the ratings
    val ratings by ratingViewModel.ratings.collectAsState()
    val user = UserInfo("test", "test@test.com", "", "", "", "", "", "", "", "", "", 0, 0, 0, 0)
    val date = ""
    var rating_new by remember { mutableStateOf("") }
    var comment_new by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var currentRatingType by remember { mutableStateOf(RatingType.Fun)}
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
                        value = comment_new,
                        onValueChange = { comment_new = it },
                        label = { Text(getString(context, R.string.Comment, currentLocale)) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                        ,
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
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded },
                        modifier = Modifier
                            .width(250.dp)
                            .padding(8.dp)
                            .align(Alignment.CenterHorizontally)
                            .background(Purple40)
                    ) {
                        TextField(
                            // We will modify the text to use a stringResource
                            readOnly = true,
                            value = when (currentRatingType) {
                                RatingType.Fun -> getString(context, R.string.Fun, currentLocale)
                                RatingType.Awesome -> getString(context, R.string.Awesome, currentLocale)
                                RatingType.Mediocre -> getString(context, R.string.Mediocre, currentLocale)
                                RatingType.Bad -> getString(context, R.string.Bad, currentLocale)
                                RatingType.KindaFun -> getString(context, R.string.KindaFun, currentLocale)
                            },
                            onValueChange = {},
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .background(Purple40),
                            colors = androidx . compose . material3 . TextFieldDefaults.textFieldColors(
                                containerColor = Purple40,
                                textColor = Color.White,
                                focusedTrailingIconColor = Color.White,
                                unfocusedTrailingIconColor = Color.White,
                                disabledTrailingIconColor = Color.White,
                                disabledTextColor = Color.White,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent,
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier
                                .exposedDropdownSize()
                                .background(Purple40)
                        ) {
                            DropdownMenuItem(
                                text = { Text(getString(context, R.string.Awesome, currentLocale)) },
                                onClick = {
                                    currentRatingType = RatingType.Awesome
                                    expanded = false
                                    rating_new = "Awesome"
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(getString(context, R.string.Fun, currentLocale)) },
                                onClick = {
                                    currentRatingType = RatingType.Fun
                                    expanded = false
                                    rating_new = "Fun"
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(getString(context, R.string.KindaFun, currentLocale)) },
                                onClick = {
                                    currentRatingType = RatingType.KindaFun
                                    expanded = false
                                    rating_new = "KindaFun"
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(getString(context, R.string.Mediocre, currentLocale)) },
                                onClick = {
                                    currentRatingType = RatingType.Mediocre
                                    expanded = false
                                    rating_new = "Mediocre"
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(getString(context, R.string.Bad, currentLocale)) },
                                onClick = {
                                    currentRatingType = RatingType.Bad
                                    expanded = false
                                    rating_new = "Bad"
                                }
                            )
                        }
                    }
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
                                ratingViewModel.createRating(
                                    user,
                                    eventId,
                                    date,
                                    rating_new,
                                    comment_new
                                )
                                // Refresh the list

                            }
                        ) {
                            Text(
                                text = getString(context, R.string.send, currentLocale),
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
            //Spacer(modifier = Modifier.padding(5.dp))
            if (ratings.isEmpty() && error == null) {
                    Text("No ratings yet", color = Purple40, modifier = Modifier.padding(8.dp))
            } else {
                ratings.forEach { rating ->
                    RatingBox(rating = rating, onRatingClick = { onRatingSelected(rating) })
                }
            }
        }
    }
}

