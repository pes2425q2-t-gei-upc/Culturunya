package com.example.culturunya.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.culturunya.endpoints.events.Rating
import com.example.culturunya.models.currentSession.CurrentSession
import com.example.culturunya.ui.theme.PurpleGrey80

@Composable
fun RatingBox(
    rating: Rating,
    onRatingClick: (Rating) -> Unit
){
    val context = LocalContext.current
    CurrentSession.getInstance()
    val currentLocale by remember { mutableStateOf(CurrentSession.language) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(PurpleGrey80, RoundedCornerShape(8.dp))
    ){
        Column {
            Text(text = rating.date.toString())
            Spacer(modifier = Modifier.padding(8.dp))
            rating.comment?.let { Text(text= it) }
            Spacer(modifier = Modifier.padding(8.dp))
            Text(text = rating.rating.toString())
        }

    }
}