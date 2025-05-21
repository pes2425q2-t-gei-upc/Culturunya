package com.example.culturunya.views

import android.widget.Toast
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.example.culturunya.R
import com.example.culturunya.dataclasses.ratings.Rating
import com.example.culturunya.CurrentSession
import com.example.culturunya.dataclasses.ratings.ReportRequest
import com.example.culturunya.ui.theme.*
import com.google.android.gms.maps.model.Circle
import com.example.culturunya.viewmodels.ReportViewModel

@OptIn(UnstableApi::class)
@Composable
fun RatingBox(
    rating: Rating,
    onRatingClick: (Rating) -> Unit,
    reportViewModel: ReportViewModel = viewModel()
){
    val context = LocalContext.current
    CurrentSession.getInstance()
    val currentLocale by remember { mutableStateOf(CurrentSession.language) }

    Log.d("RatingBox", "RatingBox recomposing. Initializing.")

    //val successMessage by reportViewModel.successMessage.collectAsState()
    //val errorMessage by reportViewModel.errorMessage.collectAsState()
    val isLoading by reportViewModel.isLoading.collectAsState()

    /*LaunchedEffect(successMessage, errorMessage, isLoading) {
        Log.d("RatingBox", "States updated: success='${successMessage}', error='${errorMessage}', loading='${isLoading}'") // <-- LOG B
    }*/

    /*LaunchedEffect(successMessage) {
        successMessage?.let { message ->
            Log.d("RatingBox", "SUCCESS LaunchedEffect: Message='${message}'")
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            reportViewModel.clearSuccessMessage() // Clear message after showing
        }
    }*/

    // Show Toast for error message
    /*LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            Log.d("RatingBox", "ERROR LaunchedEffect: Message='${message}'")
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            reportViewModel.clearErrorMessage() // Clear message after showing
        }
    }*/

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(GrisMoltFluix, RoundedCornerShape(8.dp))
    ){
        Column (modifier = Modifier.padding(8.dp)){
            Row(
                modifier = Modifier.fillMaxWidth(),
            ){
                Text(text = rating.user.username + " " + getString(context, R.string.atDate, currentLocale) +" "+ rating.date.toString() + " " + getString(context, R.string.saysComment, currentLocale),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 12.sp,
                        color = Purple40
                    ), modifier = Modifier.padding(top = 8.dp, bottom = 4.dp))
                Spacer(Modifier.weight(1f))
                IconButton(
                    modifier = Modifier
                        .size(35.dp)
                        .background(Color.Red, CircleShape)
                        .clip(CircleShape)
                    ,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.White
                    ),
                    onClick = {
                        Log.d("RatingBox", "ROW INSIDE RatingBox CLICKED!")
                        val rep = ReportRequest(rating_id = rating.id.toInt(), message = "")
                        reportViewModel.reportRating(rep)
                    },
                    enabled = !isLoading
                ){
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_report),
                            contentDescription = stringResource(id = R.string.Report),
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            //Spacer(modifier = Modifier.padding(8.dp))
            Text(text = getString(context, R.string.Rating, currentLocale) + ": " + rating.rating.toString(), color = Purple40)
            Spacer(modifier = Modifier.padding(8.dp))
            rating.comment?.let { Text(text= it, color = Purple40, fontSize = 16.sp)  }
        }

    }
}