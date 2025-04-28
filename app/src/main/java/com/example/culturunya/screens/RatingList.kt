package com.example.culturunya.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.culturunya.endpoints.events.RatingViewModel
import com.example.culturunya.endpoints.events.Rating
import com.example.culturunya.ui.theme.*


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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Display error if needed
        error?.let { errorMessage ->
            Text(text = errorMessage, color = Color.Red)
        }
        if (ratings.isEmpty() && error == null) {
            Text("No ratings yet", color = Purple40)
        } else {
            Column(
                modifier = Modifier
                    .background(Color.White)
            ) {
                ratings.forEach { rating ->
                    RatingBox(rating = rating, onRatingClick = { onRatingSelected(rating) })
                }
            }
        }
    }
}